package com.sky.oa.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;

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
    private float firstX;

    /**
     * 记录上次多点触控的数量
     */
    private int mLastPointerCount = 0;
    // 是否推动中
    private Boolean isDragging;

    // 左右边界检查
    private Boolean isCheckLeftAndRight;
    // 上下边界检查
    private Boolean isCheckTopAndBottom;

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
//        super.setScaleType(ScaleType.CENTER);
        // 缩放检查
        scaleDetector = new ScaleGestureDetector(context, this);
        // 手势检查
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isScaling)
                    return true;
                onDoubleTab(e);
                return true;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);

        // 获取系统认为“最小有效滑动距离”——即用户手指滑动超过这个距离，才被认为是“滑动手势”，而不是“点击”或“误触”。
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        scroller = new OverScroller(context);

        setOnTouchListener(this);
    }

    private void onDoubleTab(MotionEvent e) {

        System.out.println("双击事件");
        float x = e.getX();
        float y = e.getY();

        if (getScale() < midScale) {
//            matrix.postScale(midScale / getScale(), midScale / getScale(),x, y);
//            setImageMatrix(matrix);
            postDelayed(new AutoScaleRunnable(midScale, x, y), 18);
            isScaling = true;
        } else {

//            matrix.postScale(minScale / getScale(), minScale / getScale(), x, y);
//            setImageMatrix(matrix);
            postDelayed(new AutoScaleRunnable(minScale, x, y), 18);
            isScaling = true;
        }
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
            checkimage();
            setImageMatrix(matrix);
            float currentScale = getScale();
            if (tempScale > 1.0f && currentScale < mTageTScale || tempScale < 1.0f && currentScale > mTageTScale) {
                postDelayed(this, 18);
            } else {
                isScaling = false;
                float scale = mTageTScale / getScale();
                matrix.postScale(scale, scale, x, y);
                checkimage();
                setImageMatrix(matrix);
            }
        }
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
        Drawable drawable = getDrawable();
        if (drawable == null) return;

        int dWidth = drawable.getIntrinsicWidth();
        int dHeight = drawable.getIntrinsicHeight();
        int vWidth = getWidth();
        int vHeight = getHeight();

//        System.out.println("控件宽=="+vWidth+",控件高"+vHeight);
//        System.out.println("图宽=="+dWidth+",图高"+dHeight);

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
            System.out.println("图宽高");
            scale = Math.min(vWidth * 1.0f / dWidth, vHeight * 1.0f / dHeight);
        }
        System.out.println("scale=="+scale);

        minScale = scale;
        midScale = 2 * minScale;
        maxScale = 4 * minScale;

        // 缩放前移动，按原来的图高计算
        float dx = (vWidth - dWidth) / 2f;
        float dy = (vHeight - dHeight) / 2f;
        System.out.println(dx + "," + dy);
        //将图片在 X 轴方向移动 dx 像素，在 Y 轴方向移动 dy 像素。
        matrix.postTranslate(dx, dy);
        //将图片先缩放到“适应屏幕”的大小，并以屏幕中心为锚点进行缩放。
        matrix.postScale(minScale, minScale, vWidth / 2f, vHeight / 2f);



//        //将图片先缩放到“适应屏幕”的大小，并以默认锚点（0,0）进行缩放。
//        matrix.postScale(scale, scale);
//
//        // 缩放后移动，按缩放后的图高计算
//        float dx = (vWidth - dWidth * scale) / 2f;
//        float dy = (vHeight - dHeight * scale) / 2f;
//        System.out.println(dx + "," + dy);
//        matrix.postTranslate(dx, dy);

        //将你自定义的 Matrix（3x3 变换矩阵）注入到 ImageView 的绘制流程中，在 onDraw() 时使用它来变换图片的坐标。
        setImageMatrix(matrix);

        isInitialized = true;
    }

    private float getScale() {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null)
            return true;
        if ((scale > minScale && scaleFactor < 1.0f) || (scale < maxScale && scaleFactor > 1.0f)) {
            if (scale * scaleFactor < minScale) {
                scaleFactor = minScale / scale;
            }
            if (scale * scaleFactor > maxScale) {
                scaleFactor = maxScale / scale;
            }
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
//            matrix.postScale(scaleFactor,scaleFactor,getWidth()/2,getHeight()/2);
            checkimage();
            setImageMatrix(matrix);

        }
        return true;
    }

    private RectF getRectF() {
        Matrix mmatrix = matrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {

            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mmatrix.mapRect(rectF);
        }
        return rectF;
    }

    private void checkimage() {
        RectF rectF = getRectF();
        float delaX = 0;
        float delaY = 0;
        int width = getWidth();
        int height = getHeight();
        if (rectF.width() >= width) {
            if (rectF.left > 0) {
                delaX = -rectF.left;
            }
            if (rectF.right < width) {
                delaX = width - rectF.right;
            }
        }

        if (rectF.height() >= height) {
            if (rectF.top > 0) {
                delaY = -rectF.top;
            }
            if (rectF.bottom < height) {
                delaY = height - rectF.bottom;

            }
        }

        if (rectF.width() < width) {
            delaX = width / 2f - rectF.left - rectF.width() / 2f;
        }
        if (rectF.height() < height) {
            delaY = height / 2f - rectF.top - rectF.height() / 2f;
        }
        matrix.postTranslate(delaX, delaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        scaleDetector.onTouchEvent(event);
        float x = 0;
        float y = 0;

        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            isDragging = false;
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if (mLastPointerCount != pointerCount) {
            lastX = x;
            lastY = y;
        }
        mLastPointerCount = pointerCount;
        RectF rectF = getRectF();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                firstX = event.getRawX();
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    //告诉viewpager不拦截触摸事件，false则拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            case MotionEvent.ACTION_MOVE:
                if (rectF.width() > getWidth() + 0.01 || rectF.height() > getHeight() + 0.01) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float dx = (int) event.getRawX() - firstX;
                    if (rectF.right <= getWidth() + 0.01 && dx < 0 || rectF.left >= -0.01 && dx > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }

                float dx = x - lastX;
                float dy = y - lastY;
                if (!isDragging) {
                    isDragging = isMoveAction(dx, dy);
                }
                if (isDragging) {
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }
                        matrix.postTranslate(dx, dy);
                        checkBoderWhenTranslate();
                        setImageMatrix(matrix);
                    }
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;
                break;
        }
        return true;
    }

    /**
     * 当移动时进行边界检查
     */
    private void checkBoderWhenTranslate() {
        RectF rectF = getRectF();
        float delaX = 0;
        float delaY = 0;

        if (rectF.right < getWidth() && isCheckLeftAndRight) {
            delaX = getWidth() - rectF.right;
        }
        if (rectF.left > 0 && isCheckLeftAndRight) {
            delaX = -rectF.left;
        }
        if (rectF.top > 0 && isCheckTopAndBottom) {
            delaY = -rectF.top;
        }
        if (rectF.bottom < getHeight() && isCheckTopAndBottom) {
            delaY = getHeight() - rectF.bottom;
        }
        matrix.postTranslate(delaX, delaY);
    }

    private boolean isMoveAction(float dx, float dy) {
        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }
}
