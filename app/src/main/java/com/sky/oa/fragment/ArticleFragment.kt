package com.sky.oa.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.sky.oa.adapter.NotesFragmentAdapter
import com.sky.oa.databinding.FragmentArticleBinding
import com.sky.oa.repository.NotesRepository
import com.sky.oa.vm.NotesViewModel
import com.sky.base.ui.BaseMFragment
import com.sky.base.utils.LogUtils
import com.sky.oa.vm.UiState
import kotlinx.coroutines.launch

/**
 *
 * @Author: 李彬
 * @CreateDate: 2022/3/11 10:25 下午
 * @Description: 文章模块
 */
class ArticleFragment : BaseMFragment<FragmentArticleBinding, NotesViewModel>() {
    lateinit var adapter: NotesFragmentAdapter

    override fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentArticleBinding.inflate(inflater, container, false)

    override val viewModel: NotesViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotesViewModel(NotesRepository()) as T
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tabs.tabMode = TabLayout.MODE_SCROLLABLE
//        binding.tabs.setupWithViewPager(binding.viewPager)

        adapter = NotesFragmentAdapter(childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
//                TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.tabs.setScrollPosition(position, 0f, true)
            }
        })
    }

    override fun loadDatas() {
        showLoading()
        viewModel.loadNotes(requireContext().assets, "Documents/文学")
    }

    override fun setObservers() {
        collectUiState()
    }
    fun collectUiState() {
//        等待数据加载完成
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            LogUtils.i("数据加载中")
                        }

                        is UiState.Success -> {
                            LogUtils.i("数据加载成功")
                            adapter.maps = state.datas
                            val keys = state.datas.keys
                            for (key in keys) {
                                binding.tabs.addTab(
                                    binding.tabs.newTab().setText(key.split("/").last())
                                )
                            }
                            disLoading()
                        }

                        is UiState.Error -> {
                            showToast("数据加载失败")
                        }
                    }
                }
            }
        }
    }
}