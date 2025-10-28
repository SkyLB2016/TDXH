package com.sky.oa.activity

//import com.scwang.smartrefresh.layout.api.RefreshLayout
//import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sky.base.ui.BaseMActivity
import com.sky.base.utils.SDCardUtils
import com.sky.base.utils.ScreenUtils
import com.sky.base.widget.BasePop
import com.sky.oa.R
import com.sky.oa.adapter.LoaderUriAdapter
import com.sky.oa.databinding.ActivityUriBinding
import com.sky.oa.data.model.ImageFloder
import com.sky.oa.pop.FloderPop
import com.sky.oa.pop.URIPop
import com.sky.oa.repository.ImageRepository
import com.sky.oa.vm.ImageUriViewModel
import java.io.File
import java.io.FilenameFilter

/**
 * Created by SKY on 2015/11/28.
 * 加载本地图片
 */
class ImageUriActivity : BaseMActivity<ActivityUriBinding, ImageUriViewModel>() {
    //瀑布流布局
    private var layoutManager: StaggeredGridLayoutManager? = null
    private var firstVisibleItem: Int = 0//初始可见item
    private var lastVisibleItem: Int = 0//最后一个可见item

    private var firstTail = false //第一次到达底部

    private var first = true

    lateinit var adapter: LoaderUriAdapter
    private var floderPop: BasePop<*>? = null

    class ImageUri : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            ImageUriViewModel(ImageRepository()) as T
    }

    override fun inflateBinding() = ActivityUriBinding.inflate(layoutInflater)
    override val viewModel: ImageUriViewModel by viewModels {
        ImageUri()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbar(binding.appBar.toolbar, "本地图片加载")

        binding.swipe.swipe.isEnabled = false
        //设置swipe的开始位置与结束位置
        binding.swipe.swipe.setProgressViewOffset(
            false,
            0,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, resources.displayMetrics)
                .toInt()
        )
        //为进度圈设置颜色
        binding.swipe.swipe.setColorSchemeResources(
            android.R.color.black,
            android.R.color.holo_green_dark,
            android.R.color.white
        )

        //下拉刷新监听
        binding.swipe.swipe.setOnRefreshListener {
        }

        adapter = LoaderUriAdapter()
        adapter.onItemClickListener = { _, position -> showImagePop(position) }

        binding.swipe.recycler.setHasFixedSize(true)
        layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)//瀑布流布局
        binding.swipe.recycler.layoutManager = layoutManager
        //        recycler.setLayoutManager(new StaggeredGridLayoutManager(this,null,0,0));
        //        recycler.setLayoutManager(new GridLayoutManager(this,3));
        binding.swipe.recycler.adapter = adapter

        binding.swipe.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        binding.relative.setOnClickListener {
            if (!floderPop!!.isShowing)
                floderPop!!.showAtLocation(window.decorView, Gravity.BOTTOM, 0, 0)
//            floderPop?.showAtLocation(binding.relative, Gravity.BOTTOM, 0, 0)
        }
        if (!SDCardUtils.isSDCardEnable()) showToast("暂无外部存储")
        else viewModel.checkDiskImage(this)

        viewModel.getLiveDataFloders().observe(this) {
            showFloderPop(it)
        }
        viewModel.getLiveDataParent().observe(this) {
            setAdapterData(it)
        }
    }

    var total = 0

    //上划加载
    private fun loadMore() {
        if (total <= adapter.datas.size) {
            showToast("已无更多")
//        } else {
//            page++
//            getDataList()
        }
    }

    private fun showImagePop(position: Int) {
        val imagePop = URIPop(LayoutInflater.from(this).inflate(R.layout.viewpager, null))
        imagePop.parentPath = adapter.parentPath
        imagePop.datas = adapter.datas
        imagePop.setCurrentItem(position)
        if (!imagePop.isShowing) imagePop.showAtLocation(
            binding.swipe.recycler,
            Gravity.CENTER,
            0,
            0
        )
    }

    fun onRecyclerScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE)
            adapter?.setImageLoader(firstVisibleItem, lastVisibleItem, recyclerView!!)
    }

    fun onRecyclerScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        //获取可见的第一个与最后一个item
        val firstPositions =
            layoutManager!!.findFirstVisibleItemPositions(IntArray(layoutManager!!.spanCount))
        firstVisibleItem = getMinPositions(firstPositions)
        val lastPositions =
            layoutManager!!.findLastVisibleItemPositions(IntArray(layoutManager!!.spanCount))
        lastVisibleItem = getMaxPositions(lastPositions)
        if (firstVisibleItem == -1) firstVisibleItem = 0
        //首次加载执行
        if (lastVisibleItem > -1 && first) {
            first = false
            adapter?.setImageLoader(firstVisibleItem, lastVisibleItem, binding.swipe.recycler)
        }
    }

    private fun getMinPositions(firstPositions: IntArray): Int {
        return firstPositions.indices
            .map { firstPositions[it] }
            .minOf { it }

    }

    private fun getMaxPositions(lastPositions: IntArray): Int {
        return (lastPositions.indices)
            .map { lastPositions[it] }
            .maxOf { it }
    }

    private fun showFloderPop(floders: List<ImageFloder>) {
        floderPop = FloderPop(
            LayoutInflater.from(this).inflate(R.layout.include_recycler, null),
            ScreenUtils.getWidthPX(this), (ScreenUtils.getHeightPX(this) * 0.7).toInt()
        )
//        floderPop?.datas = floders
        floderPop?.setOnItemClickListener { _, position ->
            setAdapterData(File(floders[position].dirPath!!))
            floderPop?.dismiss()
        }
    }

    private fun setAdapterData(parent: File?) {
        if (parent == null) return
        first = true
        adapter.clearDatas()
        val imageNames = mutableListOf(*parent!!.list(filter))
        adapter.parentPath = parent.absolutePath
        adapter.datas = imageNames
        binding.flodername.text = parent.name
        binding.number.text = "共${imageNames.size}张图片"
        total = imageNames.size
    }

    override fun onDestroy() {
        super.onDestroy()
        adapter?.interruptExecutors()
    }

    private var filter: FilenameFilter = FilenameFilter { _, filename ->
        arrayOf(".jpg", ".JPG", ".jpeg", ".JPEG", ".png", ".PNG").any { filename.endsWith(it) }
    }
}