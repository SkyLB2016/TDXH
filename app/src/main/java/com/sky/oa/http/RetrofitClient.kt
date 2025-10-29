package com.sky.oa.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 0:36
 * @Description:
 */
object RetrofitClient {
    //
    private const val BASE_URL="https://www.imooc.com/"
    private val logging= HttpLoggingInterceptor().apply {
     level=HttpLoggingInterceptor.Level.BODY
    }
    private val okClient= OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val httpApi: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(ApiService::class.java)
    }

}