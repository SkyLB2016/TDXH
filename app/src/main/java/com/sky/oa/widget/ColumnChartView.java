package com.sky.oa.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
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
public class ColumnChartView extends View {
    private List<String> bottomTexts = new ArrayList<>();//底部文字集合
    private List<Float> columns = new ArrayList<>();//柱形图的所占比例的集合，是底部文字的两倍。
    private List<String> leftTexts = new ArrayList<>();//左侧文字
    private List<Integer> columnColors = new ArrayList<>();//柱形图的颜色
    private int lineSpace = 50;//线之间的间隔
    private TextPaint textP;
    private Paint paint;
    private int columnWidth = 18;//柱形图宽度

    public ColumnChartView(Context context) {
        this(context, null);
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //7..0的集合
        for (int i = 7; i >= 0; i--) {
            leftTexts.add(i + "");
        }
        bottomTexts.clear();
        bottomTexts.add("入职");
        bottomTexts.add("转正");
        bottomTexts.add("调薪");
        bottomTexts.add("离职");

        columns.clear();
        columns.add(3f);
        columns.add(5f);
        columns.add(2f);
        columns.add(6f);
        columns.add(7f);
        columns.add(4f);
        columns.add(5f);
        columns.add(6f);

        columnColors.clear();
        columnColors.add(R.color.color_437DFF);
        columnColors.add(R.color.color_06CAFD);

        columnWidth = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_10);//柱形图宽度

        textP = new TextPaint();
        textP.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_12));

        paint = new Paint();//线与柱形图的画笔
        paint.setAntiAlias(true);
//        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);

    }

    public void setBottomTexts(List<String> bottomTexts) {
        this.bottomTexts = bottomTexts;
    }

    public void setColumns(List<Float> columns) {
        this.columns = columns;
    }

    public void setLeftTexts(List<String> leftTexts) {
        this.leftTexts = leftTexts;
    }

    public void setColumnColors(List<Integer> columnColors) {
        this.columnColors = columnColors;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = getPaddingTop() + getPaddingBottom() + lineSpace * 9;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (null == leftTexts || leftTexts.size() == 0) return;
        int left = getPaddingLeft() + lineSpace;
        int right = getMeasuredWidth() - getPaddingRight() - lineSpace;
        int top = (int) (getPaddingTop() + lineSpace * 0.5);

        textP.setTextAlign(Paint.Align.LEFT);
        Paint.FontMetricsInt metrics = textP.getFontMetricsInt();//文本的基线数据
        int textHeight = metrics.bottom - metrics.top;//文本框所占的高度
        float baseline = top + textHeight / 2 - metrics.bottom;//左侧第一行文字的基准线

        //线的颜色
        paint.setColor(ContextCompat.getColor(getContext(), R.color.color_B4B4B4));
        int leftLineX = left + 50;//虚线距离左边的距离，要跳过文字的宽度
        paint.setPathEffect(new DashPathEffect(new float[]{10f, 10f}, 0f));//虚线
        for (int i = 0; i < leftTexts.size(); i++) {
            canvas.drawText(leftTexts.get(i), left, baseline + lineSpace * i, textP);//画左侧文字
            if (i == leftTexts.size() - 1) {
                paint.setPathEffect(null);//去掉虚线，画实线。
            }
            canvas.drawLine(leftLineX, top + lineSpace * i, right, top + lineSpace * i, paint);//画线
        }

        //底部文字居中画
        textP.setTextAlign(Paint.Align.CENTER);//从每部分中间开始画文本
        //画底部文字，文本和柱形图，都按文本数组长度分组，
        int part = (right - leftLineX) / bottomTexts.size();
        //基线之上的top值本身是负数，所以是 -top
        baseline = top + lineSpace * 7 + lineSpace * 0.5f - metrics.top;

        RectF rectF = new RectF();//柱形图所在的矩形
        int smallRadius = columnWidth / 2;//柱形图上圆盖的半径
        for (int i = 0; i < bottomTexts.size(); i++) {
            //画底部文字
            canvas.drawText(bottomTexts.get(i), leftLineX + part / 2 + part * i, baseline, textP);

            //每部分第一个柱形图在中间偏左，第二个在中间偏右
            rectF.left = leftLineX + part / 2 - 5 - columnWidth + part * i;
            rectF.right = rectF.left + columnWidth;
            rectF.bottom = top + lineSpace * 7;
            //计算柱形图的高度，矩形底部边距减去柱形图所占的高度就是矩形的顶部边距
            rectF.top = rectF.bottom - columns.get(2 * i) * lineSpace;//所占比例

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(ContextCompat.getColor(getContext(), columnColors.get(0)));
            //画第一个柱形图
            canvas.drawRect(rectF, paint);
            //柱形图上的小圆盖
            canvas.drawCircle(rectF.left + smallRadius, rectF.top, smallRadius, paint);

            //每部分第二个柱形图在中间偏右，底部相同，左右上需要计算
            rectF.left = leftLineX + part / 2 + 5 + part * i;
            rectF.right = rectF.left + columnWidth;
            //计算柱形图的高度，矩形底部边距减去柱形图所占的高度就是矩形的顶部边距
            rectF.top = rectF.bottom - (columns.get(2 * i + 1) * lineSpace);//所占比例

            paint.setColor(ContextCompat.getColor(getContext(), columnColors.get(1)));
            //画第二个柱形图
            canvas.drawRect(rectF, paint);
            //柱形图上的小圆盖
            canvas.drawCircle(rectF.left + smallRadius, rectF.top, smallRadius, paint);
        }
    }
}