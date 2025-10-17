package com.sky.oa.utils.imageloader

import android.graphics.Bitmap
import android.util.LruCache

/**
 * Created by SKY on 2018/6/25 14:50.
 */
class MemoryCache : ImageCache {
    private val memory: LruCache<String, Bitmap>

    init {
        memory = object : LruCache<String, Bitmap>((Runtime.getRuntime().maxMemory() / 4).toInt()) {
            override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount
        }
    }

    /**
     * 添加bitmap到lrucache中
     *
     * @param url
     * @param bitmap
     * @return
     */
    override fun put(url: String, bitmap: Bitmap?) {
        get(url) ?: memory.put(url, bitmap)
    }

    /**
     * 从缓存中获取bitmap
     *
     * @param url
     * @return
     */
    override fun get(url: String): Bitmap? = memory.get(url)
}
