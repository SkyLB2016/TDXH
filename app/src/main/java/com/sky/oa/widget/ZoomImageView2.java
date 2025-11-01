package com.sky.oa.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 优化版 ZoomImageView
 * 支持：双击放大/缩小、双指缩放、拖拽移动、边界阻尼、与 ViewPager 和谐共存
 */
public class ZoomImageView2 extends AppCompatImageView
        implements ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener {

    // === 缩放等级 ===
    private float minScale = 0.8f;   // 最小缩放（可整体缩小）
    private float midScale = 1.0f;   // 中等缩放（原始尺寸）
    private float maxScale = 3.0f;   // 最大缩放

    // === 手势检测器 ===
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;

    // === 图像变换矩阵 ===
    private final Matrix matrix = new Matrix();
    private final float[] matrixValues = new float[9];

    // === 状态标志 ===
    private boolean isInitialized = false;        // 是否已完成初始化布局
    private boolean isScaling = false;            // 是否正在缩放
    private boolean isDragging = false;           // 是否正在拖拽
    private boolean isAutoScaling = false;        // 是否在执行双击动画

    // === 触控参数 ===
    private int touchSlop;
    private float lastX, lastY;                   // 上次触点中心位置
    private int lastPointerCount = 0;             // 上次手指数量

    // === 双击动画参数 ===
    private long doubleTapAnimStartTime;
    private float doubleTapStartScale;
    private float doubleTapTargetScale;
    private float doubleTapStartX, doubleTapStartY;
    private float doubleTapTargetX, doubleTapTargetY;

    // === 惯性滑动 ===
    private OverScroller scroller;

    public ZoomImageView2(Context context) {
        this(context, null);
    }

    public ZoomImageView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setScaleType(ScaleType.MATRIX);
        matrix.reset();

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // 初始化手势检测器
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScaling || isScaling) return true;

                float x = e.getX();
                float y = e.getY();
                float scale = getScale();

                if (scale < midScale) {
                    // 小于正常尺寸 → 放大到 midScale
                    startScaleAnimation(midScale, x, y);
                } else if (scale < maxScale) {
                    // 小于最大尺寸 → 放大到 maxScale
                    startScaleAnimation(maxScale, x, y);
                } else {
                    // 超过正常尺寸 → 缩小到 minScale
                    startScaleAnimation(minScale, x, y);
                }
                return true;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);

        setOnTouchListener(this);

        scroller = new OverScroller(context);
    }

    // -------------------------------
    // 生命周期：监听布局完成
    // -------------------------------

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        removeCallbacks(autoScaleRunnable);
    }

    @Override
    public void onGlobalLayout() {
        if (isInitialized || getDrawable() == null) return;

        int viewWidth = getWidth();
        int viewHeight = getHeight();
        Drawable drawable = getDrawable();
        int dw = drawable.getIntrinsicWidth();
        int dh = drawable.getIntrinsicHeight();

        if (dw == 0 || dh == 0) return;

        float scale = 1.0f;
        if (dw > viewWidth || dh > viewHeight) {
            scale = Math.min(
                    (float) viewWidth / dw,
                    (float) viewHeight / dh
            );
        } else {
            scale = Math.min(
                    (float) viewWidth / dw,
                    (float) viewHeight / dh
            ); // 允许留白
        }

        minScale = scale;
        midScale = minScale * 2f;
        maxScale = minScale * 4f;

        // 居中显示
        float dx = (viewWidth - dw * scale) / 2f;
        float dy = (viewHeight - dh * scale) / 2f;

        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);

        isInitialized = true;
    }

    // -------------------------------
    // 手势监听：onTouch 分发
    // -------------------------------

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 1. 优先处理双击
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        // 2. 缩放手势优先级高于拖拽
        boolean result = scaleGestureDetector.onTouchEvent(event);
        if (scaleGestureDetector.isInProgress()) {
            isScaling = true;
            isDragging = false;
            return true;
        }

        // 3. 处理拖拽
        handleDrag(event);
        return true;
    }

    private void handleDrag(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        float x = getPointerCenterX(event);
        float y = getPointerCenterY(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                isDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = x - lastX;
                float dy = y - lastY;

                if (!isDragging) {
                    isDragging = canStartDrag(dx, dy);
                    if (isDragging) {
                        // 告诉父容器不要拦截：我准备自己处理滑动
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }

                if (isDragging && !isScaling) {
                    matrix.postTranslate(dx, dy);
                    checkBoundsAndDamp(); // 阻尼边缘
                    setImageMatrix(matrix);
                }

                lastX = x;
                lastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                flingIfNecessary();
                isScaling = false;
                break;
        }
    }

    // -------------------------------
    // 缩放手势监听
    // -------------------------------

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            isScaling = true;
            removeCallbacks(autoScaleRunnable); // 中断双击动画
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            float currentScale = getScale();
            float newScale = currentScale * scaleFactor;

            // 限制范围 + 阻尼效果
            if (newScale < minScale) {
                scaleFactor = 1f + (scaleFactor - 1f) * (minScale / newScale);
            } else if (newScale > maxScale) {
                scaleFactor = 1f + (scaleFactor - 1f) * (maxScale / newScale);
            }

            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            checkBoundsAndDamp();
            setImageMatrix(matrix);

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            isScaling = false;
            flingIfNecessary();
        }
    }

    // -------------------------------
    // 双击动画：平滑插值缩放
    // -------------------------------

    private final Runnable autoScaleRunnable = new Runnable() {
        private static final long ANIM_DURATION = 200; // 动画时长
        private final android.view.animation.DecelerateInterpolator interpolator
                = new android.view.animation.DecelerateInterpolator();

        @Override
        public void run() {
            if (!isAutoScaling) return;

            long t = System.currentTimeMillis() - doubleTapAnimStartTime;
            float progress = Math.min(t / (float) ANIM_DURATION, 1f);
            float interp = interpolator.getInterpolation(progress);

            float scale = doubleTapStartScale + (doubleTapTargetScale - doubleTapStartScale) * interp;
            float x = doubleTapStartX + (doubleTapTargetX - doubleTapStartX) * interp;
            float y = doubleTapStartY + (doubleTapTargetY - doubleTapStartY) * interp;

            matrix.setScale(scale, scale);
            matrix.postTranslate(x, y);
            checkBoundsAndDamp();
            setImageMatrix(matrix);

            if (progress < 1f) {
                postOnAnimation(autoScaleRunnable);
            } else {
                isAutoScaling = false;
            }
        }
    };

    private void startScaleAnimation(float targetScale, float focusX, float focusY) {
        isAutoScaling = true;
        doubleTapAnimStartTime = System.currentTimeMillis();
        doubleTapStartScale = getScale();
        doubleTapTargetScale = targetScale;

        // 计算缩放后图像的位置偏移
        RectF rect = getDisplayRect();
        if (rect == null) return;

        float currentFocusX = rect.left + focusX;
        float currentFocusY = rect.top + focusY;

        float newFocusX = currentFocusX * (targetScale / doubleTapStartScale);
        float newFocusY = currentFocusY * (targetScale / doubleTapStartScale);

        doubleTapStartX = getMatrixX();
        doubleTapStartY = getMatrixY();
        doubleTapTargetX = doubleTapStartX + (focusX - (newFocusX - rect.left));
        doubleTapTargetY = doubleTapStartY + (focusY - (newFocusY - rect.top));

        removeCallbacks(autoScaleRunnable);
        postOnAnimation(autoScaleRunnable);
    }

    // -------------------------------
    // 边界检查与阻尼回弹
    // -------------------------------

    /**
     * 检查边界并添加阻尼效果（越界越难拖）
     */
    private void checkBoundsAndDamp() {
        RectF rect = getDisplayRect();
        if (rect == null) return;

        float deltaX = 0, deltaY = 0;
        int w = getWidth(), h = getHeight();

        if (rect.width() > w) {
            if (rect.left > 0) deltaX = -rect.left * 0.5f;           // 右拉阻尼
            if (rect.right < w) deltaX = (w - rect.right) * 0.5f;    // 左拉阻尼
        } else {
            deltaX = (w - rect.width()) / 2 - rect.left;             // 居中
        }

        if (rect.height() > h) {
            if (rect.top > 0) deltaY = -rect.top * 0.5f;
            if (rect.bottom < h) deltaY = (h - rect.bottom) * 0.5f;
        } else {
            deltaY = (h - rect.height()) / 2 - rect.top;
        }

        // 轻微阻尼拖拽
        if (Math.abs(deltaX) > 0.1f || Math.abs(deltaY) > 0.1f) {
            matrix.postTranslate(deltaX, deltaY);
        }
    }

    // -------------------------------
    // 惯性滑动（Fling）
    // -------------------------------

    private void flingIfNecessary() {
        if (isAutoScaling || isScaling) return;

        RectF rect = getDisplayRect();
        if (rect == null) return;

        if (rect.width() <= getWidth() && rect.height() <= getHeight()) return;

        float vX = scaleGestureDetector.getPreviousSpanX() - scaleGestureDetector.getCurrentSpanX();
        float vY = scaleGestureDetector.getPreviousSpanY() - scaleGestureDetector.getCurrentSpanY();

        if (Math.abs(vX) < 50 && Math.abs(vY) < 50) return;

        scroller.fling(
                (int) getMatrixX(), (int) getMatrixY(),
                (int) -vX, (int) -vY,
                (int) -(rect.width() * 2), (int) (rect.width() * 2),
                (int) -(rect.height() * 2), (int) (rect.height() * 2)
        );

        postOnAnimation(flingRunnable);
    }

    private final Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                int currX = scroller.getCurrX();
                int currY = scroller.getCurrY();

                matrix.set(getImageMatrix());
                matrix.postTranslate(currX - getMatrixX(), currY - getMatrixY());
                checkBoundsAndDamp();
                setImageMatrix(matrix);

                postOnAnimation(this);
            }
        }
    };

    // -------------------------------
    // 工具方法
    // -------------------------------

    private float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    private float getMatrixX() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_X];
    }

    private float getMatrixY() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MTRANS_Y];
    }

    private RectF getDisplayRect() {
        Drawable d = getDrawable();
        if (d == null) return null;
        RectF rect = new RectF(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        matrix.mapRect(rect);
        return rect;
    }

    private float getPointerCenterX(MotionEvent event) {
        float sum = 0;
        for (int i = 0; i < event.getPointerCount(); i++) {
            sum += event.getX(i);
        }
        return sum / event.getPointerCount();
    }

    private float getPointerCenterY(MotionEvent event) {
        float sum = 0;
        for (int i = 0; i < event.getPointerCount(); i++) {
            sum += event.getY(i);
        }
        return sum / event.getPointerCount();
    }

    private boolean canStartDrag(float dx, float dy) {
        return Math.hypot(dx, dy) > touchSlop;
    }

    // -------------------------------
    // 外部 API
    // -------------------------------

    public void reset() {
        if (isAutoScaling) {
            removeCallbacks(autoScaleRunnable);
            isAutoScaling = false;
        }
        if (scroller != null && !scroller.isFinished()) {
            scroller.abortAnimation();
        }
        isInitialized = false;
        isScaling = false;
        isDragging = false;
        matrix.reset();
        setImageMatrix(matrix);
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }
}