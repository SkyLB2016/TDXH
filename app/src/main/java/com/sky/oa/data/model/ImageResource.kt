package com.sky.oa.data.model

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 17:29
 * @Description:
 */
data class ImageResource(
    val name: String,   //名称
    val resId:Int,      // id
    val type: String,    // drawable 、mipmap
    val mimeType: String? //后缀
)