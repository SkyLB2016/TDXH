package com.sky.oa.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sky.oa.R;
import com.sky.oa.data.model.MyAttendanceEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/5/12 2:50 下午
 * @Version: 1.0
 */
public class MyAttendanceView extends View {

    private int radius;//小圆半径
    private int drawablePadding;//小圆与文字的间隔
    private int textSpace;//状态文字间的间隔

    private String weekText = "";//第几周
    private String weekTotal = "";//周工作时长
    private String monthTotal = "";//月工作时长
    private String weekOrMonth = "";//本月还是本周
    private boolean isMonth;//是否本周
    Bitmap icChange;//本周与本月的图标
    private Rect monthRect;


    private List<Integer> colors = new ArrayList<>();//进度条的颜色
    private List<String> attendanceStatus = new ArrayList<>();//考勤状态
    private List<String> weeks = new ArrayList<>();//一周的文字周文字
    private List<MyAttendanceEntity> attentances = new ArrayList<>();//考勤信息

    private int todayIndex = -1;//当天日期的位置
    private int selectDayIndex = -1;//点击后选中的位置
    private List<Rect> rects = new ArrayList<>();//每天日期的点击事件
    //    private OnItemClickListener itemClickListener;
    Paint paint;
    TextPaint textPaint;
    Paint.FontMetricsInt metrics;//文字的基线数据
    int textHeight;//文字的高
    int lineSpace = 20;//行间距
    int attentanceHeight;//考勤状态的高度，因为小圆的中心与文字的中心在一条线上，小圆的直径可能大于文字的高度，所以需要计算
    Drawable dayBg;//选中日期的背景框
    Drawable workTimebg;//每天排班信息的背景框

    public MyAttendanceView(Context context) {
        this(context, null);
    }

    public MyAttendanceView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyAttendanceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //小圆半径
        radius = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_4);
        //小圆与文字的间隔
        drawablePadding = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_5);
        //状态文字间的间隔
        textSpace = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_20);
        lineSpace = getContext().getResources().getDimensionPixelSize(com.sky.base.R.dimen.wh_14);

        //画圆的画笔
        paint = new Paint();
        paint.setAntiAlias(true);

        textPaint = new TextPaint();
        textPaint.setTextSize(getContext().getResources().getDimension(com.sky.base.R.dimen.text_12));
        metrics = textPaint.getFontMetricsInt();
        textHeight = metrics.bottom - metrics.top;

        dayBg = ContextCompat.getDrawable(getContext(), R.drawable.shape_circle_e2ebff);//今天的背景框
//        icChange = ContextCompat.getDrawable(getContext(), R.mipmap.ic_change);//本月本周切换
        icChange = BitmapFactory.decodeResource(getContext().getResources(), R.mipmap.ic_change);

        attentanceHeight = Math.max(textHeight, radius * 2);
        workTimebg = ContextCompat.getDrawable(getContext(), R.drawable.shape_f5f5f5_radius_10);

        colors.clear();
//        colors.add(R.color.color_BFE12A);
        colors.add(R.color.color_F86A55);
        colors.add(R.color.color_FFC512);
        colors.add(R.color.color_4C97EE);
        colors.add(R.color.color_8B6AFF);

        attendanceStatus.clear();
