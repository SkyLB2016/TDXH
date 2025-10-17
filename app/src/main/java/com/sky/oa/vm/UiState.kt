package com.sky.oa.vm
/**
 * @Author: 李彬
 * @TIME: 2025/10/11 14:53
 * @Description: UI 状态密封类
 */
sealed interface UiState<out T> {
    object Loading : UiState<Nothing>

    data class Success<T>(val datas: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}