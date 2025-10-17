package com.sky.oa.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/5/16 6:24 下午
 * @Version: 1.0
 */
public class MyAttendanceEntity implements Serializable {
    private long date;//1653235200000,

    private String days;//"22",每月几号
    private boolean today;//false,是否是今天

    private String workTime;//"无",工作时长

//    private boolean over;//false,是否加班
//    private boolean onBusiness;//false,是否出差

    private boolean missingCard;//是否缺卡
    private boolean lateEarly;//是否迟到早退
    private boolean leaves;//是否休假
    private boolean outdoor;//是否外勤

    public MyAttendanceEntity() {
    }

    public MyAttendanceEntity(String days, boolean today) {
        this.days = days;
        this.today = today;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public boolean isToday() {
        return today;
    }

    public void setToday(boolean today) {
        this.today = today;
    }

    public String getWorkTime() {
        return workTime;
    }

    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

    public void setMissingCard(boolean missingCard) {
        this.missingCard = missingCard;
    }

    public void setLateEarly(boolean lateEarly) {
        this.lateEarly = lateEarly;
    }

    public void setLeaves(boolean leaves) {
        this.leaves = leaves;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }

    public List<Integer> getDayStatus() {
        List<Integer> status = new ArrayList<>();
//        if (!over && !leaves && !onBusiness && !outdoor) {
//            status.add(0);
//            return status;
//        }
        if (missingCard) {//是否缺卡
            status.add(0);
        }
        if (lateEarly) {//是否迟到早退
            status.add(1);
        }
        if (leaves) {//是否休假
            status.add(2);
        }
        if (outdoor) {//是否外勤
            status.add(3);
        }
        return status;
    }

}
