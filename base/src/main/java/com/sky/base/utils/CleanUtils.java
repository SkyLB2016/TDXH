package com.sky.base.utils;

import android.content.Context;

/**
 * Created by SKY on 2015/3/26 17:41.
 * 本地数据清除，主要功能有清除内/外缓存，清除数据库，清除sP，清除files和清除自定义目录
 */
public class CleanUtils {
    /**
     * 清除本应用内部缓存(/data/data/包名/cache)
     */
    public static void cleanInternalCache(Context context) {
        FileUtils.deleteFile(context.getCacheDir());
    }

    /**
     * 清除外部cache下的内容
     * (/storage/emulated/0/Android/data/包名/cache)
     * (/mnt/sdcard/android/data/包名/cache)
     * /storage/emulated/0===/mnt/sdcard/
     */
    public static void cleanExternalCache(Context context) {
        FileUtils.deleteFile(context.getExternalCacheDir());
    }

    /**
     * 清除/data/data/包名/files下的内容
     */
    public static void cleanFiles(Context context) {
        FileUtils.deleteFile(context.getFilesDir());
    }

    /**
     * 清除/storage/emulated/0/Android/data/com.sky.ch/files
     */
    public static void cleanExternalFiles(Context context) {
        FileUtils.deleteFile(context.getExternalFilesDir(""));
    }

    /**
     * 清除本应用SharedPreference(/data/data/包名/shared_prefs)
     */
    public static void cleanSP(Context context) {
        FileUtils.deleteFile("/data/data/" + context.getPackageName() + "/shared_prefs");
    }

    /**
     * 清除自定义路径下的文件
     *
     * @param filePath
     */
    public static void cleanCustomCache(String filePath) {
        FileUtils.deleteFile(filePath);
    }

    /**
     * 按名字清除本应用数据库
     *
     * @param dbName
     */
    public static void cleanDBName(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }

    /**
     * 清除本应用所有数据库(/data/data/包名/databases)
     */
    public static void cleanDatabases(Context context) {
        FileUtils.deleteFile("/data/data/" + context.getPackageName() + "/databases");
    }

    /**
     * 清除本应用所有的数据
     */
    public static void cleanApp(Context context) {
        cleanApp(context, "");
    }

    /**
     * 清除本应用所有的数据
     */
    public static void cleanApp(Context context, String... filepath) {
        FileUtils.deleteFile("/data/data/" + context.getPackageName());
        cleanExternalCache(context);
        cleanExternalFiles(context);
        cleanSP(context);
        cleanDatabases(context);
        for (String filePath : filepath) {
            cleanCustomCache(filePath);
        }
    }
}