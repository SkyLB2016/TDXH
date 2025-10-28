package com.sky.oa.vm

import androidx.lifecycle.viewModelScope
import com.sky.base.ui.BaseViewModel
import com.sky.oa.data.model.ChapterEntity
import com.sky.oa.data.model.PoetryEntity
import com.sky.oa.repository.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * Created by libin on 2020/05/13 2:43 PM Wednesday.
 */
class SlidingViewModel(private val repository: NotesRepository) : BaseViewModel() {

    // 1. 创建私有的可变 StateFlow 来管理用户列表状态
    private val _uiState = MutableStateFlow<UiState<List<ChapterEntity>>>(UiState.Loading)

    //2. 提供公开的制度的stateflow 给 UI层
    val uiState: StateFlow<UiState<List<ChapterEntity>>> = _uiState.asStateFlow()

    private val _flowState = MutableStateFlow<UiState<List<PoetryEntity>>>(UiState.Loading)
    var flowState = _flowState.asStateFlow()
    fun getaArticles() {
        viewModelScope.launch {
            _flowState.value= UiState.Loading
            try {
                val articles = repository.getArticles()
                _flowState.value= UiState.Success(articles)
            }catch (e: Exception){
                _flowState.value= UiState.Error(e.message?:"位未知错误")
            }
        }
    }

    fun loadChapter(fileName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            try {
                val texts = repository.getChapter(fileName)
                _uiState.value = UiState.Success(texts)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "未知错误")
            }
        }

    }

}