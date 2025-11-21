package com.sky.oa.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sky.base.ApiResult
import com.sky.base.ui.BaseViewModel
import com.sky.base.utils.LogUtils
import com.sky.oa.data.model.ImageResource
import com.sky.oa.data.repository.HttpRepository
import com.sky.oa.data.model.CourseEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageUrlViewModel() : BaseViewModel() {
    private val repository = HttpRepository()

    // 使用StateFlow替代LiveData（更现代的选择）
    val _uiState = MutableStateFlow<UiState<MutableList<CourseEntity>>>(UiState.Loading)
    var uiState: StateFlow<UiState<MutableList<CourseEntity>>> = _uiState.asStateFlow()

    // 使用LiveData的版本（传统方式）
    private val _courseLiveData = MutableLiveData<MutableList<CourseEntity>>()
    val courseLiveData: LiveData<MutableList<CourseEntity>> = _courseLiveData

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
//                    courseLiveData.value=result as MutableList<CourseEntity>
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
                repository.getCourses(4, 30)
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
            isLoading = true
            _uiState.value = UiState.Loading
            if (!isLoad) {
                currentPage = 0
                loadedTeachers.clear()
            }

            currentPage += 1
            println("isLoad=$isLoad,currentPage=$currentPage")
            if (currentPage > 3 && isLoad) {
                _uiState.value = UiState.Error("已无更多")
                isLoading = false
                return@launch
            }
            try {
                // 在IO线程执行网络请求
                val response = withContext(Dispatchers.IO){
                    repository.getTeachers(4, currentPage * pageSize)
                }

                when(response){
                    is ApiResult.Loading->{
                        _uiState.value=UiState.Loading
                    }
                    is ApiResult.Success->{
                        val result =response.datas
                        println("获取的数据长度==${result.data?.size}")
                        val newItems = result.data!!
                            .takeLast(pageSize)
                            .filter { teacher ->
                                // 避免重复添加（可选）
                                loadedTeachers.none { it.id == teacher.id }
                            }

                        println("截取出的数据长度=${newItems.size}")
                        if (newItems.isEmpty()) {
                            // 没有更多数据
                            _uiState.value = UiState.Error("已无更多")
                            isLoading = false
                            return@launch
                        }
                        loadedTeachers.addAll(newItems)
                        _uiState.value = UiState.Success(loadedTeachers)
                    }
                    else -> {
                        _uiState.value=UiState.Error("数据错误")
                    }
                }


                isLoading = false
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "加载失败")
                isLoading = false
            }
        }
    }

    fun onItemClicked(teacher: CourseEntity) {
        // 处理点击事件，比如跳转详情页
        Log.d("Teacher", "Clicked: ${teacher.name}")
        // 例如：navigateToDetail(teacher.id)
    }
}