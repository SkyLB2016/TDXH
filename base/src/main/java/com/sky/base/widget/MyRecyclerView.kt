package com.sky.base.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2021/8/11 6:11 下午
 * @Version: 1.0
 */
class MyRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {
    init {
        layoutManager = LinearLayoutManager(context)
//        layoutManager = LinearLayoutManager(context, VERTICAL, false)
        //分割线效果
//        addItemDecoration(DividerGridItemDecoration(context))
//        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        // 添加删除时的动画效果
        itemAnimator = DefaultItemAnimator()

    }
}