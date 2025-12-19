package com.sky.base.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sky.base.utils.LogUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 16:36
 * @Description:
 */
open class BaseViewModel: ViewModel() {
    // 1. 创建一个可变的 StateFlow 用于存储错误信息
    private val _error = MutableStateFlow<String?>(null)
    // 2. 提供只读的 StateFlow 给外部观察
    val error: StateFlow<String?> = _error.asStateFlow()

//    使用 CoroutineExceptionHandler（适用于全局异常处理）
    val handler = CoroutineExceptionHandler { _, exception ->
        println("协程异常: $exception")
    }
//
//    viewModelScope.launch(handler) {
//        throw RuntimeException("出错了！")
//    }

    /**
     * 在 ViewModelScope 中启动协程，统一处理异常
     * 这是一个便捷方法，避免在每个子类中重复写 try-catch
     */
    protected fun launchOnViewModelScope(block: suspend () -> Unit) {
//        viewModelScope.launch(handler) {
        viewModelScope.launch {
            try {
                LogUtils.i("block执行之前")
                block()
                LogUtils.i("block执行之后")
            } catch (e: Exception) {
                // 3. 捕获异常并更新错误状态
                _error.value = e.message
                // 4. 调用 onError 方法，子类可重写此方法进行特定错误处理
                onError(e)
            }
        }
    }

    /**
     * 子类可重写此方法处理错误
     * 提供一个通用的错误处理入口
     */
    open fun onError(throwable: Throwable) {
        println("BaseViewModel Error: ${throwable.message}")
    }

    /**
     * 清除错误状态
     * 当错误处理完成后，可以调用此方法清除错误信息
     */
    fun clearError() {
        _error.value = null
    }
}