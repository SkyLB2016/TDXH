package com.sky.oa.activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sky.base.ui.BaseMActivity
import com.sky.base.utils.BitmapUtils
import com.sky.base.utils.LogUtils
import com.sky.base.utils.PhotoUtils
import com.sky.oa.R
import com.sky.oa.adapter.CourseAdapter
import com.sky.oa.adapter.LoaderURLAdapter
import com.sky.oa.databinding.ActivityUrlBinding
import com.sky.oa.data.model.CourseEntity
import com.sky.oa.repository.ImageRepository
import com.sky.oa.utils.imageloader.ImageLoaderAsync
import com.sky.oa.vm.ImageUrlViewModel
import com.sky.oa.vm.UiState
import kotlinx.coroutines.launch
import java.io.File

/**
 * Created by SKY on 2015/11/28.
 * 加载网络图片
 */
class ImageUrlActivity : BaseMActivity<ActivityUrlBinding, ImageUrlViewModel>() {
    private var first: Boolean = false
    private var firstVisibleItem: Int = 0
    private var lastVisibleItem: Int = 0
    private var firstTail = false //第一次到达底部
    lateinit var adapter: CourseAdapter
    override val viewModel: ImageUrlViewModel by viewModels()

    override fun inflateBinding() = ActivityUrlBinding.inflate(layoutInflater)
    override fun initViews() {
        setToolbar(binding!!.appBar.toolbar, "网络图片加载")
        showNavigationIcon()
        first = true
        //设置swipe的开始位置与结束位置
        binding.swipe!!.setProgressViewOffset(
            false,
            0,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, resources.displayMetrics)
                .toInt()
        )
        //为进度圈设置颜色
        binding.swipe!!.setColorSchemeResources(
            R.color.black,
            android.R.color.holo_green_dark,
            R.color.white
        )
        //下拉刷新监听
        binding.swipe!!.setOnRefreshListener {

        }

        binding.recycler!!.setHasFixedSize(true)
//        val layoutIds = ArrayList<Int>()//主体布局
//        layoutIds.add(R.layout.adapter_main_01)
//        layoutIds.add(R.layout.adapter_main_02)
//        layoutIds.add(R.layout.adapter_main_02)
//        layoutIds.add(R.layout.adapter_main_01)
        adapter = CourseAdapter()
        binding.recycler.adapter = adapter

        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                onRecyclerScrollStateChanged(recyclerView, newState)
                //SCROLL_STATE_SETTLING惯性，SCROLL_STATE_DRAGGING拖拽，SCROLL_STATE_IDLE停止、
                if (recyclerView.canScrollVertically(1)) firstTail = false
                if (newState == RecyclerView.SCROLL_STATE_IDLE && firstTail) loadMore()
                if (!recyclerView.canScrollVertically(1)) firstTail = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                onRecyclerScrolled(recyclerView, dx, dy)
                //  dx：大于0，向右滚动    小于0，向左滚动
                //  dy：小于0，向上滚动    大于0，向下滚动
                if (dy < -1) firstTail = false
                //                if (recycler.getLayoutManager() instanceof LinearLayoutManager) {
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) recycler.getLayoutManager();
////                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
////                    lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                } else if (recycler.getLayoutManager() instanceof StaggeredGridLayoutManager) {
//                    StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) recycler.getLayoutManager();
//                    int[] positions = layoutManager.findLastVisibleItemPositions(new int[layoutManager.getSpanCount()]);
//                    lastVisibleItem = findMax(positions);
//                }
            }
        })

        binding.fab.setOnClickListener { v ->
            Snackbar.make(v, "正在加载，请稍后", Snackbar.LENGTH_LONG)
                .setAction("cancel") { showToast("已取消") }.show()
        }
    }

    override fun setObservers() {
        collectUiState()
    }

    override fun onResume() {
        super.onResume()
        showLoading()

    }
    private fun collectUiState() {
//        collectFlow(viewModel.uiState) { state ->
//            when (state) {
//                UiState.Loading -> {
//
//                }
//
//                is UiState.Success<*> -> {
//                    adapter.submitList(state.datas as List<CourseEntity>)
//                }
//
//                is UiState.Error -> {
//
//                }
//            }
//        }
        collectFlow(viewModel.teachers) { datas ->
            adapter.submitList(datas)
            disLoading()
        }

    }

    private fun loadMore() {

    }

    fun onRecyclerScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
//        if (newState == RecyclerView.SCROLL_STATE_IDLE) setImageLoader(
//            firstVisibleItem,
//            lastVisibleItem,
//            binding.recycler
//        )
//        else cancelAlltasks()
    }

    fun onRecyclerScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        if (lastVisibleItem > 0 && first) {
            first = false
            setImageLoader(firstVisibleItem, lastVisibleItem, binding.recycler)
        }
    }

    private fun setImageLoader(start: Int, last: Int, recycle: RecyclerView) {
        for (i in start..last) {
//            ImageLoaderAsync.showAsyncImage(
//                recycle.findViewWithTag<View>(adapter.datas[i].picBig) as ImageView,
//                adapter.datas[i].picBig!!
//            )
////            imageLoader.showAsyncImage(image, adapter.datas[i].picBig);
        }
    }

    private fun cancelAlltasks() {
        ImageLoaderAsync.cancelAlltasks()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelAlltasks()
    }

    private lateinit var photoUtils: PhotoUtils

    private fun getPhoto(view: View, position: Int) {
//        val photoPath =
//            getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"
//        photoUtils = PhotoUtils(this, photoPath)
//        photoUtils?.setUploadPicture { photoName, bitmap ->
//            val image = view.findViewById<ImageView>(R.id.image)
//            image.setImageBitmap(bitmap)
//            adapter.datas[position].picBig = photoName
//            LogUtils.i("photo==$photoName")
//            LogUtils.i("压缩后所占内存大小==${bitmap.allocationByteCount / 1024}KB")
//            LogUtils.i("原图所占内存大小==${BitmapUtils.getBitmapFromPath(photoName).allocationByteCount / 1024 / 1024}MB")
//
//            val pathname =
//                getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + System.currentTimeMillis() % 1000 + ".jpg"
//            BitmapUtils.saveBitmapToFile(bitmap, pathname)//保存照片到应用缓存文件目录下
//            LogUtils.i("原图文件大小==${File(photoName).length() / 1024 / 1024}MB")
//            LogUtils.i("压缩后文件大小==${File(pathname).length() / 1024}KB")
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        photoUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        photoUtils?.onActivityResult(requestCode, resultCode, data)
    }
}