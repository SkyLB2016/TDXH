package com.sky.oa.vm

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.sky.base.ui.BaseViewModel
import com.sky.base.utils.LogUtils
import com.sky.oa.data.model.ImageResource
import com.sky.oa.data.repository.HttpRepository
import com.sky.oa.data.model.CourseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ImageUrlViewModel() : BaseViewModel() {
    private val repository = HttpRepository()

    val _uiState = MutableStateFlow<UiState<MutableList<CourseEntity>>>(UiState.Loading)
    var uiState: StateFlow<UiState<MutableList<CourseEntity>>> = _uiState.asStateFlow()

    private var currentPage = 0
    private val pageSize = 10
    private val loadedTeachers = mutableListOf<CourseEntity>()
    private var isLoading = false // ✅ 新增标志
    init {
        loadMore()
//        loadDatas()
//        loadTeachers()
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

    fun loadMore(isLoad: Boolean = false) {
        viewModelScope.launch {
            if (isLoading) return@launch
            isLoading=true
            _uiState.value = UiState.Loading
            if (!isLoad) {
                currentPage = 0
                loadedTeachers.clear()
            }

            currentPage += 1
            println("isLoad=$isLoad,currentPage=$currentPage")
            if (currentPage > 3 && isLoad) {
                _uiState.value = UiState.Error("已无更多")
                isLoading=false
                return@launch
            }
            try {
                val response = repository.getTeachers(4, currentPage * pageSize)
                println("获取的数据长度==${response.data?.size}")
                val newItems = response.data!!
                    .takeLast(pageSize)
                    .filter { teacher ->
                        // 避免重复添加（可选）
                        loadedTeachers.none { it.id == teacher.id }
                    }

                println("截取出的数据长度=${newItems.size}")
                if (newItems.isEmpty()) {
                    // 没有更多数据
                    _uiState.value = UiState.Error("已无更多")
                    isLoading=false
                    return@launch
                }
                loadedTeachers.addAll(newItems)
                _uiState.value = UiState.Success(loadedTeachers)
                isLoading=false
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "加载失败")
                isLoading=false
            }
        }
    }

    fun onItemClicked(teacher: CourseEntity) {
        // 处理点击事件，比如跳转详情页
        Log.d("Teacher", "Clicked: ${teacher.name}")
        // 例如：navigateToDetail(teacher.id)
    }
}