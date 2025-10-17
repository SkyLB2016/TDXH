package com.sky.base.utils.uu;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.Stack;

/**
 * Created by SKY on 16/5/10 下午3:50.
 * activity管理类
 */
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
    private Stack<Activity> activities;
    private Activity current;
//    private static ActivityLifecycle instance;

    public static ActivityLifecycle getInstance() {
        //双重检索模式（Double Check Lock）DCL
//        if (instance == null)
//            //所有加上synchronized 和 块语句，在多线程访问的时候，同一时刻只能有一个线程能够用
//            //同步检查，获得锁，先清空工作内存，即子内存；然后从主内存中拷贝变量的新副本到子内存中，
//            // 执行后强制刷新主内存，并释放
//            synchronized (ActivityLifecycle.class) {
//                if (instance == null)
//                    instance = new ActivityLifecycle();
//            }
////        return instance;
        return SingletonHolder.sigleton;
    }

    private static class SingletonHolder {
        private static final ActivityLifecycle sigleton = new ActivityLifecycle();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activities == null) activities = new Stack<>();
        activities.add(activity);//堆入activities管理栈中
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        current = activity;
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);//销毁时从管理栈中移除
        if (current == activity) current = null;
    }

    public Activity getCurrent() {
        return current;
    }

    /**
     * @return 当前activity的位置
     */
    public int getLocation() {
        return activities.size() - 1;
    }

    /**
     * @param position 所要获取的activity的位置
     * @return 获取指定的activity
     */
    public Activity getActivity(int position) {
        return activities.get(position);
    }


    /**
     * 移除所有的activity
     */
    public void closeAll() {
        for (Activity act : activities) act.finish();
        activities.clear();
//        Iterator<Activity> iterator = activities.iterator();
//        while (iterator.hasNext()) {
//            iterator.next().finish();
//            iterator.remove();
//        }
//        while (!activities.isEmpty()) {
//            Activity act = activities.lastElement();
//            act.finish();
//            activities.remove(act);
//        }
    }

    /**
     * 返回到指定的activity
     */
    public void backToAct(Class cls) {
        while (!activities.lastElement().getClass().equals(cls)) {
            Activity act = activities.lastElement();
            act.finish();
            activities.remove(act);
        }
    }

    /**
     * 杀掉并移除 出去当前activity的所有页面
     */
    public void removeOther() {
        while (activities.size() != 1) {
            Activity act = activities.firstElement();
            act.finish();
            activities.remove(act);
        }
    }
}
