package com.sky.oa.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sky.base.utils.BitmapUtils
import com.sky.oa.R
import com.sky.oa.databinding.PopUriItemBinding
import com.sky.oa.data.model.ImageFloder
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter
import com.sky.base.widget.BasePop

/**
 * Created by SKY on 2016/1/11 13:14.
 * 文件夹pop
 */
class FloderPop(view: View, width: Int, height: Int) : BasePop<ImageFloder>(view, width, height) {
    private var recycle: RecyclerView? = null

    private var adapter: RecyclerAdapter<PopUriItemBinding, ImageFloder>? = null

    override fun initView() {
        super.initView()
        recycle = view.findViewById<RecyclerView>(R.id.recycler)
        adapter = object : RecyclerAdapter<PopUriItemBinding, ImageFloder>() {
            override fun getBinding(context: Context?, parent: ViewGroup) =
                PopUriItemBinding.inflate(LayoutInflater.from(context), parent, false)

            override fun onAchieveHolder(holder: MvvmHolder<PopUriItemBinding>, binding: PopUriItemBinding, position: Int) {
                binding.image.setImageBitmap(BitmapUtils.getBitmapFromPath(datas[position].firstImagePath, 100, 100))
                binding.tvName.text = datas[position].name
                binding.tvCount.text = datas[position].count.toString() + "个"
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
