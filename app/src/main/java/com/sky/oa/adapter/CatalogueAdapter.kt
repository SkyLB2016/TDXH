package com.sky.oa.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sky.oa.R
import com.sky.oa.data.model.PoetryEntity
import com.sky.oa.databinding.ItemTvBinding

// Adapter: CatalogueAdapter.kt
class CatalogueAdapter(private val onTeacherClick: (PoetryEntity) -> Unit) :
    ListAdapter<PoetryEntity, CatalogueAdapter.CatalogViewHolder>(DiffCallback()) {

    var itemClick: (PoetryEntity) -> Unit = {}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val binding = ItemTvBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CatalogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CatalogViewHolder(private val binding: ItemTvBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(poetry: PoetryEntity) {
            binding.tv.apply {
                minWidth = binding.root.context.resources.getDimensionPixelSize(R.dimen.wh_48)
                textSize = 18f
                maxLines = 1
//            text = text.substringAfterLast("/", ".").substringBefore(".", "")
                text = poetry.name
                setPadding(20, 6, 20, 6)
//            id = index
//            tag = poetry.filePath
                setOnClickListener {
                    onTeacherClick(poetry)
                }

            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PoetryEntity>() {
        override fun areItemsTheSame(oldItem: PoetryEntity, newItem: PoetryEntity): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: PoetryEntity, newItem: PoetryEntity): Boolean =
            oldItem == newItem
    }
}
