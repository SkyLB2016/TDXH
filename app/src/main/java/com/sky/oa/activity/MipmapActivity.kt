package com.sky.oa.activity

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.sky.base.ui.BaseMActivity
import com.sky.oa.App
import com.sky.oa.adapter.ImageResourceAdapter
import com.sky.oa.databinding.ActivityMipmapBinding
import com.sky.oa.vm.MipViewModel
import com.sky.oa.vm.UiState
import kotlinx.coroutines.launch

/**
 * @Author: 李彬
 * @TIME: 2025/10/12 17:22
 * @Description:
 */
class MipmapActivity : BaseMActivity<ActivityMipmapBinding, MipViewModel>() {
    private lateinit var adapter: ImageResourceAdapter

    override val viewModel: MipViewModel by viewModels {
        // 需传 application 参数
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MipViewModel(application) as T
            }
        }
    }


    override fun inflateBinding(): ActivityMipmapBinding {
        return ActivityMipmapBinding.inflate(layoutInflater)
    }

    override fun initViews() {
        adapter = ImageResourceAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

    }

    override fun setObservers() {
        collectUiState()
    }

    private fun collectUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                            binding.textViewError.visibility = View.GONE
                        }
                        is UiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.textViewError.visibility = View.GONE
                            adapter.submitList(state.datas)
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.recyclerView.visibility = View.GONE
                            binding.textViewError.visibility = View.VISIBLE
                            binding.textViewError.text = state.message
                        }
                    }
                }
            }
        }
    }

}