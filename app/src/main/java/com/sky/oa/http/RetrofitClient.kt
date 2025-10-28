package com.sky.oa.http

import com.google.gson.reflect.TypeToken
import com.sky.base.ApiResponse
import com.sky.base.utils.LogUtils
import com.sky.oa.data.model.CourseEntity
import com.sky.oa.gson.GsonUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 0:36
 * @Description:
 */
object RetrofitClient {
    //
    private const val BASE_URL="https://www.imooc.com/"
    private val logging= HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okClient= OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val httpApi: HttpApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(HttpApi::class.java)
    }

}