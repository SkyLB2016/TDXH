package com.sky.oa.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sky.base.utils.LogUtils
import com.sky.oa.databinding.AdapterUrlBinding
import com.sky.oa.entity.CourseEntity

class CourseAdapter : ListAdapter<CourseEntity, CourseAdapter.ViewHolder>(DiffCallback()) {
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val binding = AdapterUrlBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: AdapterUrlBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(course: CourseEntity) {
            val face =
                Typeface.createFromAsset(binding.tvName.context.assets, "font/lobster_regular.ttf")
            binding.tvName.typeface = face
            binding.tvDescribe.typeface = face
            binding.tvName.text = "${position + 1}.${course.name}"
            binding.tvDescribe.text = course.description
            binding.image.tag = course.picBig

            binding.tvName.text = course.name
            LogUtils.i("course.picBig", course.picBig)
            // 使用 Glide 加载头像（可选）
            Glide.with(binding.image.context)
                .load("${course.picBig}".replace("http://", "https://"))
                .circleCrop()
                .into(binding.image)
//
            // 简单处理：直接设置背景色或占位图
//            binding.image.setImageResource(R.d)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CourseEntity>() {
        override fun areItemsTheSame(old: CourseEntity, new: CourseEntity) = old.id == new.id
        override fun areContentsTheSame(old: CourseEntity, new: CourseEntity) = old == new
    }
}