//        attendanceStatus.add("正常");
        attendanceStatus.add("缺卡");
        attendanceStatus.add("迟到早退");
        attendanceStatus.add("请假");
        attendanceStatus.add("外勤");

        weeks.clear();
        weeks.add("一");
        weeks.add("二");
        weeks.add("三");
        weeks.add("四");
        weeks.add("五");
        weeks.add("六");
        weeks.add("日");

        initAttendance();
    }

    private void initAttendance() {

//        weekText = "2022年05月 第四周";//第几周
        weekTotal = "累计：无";//周工作时长
        monthTotal = "累计：无";//月工作时长
        weekOrMonth = "本周";//本月还是本周

        long date = 24 * 60 * 60 * 1000;//一天的时间
        long current = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(current);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        StringBuilder builder = new StringBuilder();
        builder.append(year);
        builder.append("年");
        builder.append(month);
        builder.append(" 第");
        builder.append(week);
        builder.append("周");
        weekText = builder.toString();

        //周获取的是周日到周六，对应数字1到7，所以1，是周日，需要除去
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {//1是周日
            selectDayIndex = 6;//当天
            todayIndex = 6;//当天
        } else {//其他减2就行
            selectDayIndex = dayOfWeek - 2;//当天
            todayIndex = dayOfWeek - 2;//当天
        }

        attentances.clear();
        MyAttendanceEntity entity;
        for (int i = 0; i < 7; i++) {
            entity = new MyAttendanceEntity();
            entity.setDate(current - (selectDayIndex - i) * date);//时间戳

            calendar.setTimeInMillis(entity.getDate());//计算当前的日期
            entity.setDays(calendar.get(Calendar.DAY_OF_MONTH) + "");

            entity.setToday(selectDayIndex == i);
            entity.setWorkTime("无");
            entity.setMissingCard(i < 1);
            entity.setLateEarly(i < 2);
            entity.setLeaves(i < 3);
            entity.setOutdoor(i < 4);

            attentances.add(entity);
        }
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setDrawablePadding(int drawablePadding) {
        this.drawablePadding = drawablePadding;
    }

    public void setTextSpace(int textSpace) {
        this.textSpace = textSpace;
    }

    public void setWeekText(String weekText) {
        this.weekText = weekText;
    }

    public void setWeekTotal(String weekTotal) {
        this.weekTotal = weekTotal;
    }

    public void setMonthTotal(String monthTotal) {
        this.monthTotal = monthTotal;
    }

    public void setWeekOrMonth(String weekOrMonth) {
        this.weekOrMonth = weekOrMonth;
    }

    public void setMonth(boolean month) {
        isMonth = month;
    }

    public void setIcChange(Bitmap icChange) {
        this.icChange = icChange;
    }

    public void setMonthRect(Rect monthRect) {
        this.monthRect = monthRect;
    }

    public void setColors(List<Integer> colors) {
        this.colors = colors;
    }

    public void setAttendanceStatus(List<String> attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public void setWeeks(List<String> weeks) {
        this.weeks = weeks;
    }

    public void setAttentances(List<MyAttendanceEntity> attentances) {
        this.attentances = attentances;
        for (int i = 0; i < attentances.size(); i++) {
            if (attentances.get(i).isToday()) {
                selectDayIndex = i;
                todayIndex = i;
            }
        }
    }

    public void setRects(List<Rect> rects) {
        this.rects = rects;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public void setTextPaint(TextPaint textPaint) {
        this.textPaint = textPaint;
    }

    public void setMetrics(Paint.FontMetricsInt metrics) {
        this.metrics = metrics;
    }

    public void setTextHeight(int textHeight) {
        this.textHeight = textHeight;
    }

    public void setLineSpace(int lineSpace) {
        this.lineSpace = lineSpace;
    }

    public void setAttentanceHeight(int attentanceHeight) {
        this.attentanceHeight = attentanceHeight;
    }

    public void setDayBg(Drawable dayBg) {
        this.dayBg = dayBg;
    }

    public void setWorkTimebg(Drawable workTimebg) {
        this.workTimebg = workTimebg;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        //高等于上下间距+第一行文字高度+行间距+考勤状态高度+行间距+周文字高度+行间距+日文字背景框高度（有侵占这行的行间距）+圆直径+行间距+三倍文字高度+行间距
        int height = (int) (getPaddingTop() + getPaddingBottom() + textHeight + lineSpace + attentanceHeight + lineSpace + textHeight + lineSpace + textHeight * 1.5 + radius * 2 + lineSpace + textHeight * 3 + lineSpace);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor(getContext().getResources().getColor(R.color.color_333333));
        canvasLineText(canvas);
        canvasTwoText(canvas);
        canvasThreeText(canvas);
    }

    private void canvasLineText(Canvas canvas) {
        int left = getPaddingLeft();
        int baseline = getPaddingTop() - metrics.top;
        //第几周
        canvas.drawText(weekText, left, baseline, textPaint);
        if (isMonth) {
            left = (int) (left + textPaint.measureText(weekText) + textSpace);
            //累计时长
            canvas.drawText(monthTotal, left, baseline, textPaint);

            weekOrMonth = "本月";
            left = (int) (left + textPaint.measureText(monthTotal) + textSpace);
        } else {
            left = (int) (left + textPaint.measureText(weekText) + textSpace);
            //累计时长
            canvas.drawText(weekTotal, left, baseline, textPaint);

            weekOrMonth = "本周";
            left = (int) (left + textPaint.measureText(weekTotal) + textSpace);
        }
        //本周与本月的切换图标
        Bitmap ic = Bitmap.createBitmap(icChange, 0, 0, icChange.getWidth(), icChange.getHeight(), null, true);
        canvas.drawBitmap(ic, left, getPaddingTop() + (textHeight - icChange.getHeight()) / 2, null);
        monthRect = new Rect(left, getPaddingTop(), (int) (left + icChange.getWidth() + 10 + textPaint.measureText(weekOrMonth)), getPaddingTop() + textHeight * 2);

        left = left + icChange.getWidth() + 10;
        textPaint.setColor(getContext().getResources().getColor(R.color.color_89a5b1));
        canvas.drawText(weekOrMonth, left, baseline, textPaint);
    }

    private void canvasTwoText(Canvas canvas) {
        textPaint.setColor(getContext().getResources().getColor(R.color.color_666666));
        //考勤状态开始的y点
        int attendanceTop = getPaddingTop() + textHeight + lineSpace;
        int left = getPaddingLeft();
        //计算圆心，需要比较半径与文字高度的一半比较大小
        int textWidthTotal = 0;//文字的累计宽度
        int baseline = attendanceTop + attentanceHeight / 2 + textHeight / 2 - metrics.bottom;
        //开始绘制考勤状态
        for (int i = 0; i < attendanceStatus.size(); i++) {
            paint.setColor(getContext().getResources().getColor(colors.get(i)));
            //小圆
            canvas.drawCircle(left + radius + textWidthTotal + (radius * 2 + drawablePadding + textSpace) * i, attendanceTop + attentanceHeight / 2, radius, paint);
            //文字
            canvas.drawText(attendanceStatus.get(i), left + textWidthTotal + (radius * 2 + drawablePadding) * (i + 1) + textSpace * i, baseline, textPaint);
            //文字的宽度需要累加
            textWidthTotal += textPaint.measureText(attendanceStatus.get(i));
        }
    }

    private void canvasThreeText(Canvas canvas) {
        if (attentances == null || attentances.size() == 0) return;
        int left = getPaddingLeft();
        int right = getPaddingRight();
        int width = getMeasuredWidth() - left - right;//占据的宽度

        //周文字居中显示
        textPaint.setColor(getContext().getResources().getColor(R.color.color_333333));
        textPaint.setTextAlign(Paint.Align.CENTER);

        //第二行：周文字距离考勤状态的距离;计算周文字开始的Y值
        int weekTop = getPaddingTop() + textHeight + lineSpace + attentanceHeight + lineSpace;
        //weeks把宽分成分七份，文字居中
        int everyWidth = width / 7;
        int weekBaseline = weekTop - metrics.top;//周文字的基线

        int dayTop = weekTop + textHeight + lineSpace;
        int dayBaseline = dayTop - metrics.top;//每天日期的基线
        int dayBottom = (int) (dayTop + textHeight * 1.5);//日期的底部边

        //每天排班信息的背景框
        Rect textBgRect = new Rect(left,
                dayBottom + radius * 2 + lineSpace,
                width,
                dayBottom + radius * 2 + lineSpace + textHeight * 3);
        workTimebg.setBounds(textBgRect);
        workTimebg.draw(canvas);
        //当天排班信息的基线
        int workInfoLine = textBgRect.top + textHeight - metrics.top;

        MyAttendanceEntity entity;
        Rect dayBgRect;//item背景框所在矩形
        rects.clear();//点击事件的矩形先清空
        int dayCenterX;//日期的中心X
        int dayCenterY;//日期的中心Y
        for (int i = 0; i < weeks.size(); i++) {
            dayCenterX = left + everyWidth / 2 + everyWidth * i;
            //画周文字
            canvas.drawText(weeks.get(i), dayCenterX, weekBaseline, textPaint);

            //开始计算日期文字
            entity = attentances.get(i);
            //日期文字的背景框是一个正方形，两倍文字高度，所以以中心点来计算
            dayCenterY = dayTop + textHeight / 2;
            dayBgRect = new Rect(dayCenterX - textHeight, dayCenterY - textHeight, dayCenterX + textHeight, dayCenterY + textHeight);
            rects.add(dayBgRect);//保存需要点击日期背景框位置，比对点击的位置
            //画对应天的对应日期
            canvas.drawText(entity.getDays(), dayCenterX, dayBaseline, textPaint);

            if (todayIndex < i) continue;
            //画每天的状态。
            List<Integer> status = entity.getDayStatus();
            if (status.size() == 0) continue;
            int startX = dayCenterX - radius * (status.size() + 1) / 2 + radius;
            for (int j = 0; j < status.size(); j++) {
                paint.setColor(getContext().getResources().getColor(colors.get(status.get(j))));
                canvas.drawCircle(startX + radius * j, dayBottom + radius, radius, paint);
            }
        }
        //画选中的日期
        entity = attentances.get(selectDayIndex);
        //画选中日期的背景框
        dayBg.setBounds(rects.get(selectDayIndex));
        dayBg.draw(canvas);
        //选中日期的排班信息
        textPaint.setColor(getContext().getResources().getColor(R.color.color_666666));
        textPaint.setTextAlign(Paint.Align.LEFT);//排班信息是居左的，所以设为文字居左
        canvas.drawText(entity.getWorkTime(), left + everyWidth / 2, workInfoLine, textPaint);//当天的排班信息

        //选中日期的颜色
        textPaint.setTextAlign(Paint.Align.CENTER);//日期文字是居中的
        textPaint.setColor(getContext().getResources().getColor(R.color.color_437DFF));

        canvas.drawText(entity.getDays(), rects.get(selectDayIndex).centerX(), dayBaseline, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                onItemClick((int) event.getX(), (int) event.getY());
                onMonthClick((int) event.getX(), (int) event.getY());
                invalidate();
                break;
        }
        return true;
    }

    private void onMonthClick(int x, int y) {
        if (monthRect.contains(x, y)) {
            isMonth = !isMonth;
        }
    }

    private void onItemClick(int x, int y) {
        int index = -1;
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).contains(x, y)) {
                index = i;
            }
        }
        if (index < 0) {
            return;
        }
        selectDayIndex = index;
//        if (itemClickListener != null)
//            itemClickListener.onItemClick(index);
    }

//    public interface OnItemClickListener {
//        void onItemClick(int position);
//    }

}