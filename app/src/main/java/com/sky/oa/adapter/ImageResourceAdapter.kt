package com.sky.oa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sky.oa.data.model.ImageResource
import com.sky.oa.databinding.AdapterMipmapBinding

class ImageResourceAdapter : ListAdapter<ImageResource, ImageResourceAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AdapterMipmapBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: AdapterMipmapBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImageResource) {
            binding.image.setImageResource(item.resId)
            binding.tvName.text = item.name
            binding.tvType.text = item.type.uppercase()
            binding.tvSuffix.text = item.mimeType
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ImageResource>() {
        override fun areItemsTheSame(old: ImageResource, new: ImageResource) = old.name == new.name
        override fun areContentsTheSame(old: ImageResource, new: ImageResource) = old == new
    }
}