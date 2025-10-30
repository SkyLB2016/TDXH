package com.sky.oa.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter
import com.sky.base.widget.BasePop
import com.sky.oa.R
import com.sky.oa.data.model.Folder
import com.sky.oa.databinding.PopUriItemBinding

/**
 * Created by SKY on 2016/1/11 13:14.
 * 文件夹pop
 */
class FolderPopupwindow(view: View, width: Int, height: Int) : BasePop<Folder>(view, width, height) {
    private var recycle: RecyclerView? = null

    private var adapter: RecyclerAdapter<PopUriItemBinding, Folder>? = null

    override fun initView() {
        super.initView()
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipe.isEnabled=false
        recycle = view.findViewById<RecyclerView>(R.id.recycler)
        adapter = object : RecyclerAdapter<PopUriItemBinding, Folder>() {
            override fun getBinding(context: Context?, parent: ViewGroup) =
                PopUriItemBinding.inflate(LayoutInflater.from(context), parent, false)

            override fun onAchieveHolder(
                holder: MvvmHolder<PopUriItemBinding>,
                binding: PopUriItemBinding,
                position: Int
            ) {
                val folder = datas[position]
                Glide.with(binding.image.context)
                    .load(folder.contentUri)
                    .override(100, 100)
//                    .centerCrop()
                    .into(binding.image)

                binding.tvName.text = folder.folderName
                "${folder.photoList.size}个".also { binding.tvCount.text = it }
            }
        }
        recycle?.adapter = adapter
    }

    override fun initEvent() {
        adapter?.onItemClickListener = { view, position ->
            itemClickListener?.onItemClick(view, position)
            dismiss()
        }
    }

    override fun initDatas() {
        adapter?.datas = popDatas
    }
}
