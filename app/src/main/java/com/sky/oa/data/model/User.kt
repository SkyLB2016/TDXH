package com.sky.oa.data.model
// ============= 数据模型层 =============

/**
 * 用户数据模型类
 * 使用 Kotlin 数据类，自动生成 equals、hashCode、toString 等方法
 */
data class User(
    val id: Int,
    val name: String,
    val email: String,
    val avatar: String = "" // 可选参数，有默认值
)
