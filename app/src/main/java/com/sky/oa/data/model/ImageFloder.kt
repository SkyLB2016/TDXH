package com.sky.oa.data.model

import java.io.Serializable

/**
 * Created by SKY on 16/5/10 下午3:50.
 * 文件夹
 */
class ImageFloder : Serializable {
    var dirPath: String? = null//文件夹的路径
        set(dirPath) {
            field = dirPath
            val last = dirPath?.lastIndexOf("/")
            this.name = dirPath!!.substring(last!! + 1)
        }
    var name: String? = null
        private set//文件夹得名称
    var firstImagePath: String? = null//文件夹中的第一张图片，用于显示
    var count: Int = 0//文件夹内包含的图片数量
}
