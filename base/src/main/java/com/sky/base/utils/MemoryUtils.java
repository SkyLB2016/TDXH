package com.sky.base.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.sky.base.utils.LogUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by SKY on 2015/4/12.
 * 获取空间大小
 * MemTotal:        2758932 kB
 * MemFree:           74616 kB
 * MemAvailable:     649880 kB
 * Buffers:           30004 kB
 * Cached:           733408 kB
 * SwapCached:         8924 kB
 * Active:           856568 kB
 * Inactive:         866112 kB
 * Active(anon):     553096 kB
 * Inactive(anon):   562812 kB
 * Active(file):     303472 kB
 * Inactive(file):   303300 kB
 * Unevictable:      144132 kB
 * Mlocked:          144132 kB
 * SwapTotal:       1048572 kB
 * SwapFree:         677776 kB
 * Dirty:                 4 kB
 * Writeback:             0 kB
 * AnonPages:       1098260 kB
 * Mapped:           430520 kB
 * Shmem:             12764 kB
 * Slab:             210228 kB
 * SReclaimable:      63388 kB
 * SUnreclaim:       146840 kB
 * KernelStack:       46896 kB
 * PageTables:        48272 kB
 * NFS_Unstable:          0 kB
 * Bounce:                0 kB
 * WritebackTmp:          0 kB
 * CommitLimit:     2428036 kB
 * Committed_AS:   89864440 kB
 * VmallocTotal:   258998208 kB
 * VmallocUsed:      246476 kB
 * VmallocChunk:   258613732 kB
 */
public class MemoryUtils {
    /**
     * @return 手机总内存，读取手机配置文件获取
     */
    public static long getTotalBytes() {
        String dir = "/proc/meminfo";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
        } catch (IOException e) {
            LogUtils.d(e.toString());
        }
        return 0;
    }

    /**
     * @return 手机总内存
     */
    public static long getTotalBytes(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }

    /**
     * @return 手机剩余可用的内存
     */
    public static long getFreeBytes(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * App运行所能获取的最大内存，超过则崩溃，即OOM
     */
    public static long getAppRunMaxBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    /**
     * App当前运行中用到的总内存，随时变化，最多到MAX
     */
    public static long getAppRunTotalBytes() {
        return Runtime.getRuntime().totalMemory();
    }

    /**
     * App运行总内存中尚未用到的部分，随时变化
     */
    public static long getAppRunFreeBytes() {
        return Runtime.getRuntime().freeMemory();
    }

//    public static void getRun(Context context) {
//        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        manager.getMemoryClass();//应用所能获取到的最大内存
//        manager.getLargeMemoryClass();//应用经过扩容后所能获取到的最大内存，一般翻倍
//    }
}