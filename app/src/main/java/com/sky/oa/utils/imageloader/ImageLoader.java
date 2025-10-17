package com.sky.oa.utils.imageloader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.sky.base.utils.DiskLruCache;
import com.sky.base.utils.MD5Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by SKY on 2018/5/22 10:18.
 */
public class ImageLoader {
    private DiskLruCache diskLruCache;

    public ImageLoader() {
        int maxSize = (int) (Runtime.getRuntime().maxMemory() / 4);
        try {
            diskLruCache = DiskLruCache.open(new File("/storage/emulated/0/image/"), 1, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromDisk(String url) {
        Bitmap bitmap = null;
        String key = MD5Utils.encryption(url);
//            key = String.valueOf(url.hashCode());
        try {
            DiskLruCache.Snapshot snap = diskLruCache.get(key);
            FileInputStream is = (FileInputStream) snap.getInputStream(0);
            FileDescriptor fd = is.getFD();
            bitmap = BitmapFactory.decodeFileDescriptor(fd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void addBitmapFromDisk(String url, Bitmap bitmap) throws IOException {
        String key = MD5Utils.encryption(url);
//            key = String.valueOf(url.hashCode());
        try {
            DiskLruCache.Editor editor = diskLruCache.edit(key);
            OutputStream os = editor.newOutputStream(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        diskLruCache.flush();
    }
}
