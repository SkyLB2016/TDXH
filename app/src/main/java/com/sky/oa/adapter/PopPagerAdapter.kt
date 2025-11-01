package com.sky.oa.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter

import com.sky.oa.R
import com.sky.oa.utils.imageloader.ImageLoaderExecutors
import com.sky.oa.widget.ZoomImageView

/**
 * Created by SKY on 2016/1/13 14:09.
 */
class PopPagerAdapter : PagerAdapter() {
    var strings: List<String>? = null
        set(strings) {
            field = strings
            if (views != null)
                views = null
            views = arrayOfNulls<ImageView>(strings!!.size)
        }
    var parentPath: String? = null
    private var views: Array<ImageView?>? = null
    private val loader: ImageLoaderExecutors = ImageLoaderExecutors()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val imageView = ZoomImageView(container.context)
        imageView.setBackgroundResource(R.color.white)
//        imageView.setColorFilter(Color.parseColor("#77000000"))
        loader.loadImage(imageView, parentPath + "/" + this.strings!![position])

        container.addView(imageView)
        views!![position] = imageView
        if (Runtime.getRuntime().totalMemory() > Runtime.getRuntime().maxMemory() * 5 / 6) {
        }
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
        return if (this.strings == null) 0 else this.strings!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }
}