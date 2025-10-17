package com.sky.oa.vm

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.sky.base.ui.BaseViewModel
import com.sky.oa.entity.CourseEntity
import com.sky.oa.entity.ImageFloder
import com.sky.oa.repository.ImageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.io.File

class ImageUriViewModel(val repository: ImageRepository) : BaseViewModel() {


    fun getLiveDataFloders(): MutableLiveData<MutableList<ImageFloder>> {
        return repository.liveDataFloders
    }

    fun getLiveDataParent(): MutableLiveData<File> {
        return repository.liveDataParent
    }

    fun checkDiskImage(context: Context) {
        repository.checkDiskImage(context)
    }

    fun getImageUrl() {
        repository.getImageUrl()
    }

}
