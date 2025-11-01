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
import android.widget.Toast;

import androidx.annotation.NonNull;

/**
 * Created by SKY on 2015/8/17 15:30.
 * 图片缩放
 */
public class ZoomImageView extends androidx.appcompat.widget.AppCompatImageView implements
        ViewTreeObserver.OnGlobalLayoutListener,
        View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener {

    // 缩放级别
    private float minScale = 1.0f;
    private float midScale = 2.0f;
    private float maxScale = 4.0f;

    private final Matrix matrix;
    private final float[] matrixValues = new float[9];

    // 缩放检查
    private final ScaleGestureDetector scaleDetector;
    // 手势识别
    private final GestureDetector gestureDetector;
    private final float mTouchSlop;


    // 拖动状态
    private float lastX, lastY;

    // 第一次按下的 x 的位置
    private float firstDownX;

    /**
     * 记录上次多点触控的数量
     */
    private int mLastPointerCount = 0;
    // 是否推动中
    private Boolean isDragging;

    //是否缩放
    private Boolean isAutoScale;
    private Boolean isScaling = false;

    // 惯性滚动
    private OverScroller scroller;

    // 视图是否已初始化
    private boolean isInitialized = false;
//    private boolean once = true;//只执行一次

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化 matrix 矩阵
        matrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
        // 缩放检查
        scaleDetector = new ScaleGestureDetector(context, this);
        // 手势检查
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isScaling)
                    return true;
                System.out.println("双击事件");
