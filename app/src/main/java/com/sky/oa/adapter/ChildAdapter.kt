package com.sky.oa.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sky.oa.R
import com.sky.oa.activity.ArticleActivity
import com.sky.oa.databinding.AdapterNoteitemBinding
import com.sky.oa.entity.PoetryEntity
import com.sky.base.ui.MvvmHolder

/**
 *
 * @Description: 笔记与文章页面嵌套的 childFragment 的adapter
 * @Author: 李彬
 * @CreateDate: 2022/3/18 5:10 下午
 * @Version: 1.0
 */
class ChildAdapter : RecyclerView.Adapter<MvvmHolder<AdapterNoteitemBinding>>() {
    private val fontIcon = intArrayOf(
        R.string.font,
        R.string.font01,
        R.string.font02,
        R.string.font03,
        R.string.font04,
        R.string.font05,
        R.string.font06,
        R.string.font07,
        R.string.font08
    )
    private lateinit var context: Context

    var poetries: MutableList<PoetryEntity> = mutableListOf()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MvvmHolder<AdapterNoteitemBinding> {
        context = parent.context
        return MvvmHolder(AdapterNoteitemBinding.inflate(LayoutInflater.from(context)))
    }

    override fun onBindViewHolder(holder: MvvmHolder<AdapterNoteitemBinding>, position: Int) {
        holder.binding.tvName.text = poetries[position]?.name
        //字体，icomoon对应fonticon
        val face = Typeface.createFromAsset(context.assets, "font/icomoon.ttf")

        holder.binding.tvImage.text = context.resources.getString(fontIcon[position % 9])
        holder.binding.tvImage.typeface = face
        holder.binding.tvImage.textSize = 50f
        holder.binding.cardView.setOnClickListener {
            ArticleActivity.newInstance(context, poetries[position])
        }
    }

    override fun getItemCount() = poetries?.size ?: 0
}
