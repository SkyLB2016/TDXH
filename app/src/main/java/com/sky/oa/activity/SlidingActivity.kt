package com.sky.oa.activity

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.graphics.RectF
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.sky.base.ui.BaseMActivity
import com.sky.base.utils.FileUtils
import com.sky.base.utils.LogUtils
import com.sky.base.utils.ScreenUtils
import com.sky.oa.R
import com.sky.oa.adapter.ArticleAdapter
import com.sky.oa.adapter.CatalogueAdapter
import com.sky.oa.databinding.ActivitySlidingBinding
import com.sky.oa.data.model.ChapterEntity
import com.sky.oa.data.model.PoetryEntity
import com.sky.oa.pop.CatalogPop
import com.sky.oa.repository.NotesRepository
import com.sky.oa.vm.SlidingViewModel
import com.sky.oa.vm.UiState
import kotlinx.coroutines.launch

/**
 * Created by SKY on 2018/3/16.
 */
class SlidingActivity : BaseMActivity<ActivitySlidingBinding, SlidingViewModel>() {
    private lateinit var cataAdapter: CatalogueAdapter
    private var gravity = Gravity.LEFT

    lateinit var adapter: ArticleAdapter

    private lateinit var clipM: ClipboardManager

    class ArticleModel : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SlidingViewModel(NotesRepository()) as T
        }
    }

    override val viewModel: SlidingViewModel by viewModels {
        ArticleModel()
    }

    override fun inflateBinding() = ActivitySlidingBinding.inflate(layoutInflater)
    override fun initViews() {
        binding.appBar.toolbar.setNavigationIcon(R.mipmap.ic_menu)
        binding.appBar.toolbar.setNavigationOnClickListener {
            binding.sliding.toggleMenu()
        }
        binding.appBar.tvRight.text = "3.16建"

        adapter = ArticleAdapter()
        binding.recycler.adapter = adapter

        clipM = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        binding.tvLast.setOnClickListener { upToChapter() }
        binding.tvCatalog.setOnClickListener { showCatalogPop(adapter?.datas) }
        binding.tvNext.setOnClickListener { nextChapter() }

        binding.sliding.menuState = { state ->
            LogUtils.i("state==$state")
        }


    }

    private fun addAni() {
        val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.anim_layout)
        controller.delay = 0.5f
        controller.order = LayoutAnimationController.ORDER_NORMAL
        binding.recycler.layoutAnimation = controller
    }


    override fun loadDatas() {
        val filePath = "Documents/文学/道家/道德经.txt"
        val text = getDocument(filePath)
        binding.appBar.tvCenter.text = text.lines()[0]
        viewModel.loadChapter(filePath)
        viewModel.getaArticles()
    }

    override fun setObservers() {
        addFlowView()
        collectUiState()
    }

    private fun addFlowView() {
        collectFlow(viewModel.flowState) { state ->
            when (state) {
                is UiState.Loading -> {

                }

                is UiState.Success -> {
                    var articles = state.datas
                    flowAddView(articles)
                }

                is UiState.Error -> {
                    showToast("数据加载失败")

                }
            }
        }
    }

    fun flowAddView(articles: List<PoetryEntity>) {
//        private lateinit var poetries: ArrayList<PoetryEntity>
        cataAdapter = CatalogueAdapter() { poetry ->
            binding.appBar.tvCenter.text = poetry.name
            viewModel.loadChapter(poetry.filePath)
//            println("$index,${poetry.name}")

        }
        binding.reCatalog.apply {
            layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)//瀑布流布局
            adapter = cataAdapter
        }
        cataAdapter.submitList(articles)
//        var tv: TextView
//        for ((index, poetry) in articles.withIndex()) {
//            tv =
//                LayoutInflater.from(this).inflate(R.layout.item_tv, binding.flow, false) as TextView
////            tv.width = resources.getDimensionPixelSize(R.dimen.wh_96)
//            tv.minWidth = resources.getDimensionPixelSize(R.dimen.wh_48)
//            tv.textSize = 18f
//            tv.maxLines = 1
////            tv.text = text.substringAfterLast("/", ".").substringBefore(".", "")
//            tv.text = poetry.name
//            tv.setPadding(20, 6, 20, 6)
//            tv.id = index
//            tv.tag = poetry.filePath
//            binding.flow.addView(tv)
//            tv.setOnClickListener(selectArticle)
//            println("$index,${poetry.name}")
//        }
    }

    fun collectUiState() {
//        等待数据加载完成
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is UiState.Loading -> {
                            LogUtils.i("数据加载中")

                        }

                        is UiState.Success -> {
                            LogUtils.i("数据加载成功")
                            adapter.datas = state.datas as MutableList<ChapterEntity>
//                            LogUtils.i(it.toString())
                        }

                        is UiState.Error -> {
                            showToast("数据加载失败")

                        }
                    }

                }
            }
        }
    }

    private fun getDocument(sign: String): String {
        return FileUtils.readAssestToChar(this, sign)
    }

    private val selectArticle = View.OnClickListener { v ->
        val filePath = v.tag as String
        val name = filePath.substringAfterLast("/").substringBefore(".")
        binding.appBar.tvCenter.text = name
        viewModel.loadChapter(filePath)
//        viewModel.loadChapter(poetries[v.id].filePath)
    }

    private fun nextChapter() {
        val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        layoutManager.scrollToPositionWithOffset(position + 1, 0)
        layoutManager.stackFromEnd = true
    }

    private fun upToChapter() {
        val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
        val position = layoutManager.findFirstVisibleItemPosition()
        binding.recycler.scrollToPosition(position - 1)
    }

    private fun moveToChapter(position: Int) {
        val layoutManager = binding.recycler.layoutManager as LinearLayoutManager
        layoutManager.scrollToPositionWithOffset(position, 0)
        layoutManager.stackFromEnd = true
    }

    private fun showCatalogPop(floders: List<ChapterEntity>?) {
        val floderPop = CatalogPop(
            LayoutInflater.from(this).inflate(R.layout.include_recycler, null),
            ScreenUtils.getWidthPX(this), (ScreenUtils.getHeightPX(this) * 0.7).toInt()
        )
        floderPop?.datas = floders
        floderPop?.setOnItemClickListener { _, position -> moveToChapter(position) }
        floderPop?.showAtLocation(window.decorView, Gravity.CENTER, 0, 0)
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
                if (Math.abs(ev.x - downX) < 5 && Math.abs(ev.y - downY) < 1 && rect.contains(
                        ev.x,
                        ev.y
                    ) && binding.sliding.isClose
                )
                    binding.llBottomBar.visibility =
                        if (binding.llBottomBar.visibility == View.GONE) View.VISIBLE else View.GONE
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("GestureBackNavigation")
    override fun onBackPressed() {
        when {
            binding.sliding.isOpen -> binding.sliding.toggleMenu()
            binding.llBottomBar.visibility == View.VISIBLE -> binding.llBottomBar.visibility =
                View.GONE

            else -> super.onBackPressed()
        }
    }
}