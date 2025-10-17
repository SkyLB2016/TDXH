package com.sky.oa.adapter.itemtouch

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/4/13 3:15 下午
 * @Version: 1.0
 */
interface ItemTouchHelperListener {
    fun onItemMove(fromPosition:Int,toPosition:Int)
    fun onItemSwiped()

}