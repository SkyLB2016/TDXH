package com.sky.oa.adapter


import android.animation.AnimatorInflater
import android.graphics.Outline
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sky.base.utils.LogUtils
import com.sky.oa.R
import com.sky.oa.data.model.Photo
import com.sky.oa.databinding.AdapterUriBinding

class PhotoUriAdapter : PagingDataAdapter<Photo, PhotoUriAdapter.PhotoViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = AdapterUriBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photo = getItem(position)
        if (photo != null) {
            holder.bind(photo)
        }
    }

    class PhotoViewHolder(private val binding: AdapterUriBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                //使用ripple
                binding.cardView.background =
                    binding.cardView.context.getDrawable(R.drawable.ripple)
                //点击效果，阴影效果
                binding.cardView.stateListAnimator = AnimatorInflater.loadStateListAnimator(
                    binding.cardView.context,
                    R.drawable.state_list_animator
                )
                //视图裁剪
                binding.image.clipToOutline = true
                binding.image.outlineProvider = object : ViewOutlineProvider() {
                    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(view.left, view.top, view.right, view.bottom, 30f)
                    }
                }
            }
            // 使用 Glide 或 Coil 加载图片
            Glide.with(binding.image.context)
                .load(photo.contentUri)
//                .placeholder(R.drawable.ic_image_placeholder)
                .centerCrop()
                .into(binding.image)
            binding.tvText.text = photo.displayName
            LogUtils.i("photo.displayName", photo.displayName)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
            return oldItem == newItem
        }
    }
}