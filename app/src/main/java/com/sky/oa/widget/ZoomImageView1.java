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
 * 优化版 ZoomImageView1
 * 支持：双击放大、手势缩放、拖动、边界回弹、与 ViewPager 协同
 */
public class ZoomImageView1 extends AppCompatImageView implements
        ViewTreeObserver.OnGlobalLayoutListener,
        View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener,
        GestureDetector.OnGestureListener {

    // 缩放级别
    private float minScale = 1.0f;
    private float midScale = 2.0f;
    private float maxScale = 4.0f;

    private Matrix matrix;
    private final float[] matrixValues = new float[9];

    // 手势识别
    private ScaleGestureDetector scaleDetector;
    private GestureDetector gestureDetector;
    private float mTouchSlop;

    // 拖动状态
    private float lastX, lastY;
    private int lastPointerCount = 0;
    private boolean isDragging = false;

    // 双击状态
    private boolean isScaling = false;

    // 惯性滚动
    private OverScroller scroller;

    // 视图是否已初始化
    private boolean isInitialized = false;

    public ZoomImageView1(Context context) {
        this(context, null);
    }

    public ZoomImageView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        matrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        scaleDetector = new ScaleGestureDetector(context, this);
        gestureDetector = new GestureDetector(context, this);
        gestureDetector.setIsLongpressEnabled(false);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scroller = new OverScroller(context);

        setOnTouchListener(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void onGlobalLayout() {
        if (isInitialized) return;

        Drawable drawable = getDrawable();
        if (drawable == null) return;

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth();
        int vHeight = getHeight();

        float scale = 1.0f;

        // 图片宽高都小于控件，居中显示（不缩放）
        if (dWidth <= vWidth && dHeight <= vHeight) {
            scale = 1.0f;
        }
        // 图宽 > 控件宽，按宽度缩放
        else if (dWidth > vWidth && dHeight <= vHeight) {
            scale = vWidth * 1.0f / dWidth;
        }
        // 图高 > 控件高，按高度缩放
        else if (dHeight > vHeight && dWidth <= vWidth) {
            scale = vHeight * 1.0f / dHeight;
        }
        // 宽高都大于控件，按最小比例缩放
        else {
            scale = Math.min(vWidth * 1.0f / dWidth, vHeight * 1.0f / dHeight);
        }

        minScale = scale;
        midScale = minScale * 2f;
        maxScale = minScale * 4f;

        float dx = (vWidth - dWidth * scale) / 2f;
        float dy = (vHeight - dHeight * scale) / 2f;

        matrix.postScale(scale, scale);
        matrix.postTranslate(dx, dy);
        setImageMatrix(matrix);

        isInitialized = true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 优先交给双指缩放检测
        scaleDetector.onTouchEvent(event);

        // 如果正在缩放，不处理其他手势
        if (scaleDetector.isInProgress()) {
            isDragging = false;
            return true;
        }

        // 处理单点手势
        gestureDetector.onTouchEvent(event);

        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        float x = 0, y = 0;
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                lastY = y;
                lastPointerCount = pointerCount;
                isDragging = false;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                lastPointerCount = pointerCount;
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = x - lastX;
                float dy = y - lastY;

                if (!isDragging && pointerCount == 1) {
                    isDragging = Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
                }

                if (isDragging && pointerCount == 1) {
                    // 检查是否需要拦截事件
                    RectF rect = getMatrixRectF();
                    boolean canScrollHorizontally = false;

                    if (dx > 0 && rect.left >= 0) canScrollHorizontally = true;
                    if (dx < 0 && rect.right <= getWidth()) canScrollHorizontally = true;

                    getParent().requestDisallowInterceptTouchEvent(!canScrollHorizontally);

                    matrix.postTranslate(dx, dy);
                    checkBorderAndCenter();
                    setImageMatrix(matrix);
                }

                lastX = x;
                lastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                // 惯性滑动
                if (isDragging && !scaleDetector.isInProgress()) {
                    RectF rect = getMatrixRectF();
                    if (rect.width() > getWidth() || rect.height() > getHeight()) {
                        scroller.fling(
                                (int) rect.left, (int) rect.top,
                                (int) getLastXVelocity(), (int) getLastYVelocity(),
                                Integer.MIN_VALUE, Integer.MAX_VALUE,
                                Integer.MIN_VALUE, Integer.MAX_VALUE
                        );
                        postOnAnimation(flingRunnable);
                    }
                }
                break;
        }

        return true;
    }

    // -----------------------------
    // 缩放控制
    // -----------------------------

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        float currentScale = getScale();

        if ((currentScale < maxScale && scaleFactor > 1.0f) ||
                (currentScale > minScale && scaleFactor < 1.0f)) {

            float targetScale = currentScale * scaleFactor;
            targetScale = Math.max(minScale, Math.min(targetScale, maxScale));

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            matrix.postScale(targetScale / currentScale, targetScale / currentScale, focusX, focusY);
            checkBorderAndCenter();
            setImageMatrix(matrix);
        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true; // 开始缩放
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        // 缩放结束后居中对齐
        checkBorderAndCenter();
    }

    // -----------------------------
    // 双击放大/缩小
    // -----------------------------

    public boolean onDoubleTap(MotionEvent e) {
        if (isScaling) return true;

        float currentScale = getScale();
        float targetScale;
        if (currentScale < midScale) {
            targetScale = midScale;
        } else {
            targetScale = minScale;
        }

        smoothZoomTo(targetScale, e.getX(), e.getY());
        return true;
    }

    private void smoothZoomTo(float targetScale, float focusX, float focusY) {
        isScaling = true;
        final float startScale = getScale();
        final long duration = 200;

        final long startTime = System.currentTimeMillis();

        post(new Runnable() {
            @Override
            public void run() {
                long t = System.currentTimeMillis() - startTime;
                float progress = Math.min((float) t / duration, 1.0f);

                // 插值器：加速减速
                progress = (float) (1 - (1 - progress) * (1 - progress));

                float scale = startScale + (targetScale - startScale) * progress;
                float focusScale = scale / startScale;

                matrix.postScale(focusScale, focusScale, focusX, focusY);
                checkBorderAndCenter();
                setImageMatrix(matrix);

                if (progress < 1.0f) {
                    postOnAnimation(this);
                } else {
                    isScaling = false;
                }
            }
        });
    }

    // -----------------------------
    // 边界检查与居中
    // -----------------------------

    private void checkBorderAndCenter() {
        RectF rect = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        int width = getWidth();
        int height = getHeight();

        if (rect.width() > width) {
            if (rect.left > 0) deltaX = -rect.left;
            if (rect.right < width) deltaX = width - rect.right;
        } else {
            deltaX = width / 2f - rect.left - rect.width() / 2f;
        }

        if (rect.height() > height) {
            if (rect.top > 0) deltaY = -rect.top;
            if (rect.bottom < height) deltaY = height - rect.bottom;
        } else {
            deltaY = height / 2f - rect.top - rect.height() / 2f;
        }

        matrix.postTranslate(deltaX, deltaY);
    }

    private RectF getMatrixRectF() {
        RectF rect = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rect.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    private float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    // -----------------------------
    // 惯性滑动
    // -----------------------------

    private float getLastXVelocity() { return 0; } // 可扩展：使用 VelocityTracker
    private float getLastYVelocity() { return 0; }

    private final Runnable flingRunnable = new Runnable() {
        @Override
        public void run() {
            if (scroller.computeScrollOffset()) {
                int oldX = scroller.getCurrX();
                int oldY = scroller.getCurrY();
                scroller.computeScrollOffset();
                int newX = scroller.getCurrX();
                int newY = scroller.getCurrY();

                matrix.postTranslate(oldX - newX, oldY - newY);
                checkBorderAndCenter();
                setImageMatrix(matrix);

                postOnAnimation(this);
            }
        }
    };

    // -----------------------------
    // 未使用的方法（保持接口完整）
    // -----------------------------

    @Override public boolean onDown(MotionEvent e) { return true; }
    @Override public void onShowPress(MotionEvent e) {}
    @Override public boolean onSingleTapUp(MotionEvent e) { return false; }
    @Override public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) { return false; }
    @Override public void onLongPress(MotionEvent e) {}
    @Override public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) { return false; }
}