package com.sky.oa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.reflect.TypeToken
import com.sky.base.ui.BaseViewModel
import com.sky.oa.adapter.ChildAdapter
import com.sky.oa.databinding.FragmentChildBinding
import com.sky.oa.data.model.PoetryEntity
import com.sky.oa.gson.GsonUtils
import com.sky.base.ui.BaseMFragment

class NotesChildFragment : BaseMFragment<FragmentChildBinding, BaseViewModel>() {

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentChildBinding.inflate(inflater!!, container, false)

    override val viewModel: BaseViewModel by viewModels()
    private lateinit var poetries: MutableList<PoetryEntity>



    companion object {
        const val KEY = "poetry"
        fun newInstance(json: String): Fragment {
            val fragment = NotesChildFragment()
            val bundle = Bundle()
            bundle.putString(KEY, json)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initParameters() {
        if (arguments != null) {
            poetries = GsonUtils.fromJson(
                requireArguments().getString(KEY),
                object : TypeToken<List<PoetryEntity>>() {}.type
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = GridLayoutManager(context, 3)
        val adapter = ChildAdapter()
        binding.recycler.adapter = adapter
        adapter.poetries = poetries
//        binding.smartRefresh.setRefreshHeader(WaterDropHeader(context))
//        binding.smartRefresh.setRefreshFooter(
//            BallPulseFooter(context!!).setSpinnerStyle(
//                SpinnerStyle.Scale
//            )
//        )
//        binding.smartRefresh.setOnRefreshListener { refreshLayout -> binding.smartRefresh.finishRefresh() }
//        binding.smartRefresh.setOnLoadMoreListener { refreshLayout -> binding.smartRefresh.finishLoadMore() }

        //        setLoadSir(binding.smartRefresh);
        //        showContent();
    }

}
