package com.sky.oa.vm

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.sky.base.ui.BaseViewModel
import com.sky.base.utils.LogUtils
import com.sky.oa.R
import com.sky.oa.data.model.ImageResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MipViewModel(application: Application) : BaseViewModel() {

    private val context = application

    private val _uiState = MutableStateFlow<UiState<List<ImageResource>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ImageResource>>> = _uiState.asStateFlow()

    init {
        loadAllImageResources()
    }

    private fun loadAllImageResources() =launchOnViewModelScope{

        try {
            val resources = context.resources
            val packageName = context.packageName

            val imageList = mutableListOf<ImageResource>()

            // 1. 扫描 drawable
            val drawableFields = R.drawable::class.java.fields
            for (field in drawableFields) {
                val name = field.name
                val id = resources.getIdentifier(name, "drawable", packageName)
                if (id != 0) {
                    if (name.startsWith("state_list_animator")) {
                        continue
                    }
//                    val mimeType = getBitmapMimeType(context, id)
//                    LogUtils.i("mimetype","$name==$mimeType")
                    imageList.add(ImageResource(name, id, "drawable", "xml"))
                }
            }

            // 2. 扫描 mipmap
            val mipmapFields = R.mipmap::class.java.fields
            for (field in mipmapFields) {
                val name = field.name

                val id = resources.getIdentifier(name, "mipmap", packageName)
                if (id != 0) {
//                    if (id != 0 && isImageResource(name)) {
                    val mimeType = getBitmapMimeType(context, id)
                    imageList.add(ImageResource(name, id, "mipmap", mimeType))
                }

            }

            // 排序（可选）
            imageList.sortBy { it.name }

            _uiState.value = UiState.Success(imageList)
        } catch (e: Exception) {
            _uiState.value = UiState.Error(e.message ?: "加载资源失败")
        }
    }

    // 判断是否是图片资源（根据常见扩展名）
    private fun isImageResource(name: String): Boolean {
        val extensions = listOf("png", "jpg", "jpeg", "gif", "webp", "bmp")
        return extensions.any { name.endsWith(".$it", ignoreCase = true) } ||
                !name.contains(".") // 无扩展名的也视为资源（如编译后去扩展）
    }


    fun getAllMipmapImages(context: Context): List<ImageResource> {
        val resources = context.resources
        val packageName = context.packageName
        val list = mutableListOf<ImageResource>()

        val fields = R.mipmap::class.java.fields
        for (field in fields) {
            val name = field.name
            val id = resources.getIdentifier(name, "mipmap", packageName)
            if (id != 0) {
                val mimeType = getBitmapMimeType(context, id)
                list.add(ImageResource(name, id, "mipmap", mimeType))
            }
        }
        return list.sortedBy { it.name }
    }

    // 获取 MIME 类型
    private fun getBitmapMimeType(context: Context, resId: Int): String? {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true // 只解码边界，不加载像素，节省内存
            BitmapFactory.decodeResource(context.resources, resId, options)
            options.outMimeType // 返回 "image/png", "image/jpeg" 等
        } catch (e: Exception) {
            null
        }
    }}