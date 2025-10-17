package com.sky.oa.data.model

/**
 * 用户列表界面状态类
 * 封装了用户列表界面可能的所有状态
 */
data class UserListState(
    val users: List<User> = emptyList(), // 用户列表数据
    val isLoading: Boolean = false,      // 是否正在加载
    val error: String? = null            // 错误信息
)