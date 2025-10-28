package com.sky.oa.data.repository

import com.google.gson.reflect.TypeToken
import com.sky.base.ApiResponse
import com.sky.base.utils.LogUtils
import com.sky.oa.data.model.CourseEntity
import com.sky.oa.gson.GsonUtils
import com.sky.oa.http.RetrofitClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 23:12
 * @Description:
 */
class HttpRepository {

    //    type=4&num=30
    suspend fun getTeacher(type: Int = 4, num: Int = 30): Result<List<CourseEntity>?> =
        try {
            val response = RetrofitClient.httpApi.getTeacher(type, num)
            if (response.code == 0) {
                Result.success(response.data)
            } else {
                Result.failure(Exception(response.msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    suspend fun getTeachers(type: Int,num: Int): ApiResponse<List<CourseEntity>>{
        return RetrofitClient.httpApi.getTeachers(type,num)
    }

    fun getImageUrl() {
//        LogUtils.i("开始请求数据")
        val url = "https://www.imooc.com/api/teacher?type=4&num=30"
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get() //默认就是GET请求，可以不写
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtils.i("数据==${e.localizedMessage}")
            }

            override fun onResponse(call: Call, response: Response) {
                val data = GsonUtils.fromJson<ApiResponse<MutableList<CourseEntity>>>(response.body?.string(),
                    object : TypeToken<ApiResponse<MutableList<CourseEntity>>>() {}.type)
                LogUtils.i(data.data.toString())
//                LogUtils.i("数据==${data.status}")
            }
        })
    }
}