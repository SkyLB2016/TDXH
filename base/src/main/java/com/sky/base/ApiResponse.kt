/*
 * Copyright (C) 2015. Keegan小钢（http://keeganlee.me）
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sky.base

/**
 * Created by SKY on 15/12/9 下午8:54.
 * Api响应结果的封装类.
 */
data class ApiResponse<T>(
    var code: Int = 0// 返回状态码,"0"代表成功
    , var msg: String? = null // 返回信息
) {
    var obj: T? = null// 单个对象
    var objList: MutableList<T>? = null// 数组对象
    var currentPage = 0// 当前页数
    var pageSize = 0// 每页显示数量
    var maxCount = 0// 总条数
    var maxPage = 0// 总页数

//    var data: List<T>? = null
    var data: T? = null
    var status = 0// 返回状态码,"1"代表成功

    val isSuccess: Boolean
        get() = code == 0
}