package com.sky.oa.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

import com.sky.oa.R
import com.sky.oa.data.model.Photo
import com.sky.oa.widget.ZoomImageView

/**
 * Created by SKY on 2016/1/13 14:09.
 */
class PopupPagerAdapter(val context: Context) : PagerAdapter() {
    private val imageViewList = mutableListOf<ImageView>() // 自动管理视图

    var photos: List<Photo>? = null
        set(value) {
            field = value
            // 清除旧视图
            imageViewList.clear()
            // 创建新视图
            if (value != null) {
                repeat(value.size) {
                    val imageView = ZoomImageView(context)
//                    val imageView = ImageView(context)
//                    imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
//                    imageView.scaleType= ImageView.ScaleType.MATRIX
//                    imageView.adjustViewBounds = true
                    imageView.setBackgroundResource(R.color.white)
                    imageViewList.add(imageView)
                }
            }
            notifyDataSetChanged()
        }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = imageViewList[position]
        // 使用 Glide 或 Coil 加载图片
        Glide.with(container.context)
            .load(photos?.getOrNull(position)?.contentUri)
//            .centerCrop()
//            .placeholder(R.drawable.ic_image_placeholder) // 占位图
//            .error(R.drawable.ic_image_error)           // 错误图
            .timeout(10_000)                            // 超时 10s
            .into(imageView)
        container.addView(imageView)
//        println("totalMemory==${Runtime.getRuntime().totalMemory()}")
//        println("maxMemory==${Runtime.getRuntime().maxMemory()}")
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // 安全移除
        if (position < imageViewList.size && imageViewList[position] == `object`) {
            container.removeView(`object` as View)
            // 不需要手动置 null，mutableList 会自动管理
        }
    }

    override fun getCount(): Int = photos?.size ?: 0

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE // 数据变化时全部重建
    }

    // 可选：获取当前 ImageView（用于分享、保存等）
    fun getCurrentImageView(position: Int): ImageView? {
        return imageViewList.getOrNull(position)
    }

    // 可选：释放资源（在 ViewPager 销毁时调用）
    fun onDestroy() {
        imageViewList.forEach { imageView ->
            Glide.with(imageView.context).clear(imageView)
        }
        imageViewList.clear()
    }
}