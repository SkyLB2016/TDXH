package com.sky.oa.pop

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.sky.oa.R
import com.sky.oa.adapter.PopPagerAdapter
import com.sky.base.widget.BasePop

/**
 * Created by SKY on 2016/1/11 13:14.
 */
class URIPop(contentView: View) : BasePop<String>(contentView) {

    var parentPath: String? = null
    private var viewPager: ViewPager? = null
    private var adapter: PopPagerAdapter? = null

    override fun initEvent() {
        viewPager = view.findViewById(R.id.id_viewpager)
        adapter = PopPagerAdapter()
        viewPager?.adapter = adapter
    }

    override fun initDatas() {
        adapter?.parentPath = parentPath
        adapter?.strings = popDatas
        adapter?.notifyDataSetChanged()
    }

    fun setCurrentItem(position: Int) {
        viewPager?.setCurrentItem(position, false)
    }
}
