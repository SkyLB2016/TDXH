package com.sky.base.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.sky.base.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SKY on 2017/8/22.
 * app信息类
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated 不能被实例化*/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 获取应用程序版本名称信息
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }

    /**
     * 获取应用程序版本号
     */
    public static int getVersionCode(Context context) {
        return getPackageInfo(context).versionCode;
    }

    /**
     * 获取App包版本信息
     */
    public static PackageInfo getPackageInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            LogUtils.d(e.toString());
        }
        return null;
    }

    public static String getChannel(Context context) {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.get("CHANNEL").toString();
        } catch (NameNotFoundException e) {
            LogUtils.d(e.toString());
        }
        return null;
    }

    /**
     * 检查权限
     *
     * @param context
     * @param permission
     * @return true为已获得
     */
    public static boolean isPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查权限，未通过的单独组成一个数组向系统请求权限
     *
     * @param original    需要请求的权限数组，已通过的剔除掉，剩余的组成一个数组去请求权限
     * @param requestCode 权限请求成功后的请求码
     */
    public static void isPermissions(Activity activity, String[] original, int requestCode) {
        List<String> temps = new ArrayList<>();
        for (int i = 0; i < original.length; i++) {
            if (!isPermission(activity, original[i]))
                temps.add(original[i]);
        }
        if (temps.isEmpty()) return;
        String[] pers = new String[temps.size()];
        for (int i = 0; i < temps.size(); i++) {
            pers[i] = temps.get(i);
        }
        requestPermissions(activity, pers, requestCode);
    }

    /**
     * 请求权限
     *
     * @param permissions 需要请求的权限数组
     * @param requestCode 权限请求成功后的请求码
     */
    public static void requestPermissions(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 下载完成后 安装App
     *
     * @param file 要安装的文件
     */
    public static void installApp(Context context, File file) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)//适配7.0之后
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        else uri = Uri.fromFile(file);
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
//        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(install);
    }
}