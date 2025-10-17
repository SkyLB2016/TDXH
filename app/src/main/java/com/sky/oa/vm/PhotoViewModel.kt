package com.sky.oa.vm

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.sky.base.ui.BaseViewModel
import com.sky.oa.data.model.Photo
import com.sky.oa.data.repository.PhotoRepository
import kotlinx.coroutines.launch

// ViewModel: PhotoViewModel.kt
class PhotoViewModel(private val context: Context) : BaseViewModel() {

    private val repository = PhotoRepository(context)

    // 使用 LiveData 暴露照片列表
    private val _photos = MutableLiveData<List<Photo>>()
    val photos: LiveData<List<Photo>> = _photos

    // 是否正在加载
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // 错误信息
    private val _photoError = MutableLiveData<String>()
    val prhotoError: LiveData<String> = _photoError

    // 从 Repository 加载照片
    fun loadPhotos() = launchOnViewModelScope {
        _isLoading.value = true
        try {
            val photoList = repository.getAllPhotos()
            _photos.value = photoList
        } catch (e: Exception) {
            _photoError.value = "加载照片失败: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
}