package com.sky.base.utils;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * Created by SKY on 2015/8/17 10:15
 * sd卡工具类
 */
public class SDCardUtils {
    private SDCardUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }
//    getBlockCountLong() 文件系统中总的存储区块的数量
//    getBlockSizeLong() 文件系统中每个存储区块的字节数
//    getAvailableBlocksLong()  文件系统中可被应用程序使用的空闲存储区块的数量
//    getAvailableBytes()  文件系统中可被应用程序使用的空闲字节数
//    getFreeBlocksLong() 文件系统中总的空闲存储区块的数量，包括保留的存储区块（不能被普通应用程序使用）
//    getFreeBytes() 文件系统中总的空闲字节数，包括保留的存储区块（不能被普通应用程序使用）
//    getTotalBytes() 文件系统支持的总的存储字节数

    /**
     * 判断SDCard是否可读写,true可读
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取SD卡路径'/storage/emulated/0/'
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * 获取系统存储路径 '/system'
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 获取SD卡总量 单位byte
     */
    public static long getTotalBytes() {
        if (!isSDCardEnable()) return 0;
        return new StatFs(getSDCardPath()).getTotalBytes();
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     */
    public static long getFreeBytes() {
        if (!isSDCardEnable()) return 0;
        return new StatFs(getSDCardPath()).getFreeBytes();
    }

}
