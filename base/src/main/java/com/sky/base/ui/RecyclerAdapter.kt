package com.sky.base.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 3:32 下午
 * @Description: MVVM模式，RecyclerView的万能适配器
 */
abstract class RecyclerAdapter<V : ViewBinding, T> : RecyclerView.Adapter<MvvmHolder<V>>() {
    lateinit var context: Context

    //    var datas: MutableList<ChapterEntity> = arrayListOf()
    var datas: MutableList<T> = arrayListOf()
        set(value) {
            field = value;
            notifyDataSetChanged()
        }

    //viewItem的点击事件监听
    //子view的按钮监听
    var onItemClickListener: ((View, Int) -> Unit)? = null

    //长按监听
    var itemLongClickListener: ((View, Int) -> Boolean)? = null

    fun addDatas(datas: List<T>?) {
        this.datas!!.addAll(datas!!)
        notifyDataSetChanged()
    }

    fun clearDatas() {
        if (datas == null) return
        datas.clear()
        notifyDataSetChanged()
    }

    /**
     * @param obj      添加item
     * @param position -1时最后一个添加
     */
    //添加item,默认从最后添加
    @JvmOverloads
    fun addItem(obj: T, position: Int = LASTITEM) {
        var position = position
        position = if (position == LASTITEM) itemCount else position
        datas!!.add(position, obj)
        notifyItemInserted(position)
    }

    /**
     * @param position,为-1时，删除最后一个
     */
    fun deleteItem(position: Int) {
        var position = position
        if (position == LASTITEM && itemCount > 0) position = itemCount - 1
        if (position in (LASTITEM + 1) until itemCount) {
            datas!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    //view 的种类，如果返回position的话，拖动item的会有冲突。
    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return datas!!.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MvvmHolder<V> {
        context = parent.context
        return MvvmHolder(getBinding(context, parent))
    }

    abstract fun getBinding(context: Context?, parent: ViewGroup): V

    override fun onBindViewHolder(holder: MvvmHolder<V>, position: Int) {
        onAchieveHolder(holder, holder.binding, position)
        holder.itemView.setOnLongClickListener {
            return@setOnLongClickListener itemLongClickListener?.invoke(it, holder.layoutPosition)
                ?: false
        }
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(holder.itemView, holder.layoutPosition)
        }
    }

    /**
     * 加载数据内容于view上
     *
     * @param holder   holder
     * @param position 位置
     */
    protected abstract fun onAchieveHolder(holder: MvvmHolder<V>, binding: V, position: Int)

    companion object {
        protected const val LASTITEM = -1
    }
}