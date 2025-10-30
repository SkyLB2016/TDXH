package com.sky.oa.activity

import android.app.Dialog
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.sky.base.ui.BaseMActivity
import com.sky.oa.R
import com.sky.oa.adapter.CourseAdapter
import com.sky.oa.databinding.ActivityUrlBinding
import com.sky.oa.data.model.CourseEntity
import com.sky.oa.vm.ImageUrlViewModel
import com.sky.oa.vm.UiState

/**
 * Created by SKY on 2015/11/28.
 * 加载网络图片
 */
class ImageUrlActivity : BaseMActivity<ActivityUrlBinding, ImageUrlViewModel>() {
    private var first: Boolean = false
    private var firstVisibleItem: Int = 0
    private var lastVisibleItem: Int = 0
    private var firstTail = false //第一次到达底部
    lateinit var courseAdapter: CourseAdapter
    override val viewModel: ImageUrlViewModel by viewModels()

    override fun inflateBinding() = ActivityUrlBinding.inflate(layoutInflater)
    override fun initViews() {
        setToolbar(binding!!.appBar.toolbar, "网络图片加载")
        showNavigationIcon()
        first = true
        binding.swipe.apply {
            //设置swipe的开始位置与结束位置
            setProgressViewOffset(
                false,
                0,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    80f,
                    resources.displayMetrics
                ).toInt()
            )
            //为进度圈设置颜色
            setColorSchemeResources(
                R.color.black,
                android.R.color.holo_green_dark,
                R.color.white
            )
            //下拉刷新监听
            setOnRefreshListener {
                viewModel.loadMore()
            }
        }

        courseAdapter = CourseAdapter { teacher ->
            // 这里可以跳转到详情页
            // val intent = Intent(context, TeacherDetailActivity::class.java)
            // intent.putExtra("teacher", teacher)
            // startActivity(intent)
        }
        courseAdapter.setOnImageClickListener { course ->
            course.picBig?.let {
                val url = it.replace("http://", "https://")
                println("it=$it")
                println("url=$url")
                showImageDialog(url)
            }
        }

        binding.recycler.apply {
            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(context)
            adapter = courseAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (recyclerView.canScrollVertically(1)) {
                        firstTail = false
                    }

                    if (newState == RecyclerView.SCROLL_STATE_IDLE && firstTail) {
                        viewModel.loadMore(true)
                    }
                    if (!recyclerView.canScrollVertically(1)) firstTail = true
//                    if (!recyclerView.canScrollVertically(1)) {
//                        // 滚动到底部，加载更多
//                        viewModel.loadMore()
//                    }

//                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                    val totalItemCount = layoutManager.itemCount
//                    val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
//
//                    // 当滚动到倒数第 2 个 item 时，加载更多
//                    if (totalItemCount <= lastVisibleItem + 2) {
//                        viewModel.loadMore(true)
//                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
//                    onRecyclerScrolled(recyclerView, dx, dy)
                    //  dx：大于0，向右滚动    小于0，向左滚动
                    //  dy：小于0，向上滚动    大于0，向下滚动
//                    if (dy < -1) firstTail = false
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
            showLoading()
        }

        binding.fab.setOnClickListener { v ->
            Snackbar.make(v, "正在加载，请稍后", Snackbar.LENGTH_LONG)
                .setAction("cancel") { showToast("已取消") }.show()
        }
    }

    // ✅ 显示大图弹窗
    private fun showImageDialog(imageUrl: String) {
        val dialog = Dialog(this, R.style.FullScreenDialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_viewer)

        val imageView = dialog.findViewById<ImageView>(R.id.image)

        // 使用 Glide 加载大图
        Glide.with(this)
            .load(imageUrl)
//            .placeholder(R.mipmap.ic_panda)
//            .error(R.drawable.ic_error)
//            .override(300,300)
//            .override(Target.SIZE_ORIGINAL) // 加载原始分辨率
            // .circleCrop()
            .into(imageView)

        // 点击弹窗区域关闭
        dialog.findViewById<View>(R.id.rootLayout).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun setObservers() {
        collectUiState()
    }

    private fun collectUiState() {
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                UiState.Loading -> {

                }

                is UiState.Success<*> -> {
                    val datas = state.datas as List<CourseEntity>
                    courseAdapter.submitList(datas.toList())
                    disLoading()
                    binding.swipe.isRefreshing = false
                }

                is UiState.Error -> {
                    showToast(state.message)
                    binding.swipe.isRefreshing = false
                }
            }
        }
//        collectFlow(viewModel.teachers) { datas ->
//            courseAdapter.submitList(datas)
//            disLoading()
//            binding.swipe.isRefreshing = false
//        }

    }

    private fun loadMore() {

    }

    fun onRecyclerScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        if (lastVisibleItem > 0 && first) {
            first = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}