package com.sky.oa.vm

import android.content.res.AssetManager
import androidx.lifecycle.viewModelScope
import com.sky.base.ui.BaseViewModel
import com.sky.oa.entity.PoetryEntity
import com.sky.oa.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotesViewModel(private val repository: NotesRepository) : BaseViewModel() {

    // 1. 创建私有的可变 StateFlow 来管理用户列表状态
    private val _uiState =
        MutableStateFlow<UiState<LinkedHashMap<String, ArrayList<PoetryEntity>>>>(UiState.Loading)

    //2. 提供公开的制度的stateflow 给 UI层
    val uiState: StateFlow<UiState<LinkedHashMap<String, ArrayList<PoetryEntity>>>> =
        _uiState.asStateFlow()

    //    fun loadNotes(assets: AssetManager, directory: String) {
//        viewModelScope.launch {
//            _uiState.value = UiState.Loading
//            try {
//                val notes = repository.getPoetries(assets, directory)
//                _uiState.value = UiState.Success(notes)
//            } catch (e: Exception) {
//                _uiState.value = UiState.Error(e.message ?: "未知错误")
//            }
//        }
//    }
    fun loadNotes(assets: AssetManager, directory: String) = launchOnViewModelScope {
        println("block中的方法开始执行")
        _uiState.value = UiState.Loading
        try {
            val notes = repository.getPoetries(assets, directory)
            _uiState.value = UiState.Success(notes)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "未知错误")
        }
        println("block中的方法执行完毕")
    }
}