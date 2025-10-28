package com.sky.oa.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sky.oa.data.model.PoetryEntity
import com.sky.oa.fragment.NotesChildFragment
import com.sky.oa.gson.GsonUtils

class NotesFragmentAdapter(manager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(manager, lifecycle) {
    var maps: LinkedHashMap<String, ArrayList<PoetryEntity>>? = null
        set(value) {
            field = value
            val keys: Set<String> = maps!!.keys
            titles.clear()
            titles.addAll(keys)
//            LogUtils.i("titles==${titles}")
//        Collections.sort(titles);
            notifyDataSetChanged()
        }
    var titles: MutableList<String> = mutableListOf()

    override fun getItemCount(): Int = maps?.size ?: 0

    override fun createFragment(position: Int): Fragment =
        NotesChildFragment.newInstance(GsonUtils.toJson(maps?.get(titles[position])))
//    override fun getPageTitle(position: Int): CharSequence? {
//        return titles[position].split("/").last()
//    }

}
