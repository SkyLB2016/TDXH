package com.sky.oa.adapter.itemtouch

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/4/13 3:11 下午
 * @Version: 1.0
 */
class ItemTouchHelperCallback(val callback: ItemTouchHelperListener) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val manager = recyclerView.layoutManager
        var dragFlags = 0
        var swipeFlags = 0
        if (manager is GridLayoutManager) {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            swipeFlags = 0
        } else {
            dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.itemViewType !== target.itemViewType) {
            return false
        }
        callback.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }

    override fun isLongPressDragEnabled(): Boolean {
        return super.isLongPressDragEnabled()
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }
}