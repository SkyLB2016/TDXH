package com.sky.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.sky.base.R;

/**
 * Created by SKY on 16/5/10 下午3:50.
 * dialog管理类
 */
public final class DialogLoading {

    private static Dialog loading;//测试启动两次是什么样的，静态变量会不会冲突

    public static Dialog createDialog(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setAlpha((float) 0.8);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setBackgroundResource(R.drawable.dialog_rotate);
        AnimationDrawable drawable = (AnimationDrawable) imageView.getBackground();
        drawable.start();
        loading = new AlertDialog.Builder(context, R.style.loading_dialog).setView(imageView).create();// 创建自定义样式dialog
        return loading;
    }

    public static void showDialog(Context context) {
        if (loading == null) createDialog(context);
        loading.show();
    }

    public static void disDialog() {
        if (loading == null) return;
        loading.dismiss();
    }

    public static void setCancelable() {
        if (loading == null) return;
        loading.setCancelable(false);//不可以用“返回键”取消
    }

    public static void setCanceledOnTouchOutside() {
        if (loading == null) return;
        loading.setCanceledOnTouchOutside(false);//点击外部不消失
    }
}
