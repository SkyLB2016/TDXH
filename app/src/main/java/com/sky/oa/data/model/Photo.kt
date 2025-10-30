package com.sky.oa.data.model

import android.net.Uri

// Model: Photo.kt
data class Photo(
    val id: Long,               // id
    val displayName: String,    // 名称
    val contentUri: Uri,        //
    val filePath: String,       // 文件路径
    val folderName: String,     // 文件夹名称
    val dateAdded: Long,        // 加入日期
    val size: Long              // 大小
)