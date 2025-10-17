package com.sky.oa.http

import com.sky.base.ApiResponse
import com.sky.oa.entity.CourseEntity
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 0:21
 * @Description:
 */
interface HttpApi {
    @GET("api/teacher")
    suspend fun getTeacher(
        @Query("type") type: Int,
        @Query("num") num: Int
    ): ApiResponse<List<CourseEntity>>

    @GET("api/teacher")
    suspend fun getTeachers(
        @Query("type") type: Int,
        @Query("num") num: Int
    ): ApiResponse<List<CourseEntity>>

}