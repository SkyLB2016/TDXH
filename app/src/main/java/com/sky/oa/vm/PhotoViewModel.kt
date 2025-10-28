package com.sky.oa.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sky.base.ui.BaseViewModel
import com.sky.oa.data.model.Photo
import com.sky.oa.data.paging.PhotoPagingSource
import com.sky.oa.data.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow

// ViewModel: PhotoViewModel.kt
class PhotoViewModel(private val application: Application) : BaseViewModel() {
    val photoList: Flow<PagingData<Photo>> = Pager(
        config = PagingConfig(
            pageSize = 50,
            enablePlaceholders = true,
            initialLoadSize = 50
        ),
        pagingSourceFactory = {
            PhotoPagingSource(application)
        }
    ).flow
        .cachedIn(viewModelScope)

    private val repository = PhotoRepository(application)

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