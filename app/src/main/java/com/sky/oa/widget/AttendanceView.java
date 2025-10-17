package com.sky.oa.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sky.oa.R;
import com.sky.oa.data.model.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/5/12 2:50 下午
 * @Version: 1.0
 */
public class AttendanceView extends View {
    private int radius = 200;//大圆半径
    private int circleStrokeSize = 50;//大圆线宽度

    private String title01 = "";
    private String title02 = "";
    private String subTitle = "";
    private float start = -180f;//起始角度
    private List<Float> sweeps = new ArrayList<>();//员工所占角度集合
    private List<Integer> colors = new ArrayList<>();//进度条的颜色

    private List<KeyValue> items = new ArrayList<>();//items信息

    private int dividerSize = 20;//item之间的间隔
    private int circleItemSpace = 60;//item与圆弧之间的距离
    private int bgWidth = 0;//item的宽
    private int bgHeight = 0;//item的高

    private Paint paint;
    private RectF area;//进度条所在的矩形
    private TextPaint titlePaint;//标题的画笔
    private TextPaint subPaint;//副标题以及二级文本的画笔

    private TextPaint itemNamePaint;//条目名字的画笔
    private Paint.FontMetricsInt metricsName;//条目名字文本的基线数据
    private int itemNameHeight;//条目名字文本框所占的高度
    private TextPaint numPaint;//条目数据的画笔
    private Paint.FontMetricsInt metricsNum;//条目数据文本的基线数据
    private int itemNumHeight;//条目数据文本框所占的高度
    private Drawable textbg;//item的背景框
    int leftTextPad;//条目文字左间隔


    public AttendanceView(Context context) {
        this(context, null);
    }

