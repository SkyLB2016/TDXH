package com.sky.oa.data.model

import android.net.Uri

// Model: Photo.kt
data class Photo(
    val id: Long,
    val displayName: String,
    val contentUri: Uri,
    val dateAdded: Long,
    val size: Long
)