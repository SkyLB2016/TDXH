package com.sky.base.ui

import androidx.viewbinding.ViewBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 3:32 下午
 * @Description: MVVM模式，recycleview 的 holder
 */
class MvvmHolder<V : ViewBinding>(val binding: V) : RecyclerView.ViewHolder(binding.root)