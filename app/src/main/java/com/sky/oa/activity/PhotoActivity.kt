package com.sky.oa.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sky.base.ui.BaseMActivity
import com.sky.base.utils.ScreenUtils
import com.sky.base.widget.BasePop
import com.sky.oa.App
import com.sky.oa.R
import com.sky.oa.adapter.PhotoAdapter
import com.sky.oa.data.model.Folder
import com.sky.oa.databinding.ActivityPhotoBinding
import com.sky.oa.pop.FolderPopupwindow
import com.sky.oa.vm.PhotoViewModel

/**
 * @Author: 李彬
 * @TIME: 2025/10/13 16:59
 * @Description:
 */
class PhotoActivity : BaseMActivity<ActivityPhotoBinding, PhotoViewModel>() {
    private lateinit var photoAdapter: PhotoAdapter
    private lateinit var folderPop: BasePop<Folder>

    // 使用 Activity Result API 请求权限
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showToast("开始请求数据")
            loadPhotos()
        } else {
            showToast("需要权限才能访问照片")
        }
    }

    override val viewModel: PhotoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PhotoViewModel(App.app) as T
            }
        }
    }

    override fun inflateBinding(): ActivityPhotoBinding {
        return ActivityPhotoBinding.inflate(layoutInflater)
    }


    override fun initViews() {
        super.initViews()
        setToolbar(binding!!.appBar.toolbar, "照片")

        // 🔒 固定为竖屏

        // 或者固定为横屏
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // 或者锁定当前方向
        // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        setupRecyclerView()
        setupViewModel()
        // 检查并请求权限
        requestStoragePermission()
    }

    private fun setupRecyclerView() {
        photoAdapter = PhotoAdapter()
        binding.recycler.apply {
            layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)//瀑布流布局
//            layoutManager = LinearLayoutManager(this)
//            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = photoAdapter
        }
    }

    private fun setupViewModel() {
        // 观察数据变化
        viewModel.photos.observe(this) { folderList ->
//            showToast("文件数量==${folderList.size}")
            setAdapter(folderList[0])
//            构建文件夹的痰喘
            showFolderPop(folderList)

            binding.linear.setOnClickListener {
                if (!folderPop!!.isShowing)
                    folderPop!!.showAtLocation(window.decorView, Gravity.BOTTOM, 0, 0)
            }
        }
    }

    private fun setAdapter(folder: Folder) {
        val photos = folder.photoList
        photoAdapter.submitList(photos)
        photos.forEach { photo ->
            println(photo.toString())
        }
        binding.tvName.text = folder.folderName
        "${photos.size}张".also { binding.tvNumber.text = it }
    }

    private fun showFolderPop(folders: List<Folder>) {
        folderPop = FolderPopupwindow(
            LayoutInflater.from(this).inflate(R.layout.include_recycler, null),
            ScreenUtils.getWidthPX(this), (ScreenUtils.getHeightPX(this) * 0.7).toInt()
        )
        folderPop.datas = folders
        folderPop.setOnItemClickListener { _, position ->
            val folder = folders[position]
            setAdapter(folder)
            folderPop.dismiss()
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
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun loadPhotos() {
        showToast("开始请求数据")
        viewModel.loadPhotos()
    }

}