package com.sky.oa.pop

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.sky.oa.R
import com.sky.oa.adapter.PopupPagerAdapter
import com.sky.base.widget.BasePop
import com.sky.oa.data.model.Photo

/**
 * Created by SKY on 2016/1/11 13:14.
 */
class PhotoPopupWindow(contentView: View) : BasePop<Photo>(contentView) {

    private var viewPager: ViewPager? = null
    private var adapter: PopupPagerAdapter? = null

    override fun initEvent() {
        viewPager = view.findViewById(R.id.id_viewpager)
        adapter = PopupPagerAdapter(context)
        viewPager?.adapter = adapter
    }

    override fun initDatas() {
        adapter?.photos = popDatas
        adapter?.notifyDataSetChanged()
    }

    fun setCurrentItem(position: Int) {
        println("position == $position")
        viewPager?.setCurrentItem(position, false)
    }
}