//                smoothZoomToAsync(e);
                smoothZoomTo(e);
                return true;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);

        // 获取系统认为“最小有效滑动距离”——即用户手指滑动超过这个距离，才被认为是“滑动手势”，而不是“点击”或“误触”。
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scroller = new OverScroller(context);

        setOnTouchListener(this);
    }

    /**
     * 异步缩放
     * @param e
     */
    private void smoothZoomTo(MotionEvent e) {
        isScaling = true;

        float startScale = getScale();
        float targetScale;
        if (startScale < midScale) {
            targetScale = midScale;
        }else if (startScale<maxScale){
            targetScale = maxScale;
        } else {
            targetScale = minScale;
        }

        float focusX = e.getX();
        float focusY = e.getY();
        final long duration = 200;
        final long startTime = System.currentTimeMillis();

        // 异步缩放
//        postDelayed(new AutoScaleRunnable(targetScale, x, y), 18);
        post(new Runnable() {
            @Override
            public void run() {
                long t = System.currentTimeMillis() - startTime;
                float progress = Math.min((float) t / duration, 1.0f);

                // 插值器：加速减速
                progress = (float) (1 - (1 - progress) * (1 - progress));

                System.out.println("progress=="+progress);
                float scale = startScale + (targetScale - startScale) * progress;
                float focusScale = scale / getScale();

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


    private class AutoScaleRunnable implements Runnable {

        private float mTageTScale;
        private float x, y;

        private final float BIGGER = 1.07F;
        private final float SMALL = 0.93F;

        private float tempScale;

        public AutoScaleRunnable(float mTageTScale, float x, float y) {
            this.mTageTScale = mTageTScale;
            this.x = x;
            this.y = y;
            if (mTageTScale > getScale()) {
                tempScale = BIGGER;
            }
            if (mTageTScale < getScale()) {
                tempScale = SMALL;
            }
        }

        @Override
        public void run() {
            matrix.postScale(tempScale, tempScale, x, y);
            checkBorderAndCenter();
            setImageMatrix(matrix);
            float currentScale = getScale();
            if (tempScale > 1.0f && currentScale < mTageTScale || tempScale < 1.0f && currentScale > mTageTScale) {
                postDelayed(this, 18);
            } else {
                isScaling = false;
                float scale = mTageTScale / getScale();
                matrix.postScale(scale, scale, x, y);
                checkBorderAndCenter();
                setImageMatrix(matrix);
            }
        }
    }

    // -----------------------------
    // 双击放大/缩小
    // -----------------------------

    /**
     * 直接缩放
     * @param e
     */
    private void smoothZoomToAsync(MotionEvent e) {
        isScaling = true;

        float currentScale = getScale();
        float targetScale;
        if (currentScale < midScale) {
            targetScale = 2;
        }else if (currentScale<maxScale){
            targetScale = 2;
        } else {
            targetScale = 0.25f;
        }
        System.out.println("targetScale==" + targetScale);

        float x = e.getX();
        float y = e.getY();

        // 直接缩放
        matrix.postScale(targetScale, targetScale, x, y);
        setImageMatrix(matrix);
        isScaling = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (isInitialized) return;
        // 1.获取图片
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        // 2.获取图的宽高以及控件的宽高
        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth();
        int vHeight = getHeight();

//        System.out.println("控件宽=="+vWidth+",控件高"+vHeight);
//        System.out.println("图宽=="+dWidth+",图高"+dHeight);

        // 3.计算缩放的比例，扩大以最小的为准，缩小也已最小的为准
        float scale = 1.0f;
        // 图宽 > 控件宽，按宽度缩放
        if (dWidth > vWidth && dHeight <= vHeight) {
            scale = vWidth * 1.0f / dWidth;
            System.out.println("图宽");
        }
        // 图高 > 控件高，按高度缩放
        else if (dHeight > vHeight && dWidth <= vWidth) {
            scale = vHeight * 1.0f / dHeight;
            System.out.println("图高");
        }
        // 宽高都大于控件，按最小比例缩放
        // 图片宽高都小于控件，也是按最小的缩放比例缩放。
        else {
            System.out.println("图宽高或者控件宽高");
            scale = Math.min(vWidth * 1.0f / dWidth, vHeight * 1.0f / dHeight);
        }
        System.out.println("初始缩放scale==" + scale);


        minScale = scale;
        midScale = 2 * minScale;
        maxScale = 4 * minScale;
        // 4.1、先移动再缩放
        // 缩放前移动，按原来的图高计算
        float dx = (vWidth - dWidth) / 2f;
        float dy = (vHeight - dHeight) / 2f;
//        System.out.println(dx + "," + dy);
        //将图片在 X 轴方向移动 dx 像素，在 Y 轴方向移动 dy 像素。
        matrix.postTranslate(dx, dy);
        //将图片先缩放到“适应屏幕”的大小，并以屏幕中心为锚点进行缩放。
        matrix.postScale(minScale, minScale, vWidth / 2f, vHeight / 2f);


        // 4.1、先缩放再移动
        //将图片先缩放到“适应屏幕”的大小，并以默认锚点（0,0）进行缩放。
        //matrix.postScale(scale, scale);
        // 缩放后移动，按缩放后的图高计算
        //float dx = (vWidth - dWidth * scale) / 2f;
        //float dy = (vHeight - dHeight * scale) / 2f;
        //System.out.println(dx + "," + dy);
        //matrix.postTranslate(dx, dy);

        // 5.设置 matrix 矩阵
        //将你自定义的 Matrix（3x3 变换矩阵）注入到 ImageView 的绘制流程中，在 onDraw() 时使用它来变换图片的坐标。
        setImageMatrix(matrix);

        isInitialized = true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 1.优先 手势检测
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        // 2.其次 缩放检测
        scaleDetector.onTouchEvent(event);

        // 如果正在缩放，不处理其他手势
        if (scaleDetector.isInProgress()) {
            isDragging = false;
            return true;
        }
        // 3.单指拖拽，多指返回
        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            return true;
        }
        float currX = event.getX();
        float currY = event.getY();
        RectF rectF = getMatrixRectF();
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = currX;
                lastY = currY;
                isDragging = false;

                firstDownX = event.getRawX();
                // 控件框架内请求父容器不要拦截事件
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    //告诉viewpager不拦截触摸事件，false则拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            case MotionEvent.ACTION_MOVE:
                // 请求点击事件
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float dx = (int) event.getRawX() - firstDownX;
                    if (rectF.right <= getWidth() + 0.01 && dx < 0 || rectF.left >= -0.01 && dx > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }

                //4. 计算移动东，是否可拖拽
                float dx = currX - lastX;
                float dy = currY - lastY;

                if (!isDragging) {
                    isDragging = isMoveAction(dx, dy);
                }
                if (isDragging) {
                    matrix.postTranslate(dx, dy);
                    checkBorderAndCenter();
                    setImageMatrix(matrix);
                }
                lastX = currX;
                lastY = currY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
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
    // 边界检查与居中
    // -----------------------------

    /**
     * 当移动时进行边界检查
     */
    private void checkBorderAndCenter() {
        RectF rectF = getMatrixRectF();
        float deltaX = 0, deltaY = 0;
        int width = getWidth();
        int height = getHeight();

        if (rectF.width() > width) {
            if (rectF.right < width) {
                deltaX = width - rectF.right;
            } else if (rectF.left > 0) {
                deltaX = -rectF.left;
            }
        } else {
            deltaX = width / 2f - rectF.left - rectF.width() / 2f;
        }
        if (rectF.height() > height) {
            if (rectF.top > 0) {
                deltaY = -rectF.top;
            } else if (rectF.bottom < height) {
                deltaY = height - rectF.bottom;
            }
        } else {
            deltaY = height / 2f - rectF.top - rectF.height() / 2f;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }


    // -----------------------------
    // 惯性滑动
    // -----------------------------

    private float getLastXVelocity() {
        return 0;
    } // 可扩展：使用 VelocityTracker

    private float getLastYVelocity() {
        return 0;
    }

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
    // 惯性滑动
    // -----------------------------

    private float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    // -----------------------------
    // 缩放控制
    // -----------------------------

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // 当前缩放比例
        float scale = getScale();
        // 默认的缩放因子，放大时大于1.0，缩小时小于1.0
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null)
            return true;
        if ((scale > minScale && scaleFactor < 1.0f) ||
                (scale < maxScale && scaleFactor > 1.0f)) {

            // 缩小，不能低于最小值
            if (scale * scaleFactor < minScale) {
                scaleFactor = minScale / scale;
            }
            // 放大，不能大于最大值
            if (scale * scaleFactor > maxScale) {
                scaleFactor = maxScale / scale;
            }
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
//            matrix.postScale(scaleFactor,scaleFactor,getWidth()/2,getHeight()/2);
            checkBorderAndCenter();
            setImageMatrix(matrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
        return true; // 开始缩放
    }

    @Override
    public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
        // 缩放结束后居中对齐
        checkBorderAndCenter();
    }

    private RectF getMatrixRectF() {
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }

}
