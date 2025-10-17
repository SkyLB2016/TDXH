package com.sky.oa.utils.imageloader

import android.graphics.Bitmap

/**
 * Created by SKY on 2018/6/25 16:29.
 */
class DoubleCache : ImageCache {
    private var memory: MemoryCache = MemoryCache()

    private var disk: DiskCache = DiskCache()

    override fun get(url: String): Bitmap? = memory.get(url) ?: disk.get(url)

    override fun put(url: String, bitmap: Bitmap?) {
        memory.put(url, bitmap)
        disk.put(url, bitmap)
    }
}
