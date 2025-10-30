package com.sky.oa.adapter

import android.animation.AnimatorInflater
import android.graphics.Outline
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sky.base.utils.LogUtils
import com.sky.oa.R
import com.sky.oa.data.model.Photo
import com.sky.oa.databinding.AdapterUriBinding

// Adapter: PhotoAdapter.kt
class PhotoAdapter : ListAdapter<Photo, PhotoAdapter.PhotoViewHolder>(DiffCallback()) {

    private var onImageClick:(List<Photo>)->Unit={}
    fun setOnImageClickListener(listener:(List<Photo>)->Unit){
        onImageClick=listener
    }
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

    inner class PhotoViewHolder(private val binding: AdapterUriBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(photo: Photo) {
            // 1. 卡片样式设置（Ripple + 阴影动画）
            with(binding.cardView) {
                background = context.getDrawable(R.drawable.ripple)
                stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.drawable.state_list_animator)
                setOnClickListener {
                    onImageClick(currentList)
                }
            }
            // 2. 图片视图裁剪设置（圆角）
            binding.image.apply {
                //视图裁剪
                clipToOutline = true
                outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(view.left, view.top, view.right, view.bottom, 30f)
                    }
                }
            }.also {
                // 3. 加载图片
                // 使用 Glide 或 Coil 加载图片
                Glide.with(binding.image.context)
                    .load(photo.contentUri)
                    .centerCrop()
                    .into(binding.image)
            }


            // 4. 绑定文本（可选）
            // binding.tvText.text = photo.displayName
            LogUtils.i("photo.displayName", photo.displayName)
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean = oldItem == newItem
}