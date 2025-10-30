package com.sky.oa.data.model

import android.net.Uri

// Model: Photo.kt
data class Folder(
    val id: Int,               // id
    val folderName: String,     // 文件夹名称
    val filePath: String,       // 文件夹路径
    val contentUri: Uri,        // 第一张图片的uri
    val photoList: MutableList<Photo> =mutableListOf<Photo>()   //文件列表
)