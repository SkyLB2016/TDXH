package com.sky.oa.activity

import android.content.Context
import android.content.Intent
import android.graphics.RectF
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sky.base.ui.BaseMActivity
import com.sky.base.utils.LogUtils
import com.sky.base.utils.SPUtils
import com.sky.base.utils.ScreenUtils
import com.sky.oa.pop.CatalogPop
import com.sky.oa.R
import com.sky.oa.databinding.ActivityPoetryBinding
import com.sky.oa.entity.PoetryEntity
import com.sky.oa.vm.ArticleViewModel
import com.sky.oa.adapter.ArticleAdapter
import com.sky.oa.entity.ChapterEntity
import com.sky.oa.repository.NotesRepository
import com.sky.oa.vm.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Created by libin on 2020/05/13 2:33 PM Wednesday.
 */
class ArticleActivity : BaseMActivity<ActivityPoetryBinding, ArticleViewModel>() {
    private lateinit var poetry: PoetryEntity
    lateinit var adapter: ArticleAdapter
    var lastPosition = -1//记录当前顶部View的位置
    var lastOffset = -1//当前顶部View 的偏移量

    companion object {
        const val KEY = "poetry"
        const val LASTPOSITION = "lastPosition"
        const val LASTOFFSET = "lastOffset"

        fun newInstance(context: Context, poetry: PoetryEntity) {
            val intent = Intent(context, ArticleActivity::class.java)
            intent.putExtra(KEY, poetry)
            context.startActivity(intent)
        }
    }

    override fun inflateBinding() = ActivityPoetryBinding.inflate(layoutInflater)

    override val viewModel: ArticleViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ArticleViewModel(NotesRepository()) as T
            }
        }
    }

    //    override fun createViewModel() =
////        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(ArtivleVM::class.java)
//        ViewModelProvider(this, object : ViewModelProvider.Factory {
//            override fun <T : ViewModel> create(modelClass: Class<T>): T {
//                return ArticleViewModel(NotesRepository()) as T
//            }
//        }).get(ArticleViewModel::class.java)
    override fun initViews() {
        super.initViews()
        poetry = intent.getSerializableExtra(KEY) as PoetryEntity
        setToolbar(binding!!.appBar.toolbar, poetry.name)

        adapter = ArticleAdapter()
        binding?.recycler?.layoutManager = LinearLayoutManager(this)
        binding?.recycler?.adapter = adapter

        //获取顶部位置，以及偏移量，在下次进入时，直接跳转到此处
        binding.recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(parent: RecyclerView, newState: Int) {
                val child = parent.layoutManager!!.getChildAt(0)
                lastOffset = child!!.top
                lastPosition = parent?.layoutManager?.getPosition(child)!!
//                LogUtils.i("${poetry.name}的$LASTPOSITION==$lastPosition")
//                LogUtils.i("${poetry.name}的$LASTOFFSET==$lastOffset")
            }
        })

        binding.tvPrevious.setOnClickListener { previousChapter() }
        binding.tvCatalog.setOnClickListener { showCatalogPop(it) }
        binding.tvNext.setOnClickListener { nextChapter(it) }
//        setLastRecord()
    }

    override fun loadDatas() {
        viewModel.loadChapter(poetry.filePath)
    }

    override fun setObservers() {
        collectUiState()
    }

    fun collectUiState() {
//        等待数据加载完成
        collectFlow(viewModel.uiState) { state ->
            when (state) {
                is UiState.Loading -> {
                    LogUtils.i("数据加载中")
                    showToast("数据加载中")

                }

                is UiState.Success -> {
                    LogUtils.i("数据加载中")
                    showToast("数据加载中")
                    adapter.datas = state.datas as MutableList<ChapterEntity>
                    adapter.notifyDataSetChanged()
//                            LogUtils.i(it.toString())
                }

                is UiState.Error -> {
                    showToast("数据加载失败")

                }
            }
        }

    }

    private fun setLastRecord() {
        lastPosition = SPUtils.getInstance().getObject("${poetry.name}的$LASTPOSITION", 0)
        lastOffset = SPUtils.getInstance().getObject("${poetry.name}的$LASTOFFSET", 0)
        if (lastOffset === 0 && lastPosition == 0) return
        LogUtils.i("${poetry.name}的$LASTPOSITION==$lastPosition")
        binding.recycler.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.recycler.viewTreeObserver.removeOnGlobalLayoutListener(this)
                LogUtils.i("已移除此监听==")
                binding.recycler.post {
                    (binding.recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        lastPosition,
                        lastOffset
                    )
                }
//                LogUtils.i("${poetry.name}的$LASTPOSITION==$lastPosition")
//                LogUtils.i("${poetry.name}的$LASTOFFSET==$lastOffset")
            }
        })
    }

    //首次进入的时候有时不能加载到指定的位置。
//    override fun onResume() {
//        super.onResume()
//        setLastRecord()
//    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val lastPosition = SPUtils.getInstance().getObject("${poetry.name}的$LASTPOSITION", 0)
        val lastOffset = SPUtils.getInstance().getObject("${poetry.name}的$LASTOFFSET", 0)
        if (lastOffset === 0 && lastPosition == 0) return
        LogUtils.i("${poetry.name}的$LASTPOSITION==$lastPosition")
        binding.recycler.post {
            (binding.recycler.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                lastPosition,
                lastOffset
            )
        }
    }

    override fun onStop() {
        super.onStop()
        if (lastOffset === -1 && lastPosition == -1) return
        SPUtils.getInstance().put("${poetry.name}的$LASTPOSITION", lastPosition)
        SPUtils.getInstance().put("${poetry.name}的$LASTOFFSET", lastOffset)
//        LogUtils.i("${poetry.name}的$LASTPOSITION==$lastPosition")
//        LogUtils.i("${poetry.name}的$LASTOFFSET==$lastOffset")
    }

    private var downX = 0f
    private var downY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when (ev!!.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
            }

            MotionEvent.ACTION_UP -> {
                val width = ScreenUtils.getWidthPX(this) / 3f
                val height = ScreenUtils.getHeightPX(this) / 3f
                val rect = RectF(width, height, width * 2, height * 2)
                if (abs(ev.x - downX) < 5 && abs(ev.y - downY) < 1 && rect.contains(
                        ev.x,
                        ev.y
                    )
                )
                    binding!!.llBottomBar.visibility =
                        if (binding!!.llBottomBar.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun previousChapter() {
        val layoutManager = binding!!.recycler.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        binding!!.recycler.scrollToPosition(position - 1)
    }

    private fun nextChapter(v: View) {
        val layoutManager = binding!!.recycler.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        layoutManager.scrollToPositionWithOffset(position + 1, 0)
        layoutManager.stackFromEnd = true
    }

    private fun showCatalogPop(v: View) {
        val floderPop = CatalogPop(
            LayoutInflater.from(this).inflate(R.layout.include_recycler, null),
            ScreenUtils.getWidthPX(this), (ScreenUtils.getHeightPX(this) * 0.7).toInt()
        )
        floderPop?.datas = adapter?.datas!!
        floderPop?.setOnItemClickListener { _, position -> moveToChapter(position) }
        floderPop?.showAtLocation(window.decorView, Gravity.CENTER, 0, 0)

    }

    private fun moveToChapter(position: Int) {
        val layoutManager = binding!!.recycler.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(position, 0)
        layoutManager.stackFromEnd = true
    }
}