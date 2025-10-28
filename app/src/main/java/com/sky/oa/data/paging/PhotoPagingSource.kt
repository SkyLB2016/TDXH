package com.sky.oa.data.paging

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.sky.oa.data.model.Photo

// PhotoPagingSource.kt
class PhotoPagingSource(private val context: Context) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            println("anchorPosition==$anchorPosition")
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            println("fffffffffffffffffffffffffff")
            val page = params.key ?: 1
            val pageSize = params.loadSize
            val offset = (page - 1) * pageSize

            val photos = queryPhotos(pageSize, offset)
            println("$pageSize, $offset")

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (photos.size < pageSize) null else page + 1
//                        nextKey = if (photos.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            println(e.toString())
            LoadResult.Error(e)
        }
    }

    private fun queryPhotos(limit: Int, offset: Int): List<Photo> {
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
        // 按时间倒序，并分页
        var sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ 支持 LIMIT 和 OFFSET
            sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC LIMIT $limit OFFSET $offset"
        } else {
            // Android 10 及以下：使用传统的 selection + args 方式，然后手动截取
            sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        }
        println("sortOrder==$sortOrder")


        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val folderCol =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val displayName = cursor.getString(nameCol)
                val folderName = cursor.getString(folderCol)
                val dateAdded = cursor.getLong(dateCol)
                val contentUri =
                    ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                println("=="+id+ displayName+ contentUri+ folderName+ dateAdded)
                photos.add(Photo(id, displayName, contentUri, folderName, dateAdded, 0))
            }
        }

        return photos
    }
}