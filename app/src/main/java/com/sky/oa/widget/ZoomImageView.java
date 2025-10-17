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

/**
 * Created by SKY on 2015/8/17 15:30.
 * 图片缩放
 */
public class ZoomImageView extends androidx.appcompat.widget.AppCompatImageView implements
        ViewTreeObserver.OnGlobalLayoutListener, View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private boolean once = true;//只执行一次
    private float minScale;
    private float midScale;
    private float maxScale;

    /**
     * 记录上次多点触控的数量
     */
    private int mLastPointerCount;
    private int mTouchSlop;

    private Boolean isCanDrag;
    private Boolean isCheckLeftAndRight;
    private Boolean isCheckTopAndBottom;
    private Boolean isAutoScale;


    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;

    private Matrix matrix;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        matrix = new Matrix();
        super.setScaleType(ScaleType.CENTER.MATRIX);
        scaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        isAutoScale = false;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (isAutoScale)
                    return true;
                float x = e.getX();
                float y = e.getY();

                if (getScale() < midScale) {
//                    matrix.postScale(midScale/getScale(),midScale/getScale(),`x,y);
//                    setImageMatrix(matrix);
                    postDelayed(new AutoScaleRunnable(midScale, x, y), 18);
                    isAutoScale = true;
                } else {

//                    matrix.postScale(minScale/getScale(),minScale/getScale(),x,y);
//                    setImageMatrix(matrix);
                    postDelayed(new AutoScaleRunnable(minScale, x, y), 18);
                    isAutoScale = true;
                }
                return true;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);
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
                isAutoScale = false;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @Override
    public void onGlobalLayout() {
        if (once) {
            int width = getWidth();
            int height = getHeight();
            Drawable image = getDrawable();
            if (image == null) {
                return;
            }
            int dw = image.getIntrinsicWidth();
            int dh = image.getIntrinsicHeight();

            float scale = 1.0f;
            if (dw > width && dh < height) {
                scale = width * 1.0f / dw;
            }
            if (dh > height && dw < width) {
                scale = height * 1.0f / dh;
            }
            if ((dw > width && dh > height) || (dw < width && dh < height)) {
                scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
            }
            minScale = scale;
            midScale = 2 * minScale;
            maxScale = 4 * minScale;

            int cx = width / 2 - dw / 2;
            int cy = height / 2 - dh / 2;


            matrix.postTranslate(cx, cy);
            matrix.postScale(minScale, minScale, width / 2, height / 2);
            setImageMatrix(matrix);
            once = false;
        }

    }

    public float getScale() {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X];

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

    private float mLastX, mLastY;
    private float firstX;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        scaleGestureDetector.onTouchEvent(event);
        float x = 0;
        float y = 0;

        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            isCanDrag = false;
            x += event.getX(i);
            y += event.getY(i);
        }
        x /= pointerCount;
        y /= pointerCount;
        if (mLastPointerCount != pointerCount) {
            mLastX = x;
            mLastY = y;
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

                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }
                if (isCanDrag) {
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
                mLastX = x;
                mLastY = y;
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
