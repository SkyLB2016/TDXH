package com.sky.oa.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sky.oa.R;

import java.util.ArrayList;
import java.util.List;


/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/5/12 2:50 下午
 * @Version: 1.0
 */
public class CircleProgressView extends View {
    private int radius = 200;//大圆半径
    private int circleStrokeSize = 30;//大圆线宽度
    private int radiusSmall = 24;//小圆半径

    private String title = "";
    private String subTitle = "";
    private float markSweepLine = 2f;//弧度之间的间隔
    private float start = -90f;//起始角度
    private List<String> marks = new ArrayList<>();//员工种类文字集合
    private List<Float> sweeps = new ArrayList<>();//员工所占角度集合
    private List<Integer> colors = new ArrayList<>();//进度条的颜色

    private RectF area;//进度条所在的背景框
    private Paint paint;//进度条和小圆的画笔
    private TextPaint textP;//文字的画笔

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        title = "190人";
        subTitle = "总在职员工";

        marks.clear();
        marks.add("正式员工28人");
        marks.add("劳务派遣9人");
        marks.add("试用员工9人");
        marks.add("其他员工9人");

        //两组的
//        sweeps.clear();
//        sweeps.add(188f + 2);
//        sweeps.add(168f + 2);

        //四组的
        sweeps.clear();
        sweeps.add(118f + 2);
        sweeps.add(78f + 2);
        sweeps.add(68f + 2);
        sweeps.add(88f + 2);

        colors.clear();
        colors.add(R.color.color_437DFF);
        colors.add(R.color.color_06CAFD);
        colors.add(R.color.color_FFC512);
        colors.add(R.color.color_DFDFDF);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(circleStrokeSize);

        //绘制文字的paint
        textP = new TextPaint();
        textP.setColor(getContext().getResources().getColor(R.color.color_666666));

    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setCircleStrokeSize(int circleStrokeSize) {
        this.circleStrokeSize = circleStrokeSize;
    }

    public void setRadiusSmall(int radiusSmall) {
        this.radiusSmall = radiusSmall;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public void setMarks(List<String> marks) {
        this.marks = marks;
    }

    public void setSweeps(List<Float> sweeps) {
        this.sweeps = sweeps;
    }

    public void setStart(float start) {
        this.start = start;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = radius * 2 + circleStrokeSize + getPaddingTop() + getPaddingBottom();

        //圆形进度条所在矩形，线的宽度是从中间均分的。所以顶边的圆是会突破矩形所在空间的。
        float left = getPaddingLeft() + circleStrokeSize / 2;
        float top = getPaddingTop() + circleStrokeSize / 2;
        area = new RectF(left, top, left + radius * 2, top + radius * 2);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == sweeps || 0 == sweeps.size()) return;
        textP.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_12));
        textP.setTextAlign(Paint.Align.CENTER);//文本对齐方式，居中对齐
        Paint.FontMetricsInt metrics = textP.getFontMetricsInt();//文本的基线数据
        int textHeight = metrics.bottom - metrics.top;//文本框所占的高度

        //先画副标题的线
        float baseline = area.centerY() + 10 - metrics.top;
        canvas.drawText(subTitle, area.centerX(), baseline, textP);//副标题

        textP.setTextAlign(Paint.Align.LEFT);
        float startSweep = start;//起始角度
        float smallCenterY;
        float sweep;
        int length = sweeps.size();
        //小圆起始位置,根据进度的数量来算，小圆所占范围为直径的二分之一，三分之一，四分之一，五分之一，并居中显示。
        int smallStart = radius / length;
        int addSpace = radius * 2 / length;//小圆间的间隔
        //有个进度条就匹配几个小圆与文字
        for (int i = 0; i < length; i++) {
            //设置对应的颜色
            paint.setColor(ContextCompat.getColor(getContext(), colors.get(i)));

            //画四个进度条
            paint.setStyle(Paint.Style.STROKE);
            sweep = sweeps.get(i);
            if (sweep > 0f) {
                canvas.drawArc(area, startSweep, sweeps.get(i) - markSweepLine, false, paint);
            }
            startSweep += sweeps.get(i);

            //画进度条对应的四个小圆
            paint.setStyle(Paint.Style.FILL);
            //四个小圆取直径的八分之一，三，五，七
            smallCenterY = area.top + smallStart + addSpace * i;
            canvas.drawCircle(area.right + radius, smallCenterY, radiusSmall, paint);

            //写入对应小圆的文字
            float leftMark = area.right + radius + radiusSmall * 3;
            baseline = smallCenterY + textHeight / 2 - metrics.bottom;
            canvas.drawText(marks.get(i), leftMark, baseline, textP);//画入画布中
        }

        textP.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_18));
        textP.setTextAlign(Paint.Align.CENTER);//文本对齐方式，居中对齐
        //改变文字大小后，再一次获取文本的基线数据，好像不再次获取也可以。
        metrics = textP.getFontMetricsInt();//文本的基线数据

        //让文字居于背景中间，计算文字的左距离与底部距离
        baseline = area.centerY() - 10 - metrics.bottom;
        canvas.drawText(title, area.centerX(), baseline, textP);//主标题文字
    }
}
