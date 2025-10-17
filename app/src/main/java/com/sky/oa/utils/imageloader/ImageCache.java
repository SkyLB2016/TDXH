package com.sky.oa.utils.imageloader;

import android.graphics.Bitmap;

/**
 * Created by SKY on 2018/6/25 16:20.
 */
public interface ImageCache {
    Bitmap get(String url);

    void put(String url, Bitmap bitmap);
}
