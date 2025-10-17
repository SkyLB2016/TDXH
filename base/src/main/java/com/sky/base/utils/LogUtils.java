package com.sky.base.utils;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by SKY on 16/5/10 下午3:50.
 * Logcat统一管理类
 */
public class LogUtils {

    private LogUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;// 是否需要打印bug，debug与release的buildconfig，也可以自定义
    private static final String TAG = "SKY";//默认情况下的前缀

    public static void setIsDebug(boolean debug) {
        isDebug = debug;
    }

    private static String generateTag() {
        //此方法获取的栈的第零条数据是当前方法所在位置，以此类推，idev四个方法就是第一条，在之上的入口位置就是第二条
//        StackTraceElement stack = new Throwable().getStackTrace()[2];

        //第二条数据是当前方法的所在位置。第四条数据就是所调用的方法的位置
        StackTraceElement stack = Thread.currentThread().getStackTrace()[4];
//        String tag = "%s.%s(L:%d)";
        String tag = "%s(L:%d)";
        String className = stack.getClassName();
        className = className.substring(className.lastIndexOf(".") + 1);
//        tag = String.format(tag, className, stack.getMethodName(), stack.getLineNumber());
        tag = String.format(tag, stack.getMethodName(), stack.getLineNumber());
        tag = TextUtils.isEmpty(TAG) ? tag : TAG + ":" + tag;
        return tag;
    }

    // 下面四个是默认tag的函数
    public static void i(String msg) {
//        tag 标志是方法名加行数
        Log.i("generateTag==",generateTag());
        if (isDebug) Log.i(generateTag(), msg);
    }

    public static void d(String msg) {
        if (isDebug) Log.d(generateTag(), msg);
    }

    public static void e(String msg) {
        if (isDebug) Log.e(generateTag(), msg);
    }

    public static void v(String msg) {
        if (isDebug) Log.v(generateTag(), msg);
    }

    // 下面是传入自定义tag的函数
    public static void i(String tag, String msg) {
        if (isDebug) Log.i(tag, msg);
    }

    public static void d(String tag, String msg) {
        if (isDebug) Log.i(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isDebug) Log.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (isDebug) Log.i(tag, msg);
    }
}