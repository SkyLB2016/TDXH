package com.sky.oa.adapter

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Outline
import android.os.Build
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.recyclerview.widget.RecyclerView
import com.sky.oa.R
import com.sky.oa.databinding.AdapterUriBinding
import com.sky.oa.utils.imageloader.ImageLoaderExecutors
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter

/**
 * Created by SKY on 2015/12/9.
 */
class LoaderUriAdapter : RecyclerAdapter<AdapterUriBinding, String>() {
    private val imageLoader: ImageLoaderExecutors = ImageLoaderExecutors()
    var parentPath: String? = null

    override fun getBinding(context: Context?, parent: ViewGroup) = AdapterUriBinding.inflate(LayoutInflater.from(context), parent, false)
    override fun onAchieveHolder(holder: MvvmHolder<AdapterUriBinding>, binding: AdapterUriBinding, position: Int) {
        binding.image.tag = parentPath + "/" + datas[position]
        binding.image.setBackgroundResource(R.mipmap.ic_launcher)
        binding.image.maxWidth = 300
        binding.image.minimumWidth = 300
//            imageLoader.loadImage(binding.image, "$parentPath/${datas[position]}")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            //使用ripple
            binding.cardView.background = context.getDrawable(R.drawable.ripple)
            //点击效果，阴影效果
            binding.cardView.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.drawable.state_list_animator)
            //视图裁剪
            binding.image.clipToOutline = true
            binding.image.outlineProvider = object : ViewOutlineProvider() {
                @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(view.left, view.top, view.right, view.bottom, 30f)
                }
            }
        }
    }

    /**
     * 空闲时在加载image
     *
     * @param start
     * @param last
     * @param viewGroup
     */
    fun setImageLoader(start: Int, last: Int, viewGroup: RecyclerView) {
        for (i in start..last) {
            imageLoader.loadImage(viewGroup.findViewWithTag("$parentPath/${datas[i]}"), "$parentPath/${datas[i]}");
        }
    }

    fun interruptExecutors() {
        imageLoader.closeExecutors()
    }
}