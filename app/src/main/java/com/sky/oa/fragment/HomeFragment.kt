package com.sky.oa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.sky.base.utils.JumpAct
import com.sky.base.utils.LogUtils
import com.sky.oa.adapter.HomeAdapter
import com.sky.oa.adapter.itemtouch.ItemTouchHelperCallback
import com.sky.oa.databinding.FragmentHomeBinding
import com.sky.oa.vm.HomeViewModel
import com.sky.base.ui.BaseMFragment

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/3/11 10:25 下午
 * @Version: 1.0
 */
class HomeFragment : BaseMFragment<FragmentHomeBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by viewModels()

    //    lateinit var adapter: HomeAdapter
    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentHomeBinding.inflate(inflater, container, false)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtils.i("Homefragment的oncreateview")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
//        binding.recycler.isNestedScrollingEnabled = false
        val adapter = HomeAdapter()
        binding.recycler.adapter = adapter

        //设置拖动item
        val helper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        helper.attachToRecyclerView(binding.recycler)

        adapter.onItemClickListener = { v, p ->
            JumpAct.jumpActivity(context, adapter.datas?.get(p)!!.componentName)
        }
        viewModel.getActivities(requireContext())
        viewModel.activitiesData.observe(viewLifecycleOwner, {
            adapter.datas = it
        })
//        var footBinding = ItemFootBinding.inflate(layoutInflater);
//        binding.root.addView(
//            footBinding.root,
//            ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        )
//        binding.root.addView(footBinding.root)
    }
}