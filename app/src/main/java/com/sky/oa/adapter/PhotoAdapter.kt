package com.sky.oa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sky.base.utils.LogUtils
import com.sky.oa.data.model.Photo
import com.sky.oa.databinding.AdapterUriBinding

// Adapter: PhotoAdapter.kt
class PhotoAdapter : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(PhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = AdapterUriBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PhotoViewHolder(private val binding: AdapterUriBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            // 使用 Glide 或 Coil 加载图片
            Glide.with(binding.image.context)
                .load(photo.contentUri)
                .centerCrop()
                .into(binding.image)
//            binding.textView.text = photo.displayName
            LogUtils.i("photo.displayName",photo.displayName)
        }
    }
}

class PhotoDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem
}