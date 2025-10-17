package com.sky.oa.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import com.sky.oa.R
import com.sky.oa.databinding.ItemTvBinding
import com.sky.oa.entity.ChapterEntity
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter

/**
 *
 * @Description: 主页 目录页面 的adapter
 * @Author: 李彬
 * @CreateDate: 2022/3/18 5:10 下午
 * @Version: 1.0
 */
class CatalogAdapter : RecyclerAdapter<ItemTvBinding, ChapterEntity>() {
//    var datas: MutableList<ChapterEntity> = ArrayList()
//    var itemClick: ((View, Int) -> Unit)? = null

    override fun getItemCount() = datas.size

    override fun getBinding(context: Context?, parent: ViewGroup) =
        ItemTvBinding.inflate(LayoutInflater.from(parent.context), parent, false)


    override fun onAchieveHolder(holder: MvvmHolder<ItemTvBinding>, binding: ItemTvBinding, position: Int) {
        binding.tv.text = datas[position].chapter
        binding.tv.setBackgroundResource(R.drawable.shape_ffc107)
        binding.tv.textSize = 18f
        binding.tv.gravity = Gravity.LEFT
    }
}