    public AttendanceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AttendanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //item之间的间隔
        dividerSize = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_10);
        circleItemSpace = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_30);
        leftTextPad = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_16);

        title01 = "80";
        title02 = "/100人";
        subTitle = "考勤异常/参与人数";

        //两组的
        sweeps.clear();
        sweeps.add(180f);
        sweeps.add(80f);

        colors.clear();
        colors.add(R.color.color_437DFF);
        colors.add(R.color.color_FFC512);

        items.clear();
        items.add(new KeyValue("未打卡（人次）", "8"));
        items.add(new KeyValue("外勤（人次）", "8"));
        items.add(new KeyValue("迟到（人次）", "8"));
        items.add(new KeyValue("早退（人次）", "8"));
        items.add(new KeyValue("加班（小时/天）", "8"));
        items.add(new KeyValue("请假（小时/天）", "8"));

        paint = new Paint();
        paint.setAntiAlias(true);

        //标题的画笔
        titlePaint = new TextPaint();
        //设置主标题一级文本字体，颜色，对齐方式
        titlePaint.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_26));
        titlePaint.setColor(getContext().getResources().getColor(R.color.color_333333));

        //副标题的画笔
        subPaint = new TextPaint();
        subPaint.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_16));

        textbg = ContextCompat.getDrawable(getContext(), R.drawable.shape_f5f5f5_radius_10);
        //名字的画笔
        itemNamePaint = new TextPaint();
        itemNamePaint.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_12));
        itemNamePaint.setColor(getContext().getResources().getColor(R.color.color_999999));
        metricsName = itemNamePaint.getFontMetricsInt();//文本的基线数据
        itemNameHeight = metricsName.bottom - metricsName.top;//文本框所占的高度

        //数据的画笔
        numPaint = new TextPaint();
        numPaint.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_16));
        numPaint.setColor(getContext().getResources().getColor(R.color.color_333333));
        numPaint.setTypeface(Typeface.DEFAULT_BOLD);
        metricsNum = numPaint.getFontMetricsInt();//文本的基线数据
        itemNumHeight = metricsNum.bottom - metricsNum.top;//文本框所占的高度
    }

    public void setTitle01(String title01) {
        this.title01 = title01;
    }

    public void setTitle02(String title02) {
        this.title02 = title02;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public void setSweeps(List<Float> sweeps) {
        this.sweeps = sweeps;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public void setItems(List<KeyValue> items) {
        this.items = items;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        radius = (width - getPaddingLeft() - getPaddingRight()) / 3;

        bgWidth = (width - getPaddingLeft() - getPaddingRight() - dividerSize * 2) / 3;//item的宽
        bgHeight = bgWidth * 3 / 5;//高是宽的3/5

        //view的高需要加上上间距，下间距，进度条线宽的一半，半斤，item与圆弧之间的距离，两个bgHeight，两个间隔
        int height = getPaddingTop() + getPaddingBottom() + circleStrokeSize / 2 + radius + circleItemSpace + bgHeight * 2 + dividerSize * 2;

        //圆形进度条所在矩形，线的宽度是从中间均分的。所以顶边的圆是会突破矩形所在空间的。
        float left = width / 2 - radius;
        float top = getPaddingTop() + circleStrokeSize / 2;
        float right = left + radius * 2;
        float bottom = top + radius * 2;
        area = new RectF(left, top, right, bottom);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == sweeps || 0 == sweeps.size()) return;
        canvasCircle(canvas);//进度条
        canvasText(canvas);//进度条对应的文字
        canvasItem(canvas);//其他考勤信息
    }

    private void canvasCircle(Canvas canvas) {
        float startSweep = start;//起始角度
        paint.setStrokeWidth(circleStrokeSize);
        paint.setStyle(Paint.Style.STROKE);

        int length = sweeps.size();
        for (int i = 0; i < length; i++) {
            //设置对应的颜色
            paint.setColor(ContextCompat.getColor(getContext(), colors.get(i)));
            //画进度条
            canvas.drawArc(area, startSweep, sweeps.get(i), false, paint);
        }
        //绘制进度条上的小圆
        //起始位置是x轴的正方向，顺时针旋转
        double angle = sweeps.get(1) / 180 * Math.PI + Math.PI;
        float ix = (float) (radius * Math.cos(angle) + area.centerX());
        float iy = (float) (radius * Math.sin(angle) + area.centerY());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(ix, iy, circleStrokeSize / 2, paint);
    }

    private void canvasText(Canvas canvas) {
        subPaint.setColor(getContext().getResources().getColor(R.color.color_666666));
        subPaint.setTextAlign(Paint.Align.CENTER);//文本对齐方式，居中对齐
        Paint.FontMetricsInt metrics = subPaint.getFontMetricsInt();//文本的基线数据
        int subTitleHeight = metrics.bottom - metrics.top;//副标题文本框高度
        float baseline = area.centerY() - 10 - metrics.bottom;//副标题的基线

        canvas.drawText(subTitle, area.centerX(), baseline, subPaint);//绘制副标题文本

        //主标题二级文本的颜色
        subPaint.setColor(getContext().getResources().getColor(R.color.color_333333));
        subPaint.setTextAlign(Paint.Align.LEFT);
        float subTitleWidth = subPaint.measureText(title02);//测量二级文本的宽度

        //测量主标题的基线数据。
        metrics = titlePaint.getFontMetricsInt();
        float titleWidth = titlePaint.measureText(title01);//文本的宽度

        baseline = area.centerY() - 10 - subTitleHeight - 10 - metrics.bottom;//主标题一二级文本共用一条基线
        canvas.drawText(title01, area.centerX() - (titleWidth + subTitleWidth) / 2, baseline, titlePaint);//画入画布中
        canvas.drawText(title02, area.centerX() - (titleWidth + subTitleWidth) / 2 + titleWidth, baseline, subPaint);//画入画布
    }

    private void canvasItem(Canvas canvas) {
        int left = getPaddingLeft();
        //条目开始的位置
        int top = (int) (area.centerY() + circleItemSpace);
        int leftX;
        int topY;
        Rect textBGRect;//item背景框所在矩形

        float baseline;//基线
        int line;//第几行，取整
        int column;//第几列，取余
        KeyValue kv;
        for (int i = 0; i < items.size(); i++) {
            line = i / 3;//取整
            column = i % 3;//取余

            //计算当前背景宽的上下左右位置
            leftX = left + (bgWidth + dividerSize) * column;
            topY = top + (bgHeight + dividerSize) * line;
            textBGRect = new Rect(leftX, topY, leftX + bgWidth, topY + bgHeight);
            textbg.setBounds(textBGRect);//为文字设置背景
            textbg.draw(canvas);//画入画布中

            //画文字
            kv = items.get(i);
            baseline = topY + bgHeight / 4 + itemNameHeight / 2 - metricsName.bottom;
            canvas.drawText(kv.getKey(), leftX + leftTextPad, baseline, itemNamePaint);//画入画布中
            baseline = topY + bgHeight / 4 * 3 + itemNumHeight / 2 - metricsNum.bottom;
            canvas.drawText(kv.getValue(), leftX + leftTextPad, baseline, numPaint);//画入画布中
        }
    }
}
