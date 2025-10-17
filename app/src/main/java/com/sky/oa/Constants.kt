package com.sky.oa

/**
 * Created by SKY on 2017/3/3.
 */
object Constants {
    const val SOLAR = 2001

    //    val BASE_URL = "http://uat.b.quancome.com/platform/api"
    private const val TEST_BASE_URL = "http://test.services.banyunbang.com.cn/"//测试请求地址
    private const val TEST_IMAGE_URL = "http://test.mg.banyunbang.com.cn/"//图片请求地址
    val url: String
        get() = TEST_BASE_URL

    val imageUrl: String
        get() = TEST_IMAGE_URL
}
