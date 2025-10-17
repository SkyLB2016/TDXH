package com.sky.oa.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sky.base.ui.BaseMActivity
import com.sky.base.ui.BaseViewModel
import com.sky.oa.App
import com.sky.oa.R
import com.sky.oa.adapter.PhotoAdapter
import com.sky.oa.databinding.ActivityMainBinding
import com.sky.oa.databinding.ActivityPhotoBinding
import com.sky.oa.vm.PhotoViewModel

/**
 * @Author: 李彬
 * @TIME: 2025/10/13 16:59
 * @Description:
 */
class PhotoActivity : BaseMActivity<ActivityPhotoBinding, PhotoViewModel>() {


    private lateinit var photoAdapter: PhotoAdapter

    // 使用 Activity Result API 请求权限
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadPhotos()
        } else {
            showErrorMessage("需要权限才能访问照片")
        }
    }

    override val viewModel: PhotoViewModel by viewModels {
        object : ViewModelProvider.Factory{
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhotoViewModel(App.context) as T
            }
        }
    }

    override fun inflateBinding(): ActivityPhotoBinding {
     return ActivityPhotoBinding.inflate(layoutInflater)
    }


    override fun initViews() {
        super.initViews()
        setupRecyclerView()
        setupViewModel()
        // 检查并请求权限
        requestStoragePermission()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter()
        binding.recyclerView.adapter = photoAdapter
    }

    private fun setupViewModel() {
//        viewModel = ViewModelProvider(this)[PhotoViewModel::class.java]

        // 观察数据变化
        viewModel.photos.observe(this) { photoList ->
            photoAdapter.submitList(photoList)
            showPhotos()
        }

        viewModel.prhotoError.observe(this) { errorMsg ->
            showErrorMessage(errorMsg)
        }

        viewModel.isLoading.observe(this) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            loadPhotos()
        } else {
            binding.btnRequestPermission.setOnClickListener {
                requestPermissionLauncher.launch(permission)
            }
            binding.btnRequestPermission.visibility = View.VISIBLE
        }
    }

    private fun loadPhotos() {
        binding.btnRequestPermission.visibility = View.GONE
        viewModel.loadPhotos()
    }

    private fun showPhotos() {
        binding.recyclerView.visibility = View.VISIBLE
        binding.tvError.visibility = View.GONE
    }

    private fun showErrorMessage(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }
}