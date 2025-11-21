package com.sky.oa.data.repository

import com.google.gson.reflect.TypeToken
import com.sky.base.ApiResponse
import com.sky.base.ApiResult
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
            // 统一异常处理：网络异常、解析异常等
            when (e) {
//                31版本才有
//                is HttpException -> {
//                    val errorCode = e.code()
//                    when (errorCode) {
//                        404 -> ApiResult.Error("城市不存在: $city")
//                        401 -> ApiResult.Error("API密钥无效")
//                        else -> ApiResult.Error("服务器错误: $errorCode")
//                    }
//                }
                is IOException -> {
                    Result.failure(e)
//                    ApiResult.Error("网络连接失败，请检查网络设置")
                }

                else -> {
                    Result.failure(e)
//                    ("未知错误: ${e.message}")
                }
            }
        }

    suspend fun getCourses(type: Int, num: Int): ApiResponse<List<CourseEntity>> {
        return RetrofitClient.httpApi.getTeachers(type, num)
    }
    suspend fun getTeachers(type: Int, num: Int): ApiResult<ApiResponse<List<CourseEntity>>> {
        return try {
            val response = RetrofitClient.httpApi.getTeachers(type, num)
            ApiResult.Success(response)
        } catch (e: Exception) {
            // 统一异常处理：网络异常、解析异常等
            when (e) {
//                31版本才有
//                is HttpException -> {
//                    val errorCode = e.code()
//                    when (errorCode) {
//                        404 -> ApiResult.Error("城市不存在: $city")
//                        401 -> ApiResult.Error("API密钥无效")
//                        else -> ApiResult.Error("服务器错误: $errorCode")
//                    }
//                }
                is IOException -> {
                    ApiResult.Error("网络连接失败，请检查网络设置")
                }

                else -> {
                    ApiResult.Error("未知错误: ${e.message}")
                }
            }
        }
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
                val data =
                    GsonUtils.fromJson<ApiResponse<MutableList<CourseEntity>>>(
                        response.body?.string(),
                        object : TypeToken<ApiResponse<MutableList<CourseEntity>>>() {}.type
                    )
                LogUtils.i(data.data.toString())
//                LogUtils.i("数据==${data.status}")
            }
        })
    }
}