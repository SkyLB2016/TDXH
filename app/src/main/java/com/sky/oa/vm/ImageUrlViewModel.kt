package com.sky.oa.vm

import androidx.lifecycle.viewModelScope
import com.sky.base.ui.BaseViewModel
import com.sky.base.utils.LogUtils
import com.sky.oa.data.model.ImageResource
import com.sky.oa.data.repository.HttpRepository
import com.sky.oa.entity.CourseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageUrlViewModel() : BaseViewModel() {
    private val repository = HttpRepository()
    val _uiState = MutableStateFlow<UiState<MutableList<CourseEntity>>>(UiState.Loading)
    var uiState: StateFlow<UiState<MutableList<CourseEntity>>> = _uiState.asStateFlow()

    init {
        loadDatas()
        loadTeachers()
//        loadLocalImages()
//
    }

    private fun loadDatas() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getTeacher()
                .onSuccess { result ->
                    _uiState.value = UiState.Success(result) as UiState<MutableList<CourseEntity>>
                }
                .onFailure { e ->
                    _uiState.value = UiState.Error(e.message ?: "未知错误")
                }

        }
    }

    private val _teachers = MutableStateFlow<List<CourseEntity>>(emptyList())
    val teachers: StateFlow<List<CourseEntity>> = _teachers.asStateFlow()

    private fun loadTeachers() {
        viewModelScope.launch {
            runCatching {
                repository.getTeachers(4, 30)
            }.onSuccess {
//                LogUtils.i("输出",it.toString())
                _teachers.value = it.data!!
            }.onFailure {
                LogUtils.i("加载讲师失败", it.toString())
            }
        }
    }

    fun getImageUrl() {
        repository.getImageUrl()
    }
}