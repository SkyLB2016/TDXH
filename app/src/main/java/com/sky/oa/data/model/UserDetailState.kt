package com.sky.oa.data.model

/**
 * 用户详情界面状态类
 * 封装了用户详情界面可能的所有状态
 */
data class UserDetailState(
    val user: User? = null,              // 用户详情数据
    val isLoading: Boolean = false,      // 是否正在加载
    val error: String? = null            // 错误信息
)