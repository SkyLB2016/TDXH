package com.sky.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by libin on 2020/01/26 20:50 Sunday.
 */
public class FlowTestLayout extends ViewGroup {
    /*
     *每行有几个View，有几行。
     */
    private List<List<View>> allViews = new ArrayList<>();
    private List<Integer> lineHeights = new ArrayList<>();
    private int lineSpace = 0;//行间距

    private Scroller scroller;
    private VelocityTracker tracker;
    private float interceptX = 0;
    private float interceptY = 0;
    private float lastX = 0;
    private float lastY = 0;
    private int touchSlop = 0;
    private int topBorder = 0;
    private int bottomBorder = 0;


    public FlowTestLayout(Context context) {
        this(context, null);
    }

    public FlowTestLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowTestLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        TypedArray style = context.obtainStyledAttributes(attrs, R.styleable.FlowLay);
//        lineSpace = style.getDimensionPixelSize(R.styleable.FlowLay_line, lineSpace);
//        style.recycle();

        if (scroller == null) {
            scroller = new Scroller(getContext());
            tracker = VelocityTracker.obtain();
            touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        }
    }

    public int getLineSpace() {
        return lineSpace;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;

        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);//不需继承。
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int childCount = getChildCount();
        if (childCount == 0) {
            setMeasuredDimension(widthSize, heightSize);
        }
        //因为onMeasure要多次执行，所以要清空该清空的数据
        allViews.clear();
        lineHeights.clear();

        //View真实可用的宽高是减去左右间距以及上下间距的跨高
        int realWidth = widthSize - getPaddingLeft() - getPaddingRight();
//        int realHeight = heightSize - getPaddingTop() - getPaddingBottom();

        //最大模式（自适应模式）下的宽高
        int maxWidth = 0;//是各行宽度比较之后就是最大的宽。
        int maxHeight = 0;//加上每行的高度，间距，还有top，bottom之后的最大高度。

        int lineWidth = 0;//每行都需要和最大宽度比较大小，所以需要计算左右的间距
        int lineHeight = 0;//每行的高度不需要和最大高度作比较。

        List<View> lineViews = new ArrayList<>();

        //测量每个child的宽高
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        //开始测量宽高
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
//            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //获取child计算后的宽高，需要包含leftMargin和rightMargin
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = lp.leftMargin + child.getMeasuredWidth() + lp.rightMargin;
            int childHeight = lp.topMargin + child.getMeasuredHeight() + lp.bottomMargin;
//getChildMeasureSpec()
            //控件的宽度+左右间距大于父容器提供的宽需要换行。
            if (lineWidth + childWidth > realWidth) {
                //比较此行的宽是否最大
                maxWidth = Math.max(maxWidth, lineWidth);
                //累计当前的高度，以及行间距
                maxHeight += lineHeight + lineSpace;

                allViews.add(lineViews);//保存此行的控件
                lineHeights.add(lineHeight);//保存此行最高值
                lineViews = new ArrayList<>();//重置

                //下一行的起始的宽高
                lineWidth = childWidth;
                lineHeight = childHeight;
            } else {
                //不换行时，累计当前View的宽
                lineWidth += childWidth;
                //比较最大的行高
                lineHeight = Math.max(childHeight, lineHeight);
            }
            //把此View加入行列中
            lineViews.add(child);
        }
        //最后一行是肯定不会加入的，所以要另行添加。
        if (lineHeight != 0) {
            maxWidth = Math.max(maxWidth, lineWidth);
            //累计当前的高度，最后一行之后没有行间距
            maxHeight += lineHeight;
//                maxHeight += (lineHeight + lineSpace);
            allViews.add(lineViews);//保存此行的控件
            lineHeights.add(lineHeight);//保存此行
        }
        maxWidth += getPaddingLeft() + getPaddingRight();
        maxHeight += getPaddingTop() + getPaddingBottom();
        topBorder = 0;
        bottomBorder = maxHeight;
        //ScrollView嵌套下，heightMode的模式为UNSPECIFIED，所以以判断EXACTLY为主。推荐使用getDefaultSize这个View自带的方法。
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else if (widthMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, maxHeight);
        } else if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(maxWidth, heightSize);
        } else {
            setMeasuredDimension(maxWidth, maxHeight);
        }
//        setMeasuredDimension(getDefaultSize(maxWidth, widthMeasureSpec), getDefaultSize(maxHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!changed) return;
        int leftC;
        int topC;
        int rightC;
        int bottomC;
        View child;
        MarginLayoutParams lp;
        for (int i = 0; i < allViews.size(); i++) {
            int lineHeight = lineHeights.get(i);
            List<View> views = allViews.get(i);
            leftC = getPaddingLeft();
            topC = getPaddingTop() + (lineHeight + lineSpace) * i;
            for (int j = 0; j < views.size(); j++) {
                child = views.get(j);
                lp = (MarginLayoutParams) child.getLayoutParams();
                leftC += lp.leftMargin;
                rightC = leftC + child.getMeasuredWidth();
                bottomC = topC + lp.topMargin + child.getMeasuredHeight();
                child.layout(leftC, topC + lp.topMargin, rightC, bottomC);
                leftC = rightC + lp.rightMargin;
            }
        }
    }

    @Override
    protected MarginLayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = super.onInterceptTouchEvent(event);
        if (getHeight() > bottomBorder) return false;
        if (tracker == null) {
            tracker = VelocityTracker.obtain();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                interceptX = event.getX();
                interceptY = event.getY();
                lastX = event.getX();
                lastY = event.getY();
                intercepted = false;
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                    intercepted = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = interceptX - event.getX();
                float deltaY = interceptY - event.getY();
                if (Math.abs(deltaY) >= Math.abs(deltaX) && Math.abs(deltaY) > touchSlop) {
                    requestDisallowInterceptTouchEvent(true);
                    intercepted = true;
                } else {
                    intercepted = false;
                }

                break;
            case MotionEvent.ACTION_UP:
                requestDisallowInterceptTouchEvent(false);
                intercepted = false;
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getHeight() > bottomBorder) return false;
        tracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
//                float deltaX = lastX -event.getX();
                int deltaY = (int) (lastY - event.getY());
                if (getScrollY() + deltaY <= 0) {
                    scrollTo(0, 0);
                    return true;
                } else if (getScrollY() + getHeight() + deltaY > bottomBorder) {
                    scrollTo(0, bottomBorder - getHeight());
                    return true;
                }
                scrollBy(0, deltaY);
                lastX = event.getX();
                lastY = event.getY();

                break;
            case MotionEvent.ACTION_UP:
                requestDisallowInterceptTouchEvent(false);
                int scrollY = getScrollY();
                tracker.computeCurrentVelocity(1000);
                float speed = tracker.getYVelocity();
                int dY = (int) (lastY - event.getY());

                if (speed > 500) {//速度为正数，是下滑，scrollX逐渐变小，最小为0
                    smoothScrollTo(getScrollY(), -getScrollY());
                } else if (speed < -500) {
                    smoothScrollTo(getScrollY(), bottomBorder - getHeight() - getScrollY());
                }
//                scrollBy(0, dY);
                restTouch();
                break;
            case MotionEvent.ACTION_CANCEL:
                restTouch();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    private void smoothScrollTo(int startY, int dY) {
//        val scrollX = scrollX
//        val delta = dX - scrollX
//        //在1000ms内画像dX，慢慢的滑动
        scroller.startScroll(0, startY, 0, dY, 1000);
        invalidate();
    }

    private void restTouch() {
        if (tracker != null) {
            tracker.clear();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (tracker != null) {
            tracker.recycle();
        }
    }
}
