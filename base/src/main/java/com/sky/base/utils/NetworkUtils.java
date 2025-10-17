package com.sky.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

/**
 * Created by SKY on 16/5/10 下午3:50.
 * 网络判断
 */
public class NetworkUtils {
    private static ConnectivityManager getConnect(Context context) {
        return (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 判断网络是否连接，推荐
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = getConnect(context).getActiveNetworkInfo();
        if (info == null) return false;
        return info.isConnected();
    }

    /**
     * 判断是否是gprs网络，即移动网络
     */
    public static boolean isMobile(Context context) {
        NetworkInfo info = getConnect(context).getActiveNetworkInfo();
        if (info == null) return false;
        return info.getType() == ConnectivityManager.TYPE_MOBILE;
    }

    /**
     * 判断wifi是否连接
     */
    public static boolean isWifi(Context context) {
        NetworkInfo info = getConnect(context).getActiveNetworkInfo();
        if (info == null) return false;
        return info.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 打开wifi设置页面
     */
    public static void openWifiSettting(Context context) {
        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

    /**
     * 打开WiFi
     */
    @SuppressLint("MissingPermission")
    public static void openWifi(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(true);
    }

    private static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 判断GPS是否打开
     */
    public static boolean isGpsEnabled(Context context) {
        return getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 强制帮用户打开GPS
     *
     * @param context
     */
    public static final void openGPS(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
    }

    /**
     * 判断基站定位是否开启，移动位置服务
     */
    public static boolean isLBSEnabled(Context context) {
        return getLocationManager(context).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

}
