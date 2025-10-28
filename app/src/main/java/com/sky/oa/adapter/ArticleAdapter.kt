package com.sky.oa.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sky.oa.R
import com.sky.oa.databinding.AdapterNoteBinding
import com.sky.oa.data.model.ChapterEntity
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter

/**
 *
 * @Description: 主页 文章页面 的adapter
 * @Author: 李彬
 * @CreateDate: 2022/3/18 5:10 下午
 * @Version: 1.0
 */
class ArticleAdapter : RecyclerAdapter<AdapterNoteBinding, ChapterEntity>() {
//    override fun getItemCount() = datas?.size

    override fun getBinding(context: Context?, parent: ViewGroup) = AdapterNoteBinding.inflate(LayoutInflater.from(parent.context))

    override fun onAchieveHolder(holder: MvvmHolder<AdapterNoteBinding>, binding: AdapterNoteBinding, position: Int) {
        holder.binding.tvDisplay.text = datas[position].content
        if (position == 0)
            holder.binding.tvDisplay.setPadding(
                context.resources.getDimensionPixelSize(R.dimen.wh_16),
                context.resources.getDimensionPixelSize(R.dimen.wh_16),
                context.resources.getDimensionPixelSize(R.dimen.wh_16),
                0
            )
    }
}