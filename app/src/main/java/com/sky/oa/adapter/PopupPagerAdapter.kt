package com.sky.oa.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide

import com.sky.oa.R
import com.sky.oa.data.model.Photo
import com.sky.oa.utils.imageloader.ImageLoaderExecutors
import com.sky.oa.widget.ZoomImageView

/**
 * Created by SKY on 2016/1/13 14:09.
 */
class PopupPagerAdapter : PagerAdapter() {
    var photos: List<Photo>? = null
        set(photos) {
            field = photos
            if (views != null)
                views = null
            views = arrayOfNulls<ImageView>(photos!!.size)
        }
    private var views: Array<ImageView?>? = null

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val imageView = ZoomImageView(container.context)
//        val imageView = ImageView(container.context)
        imageView.setBackgroundResource(R.color.white)
        // 使用 Glide 或 Coil 加载图片
        Glide.with(container.context)
            .load(photos!![position].contentUri)
//            .centerCrop()
            .into(imageView)
        container.addView(imageView)
        views!![position] = imageView
//        if (Runtime.getRuntime().totalMemory() > Runtime.getRuntime().maxMemory() * 5 / 6) {
//        }
        return imageView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        if (position > views!!.size) container.removeAllViews()
        else {
            container.removeView(views!![position])
            views!![position] = null
        }
    }

    override fun getCount(): Int {
        return if (this.photos == null) 0 else this.photos!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}