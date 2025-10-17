package com.sky.base.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by SKY on 2015/8/10 10:30
 * 屏幕相关辅助类
 */
public class ScreenUtils {
    private ScreenUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取屏幕密度
     */
    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }

    /**
     * 获取屏幕宽度
     */
    public static int getWidthPX(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static int getHeightPX(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * dp转px
     */
    public static int dpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context));
    }

    /**
     * sp转px
     */
    public static int spTopx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDisplayMetrics(context));
    }

    /**
     * px转dp
     */
    public static float pxTodp(Context context, float px) {
        return px / getDensity(context);
    }

    /**
     * px转sp
     */
    public static float pxTosp(Context context, float px) {
        return px / getDisplayMetrics(context).scaledDensity;
    }

    private static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * 获得状态栏的高度
     */
    public static int getStatusHeight(Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            return context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            LogUtils.d(e.toString());
        }
        return 0;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Bitmap bp = Bitmap.createBitmap(bmp, 0, 0, getWidthPX(activity), getHeightPX(activity));
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();

        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        Bitmap bp = Bitmap.createBitmap(bmp, 0, statusBarHeight,
                getWidthPX(activity), getHeightPX(activity) - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

}
