package com.sky.base.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by SKY on 16/5/10 下午3:50.
 * Toast统一管理类
 */
public class ToastUtils {

    private ToastUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void showShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showLong(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showWithCustomView(Context context, int layoutId, int tvId, String message) {
        View view = LayoutInflater.from(context).inflate(layoutId, null);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        TextView tv = view.findViewById(tvId);
        tv.setText(message);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}