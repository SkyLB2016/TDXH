package com.sky.oa.data.repository

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.sky.oa.data.model.Photo

// Repository: PhotoRepository.kt
class PhotoRepository(private val context: Context) {

    // 查询所有图片
    fun getAllPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(nameColumn)
                val dateAdded = cursor.getLong(dateColumn)
                val size = cursor.getLong(sizeColumn)
                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                println("获取的图片的属性" + id + displayName + contentUri + dateAdded + size)
                photos.add(Photo(id, displayName, contentUri,"", dateAdded, size))
            }
        }
        return photos
    }
    fun getAllPhotos1(): List<Photo> {
        val photos = mutableListOf<Photo>()

        // 使用 VOLUME_EXTERNAL 获取所有外部存储中的媒体文件
        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, // 文件夹名，可用于分类
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.DATA, // 路径（仅用于调试，不用于加载）
            MediaStore.Images.Media.SIZE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val folderColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(nameColumn)
                val folderName = cursor.getString(folderColumn)
                val dateAdded = cursor.getLong(dateColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                val size = cursor.getLong(sizeColumn)

                // 可以过滤掉某些临时文件，但一般不用
                photos.add(Photo(id, displayName, contentUri,folderName, dateAdded, size))
            }
        }

        return photos
    }
}