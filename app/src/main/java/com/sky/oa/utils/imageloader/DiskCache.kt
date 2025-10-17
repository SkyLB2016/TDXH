package com.sky.oa.utils.imageloader

import android.graphics.Bitmap
import com.sky.base.utils.BitmapUtils
import com.sky.base.utils.MD5Utils
import java.io.File

/**
 * Created by SKY on 2018/6/25 14:50.
 */
class DiskCache : ImageCache {
    override fun get(url: String?): Bitmap? {
        val pathName = getPath(url)
        return if (File(pathName).exists()) BitmapUtils.getBitmapFromPath(pathName) else null
    }

    override fun put(url: String?, bitmap: Bitmap?) {
        val pathName = getPath(url)
        BitmapUtils.saveBitmapToFile(bitmap, pathName)
    }

    //通过MD5来确定唯一名称，hashCode也可以，但是单纯取最后的名称，有可能重名
    private fun getPath(url: String?) =
        if (url?.startsWith("http")!!)
            "/storage/emulated/0/and/image/${
                MD5Utils.encrypt(url) + url.substring(url.lastIndexOf("."))
            }"
        else url
//            if (url?.startsWith("http")!!) "$path${url.substring(url.lastIndexOf("/"))}" else url
}
