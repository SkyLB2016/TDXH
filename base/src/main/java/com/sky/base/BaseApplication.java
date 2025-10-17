package com.sky.base;

import android.app.Application;

/**
 * Created by libin on 2020/05/08 5:58 PM Friday.
 */
public class BaseApplication extends Application {
    public static Application app;

    public static boolean debug = false;

    public static void setDebug(boolean d) {
        debug = d;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }
}
