package com.sky.base
/**
 * @Author: 李彬
 * @TIME: 2025/10/11 14:53
 * @Description: API响应包装类 密封类
 */
sealed class ApiResult<out T> {
    object Loading : ApiResult<Nothing>()

    data class Success<T>(val datas: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

// 增强版ApiResult，支持更多状态
sealed class ApiResult1<out T> {
    data class Success<T>(val data: T) : ApiResult1<T>()
    data class Error(
        val message: String,
        val errorType: ErrorType = ErrorType.UNKNOWN,
        val retryAction: (() -> Unit)? = null  // 重试回调
    ) : ApiResult1<Nothing>()

    object Loading : ApiResult1<Nothing>()
    object Empty : ApiResult1<Nothing>()  // 空数据状态
}

enum class ErrorType {
    NETWORK,    // 网络错误
    SERVER,     // 服务器错误
    CLIENT,     // 客户端错误
    UNKNOWN     // 未知错误
}