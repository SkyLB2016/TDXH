package com.sky.base.ui

import android.R
import androidx.viewbinding.ViewBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.sky.base.api.IView
import com.sky.base.utils.SPUtils
import com.sky.base.utils.ToastUtils
import com.sky.base.widget.DialogLoading
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 6:43 下午
 * @Description:MVVM 模式下 activity 的基类，其中的 V 应用 viewbinding
 */
abstract class BaseMActivity<V : ViewBinding, VM : BaseViewModel> : AppCompatActivity(), IView {
    protected lateinit var binding: V
    protected abstract val viewModel: VM

    protected abstract fun inflateBinding(): V

    /**
     * 初始化视图组件的方法，子类可重写
     */
    open fun initViews() {}

    /**
     * 设置观察者的方法，子类可重写
     * 用于监听 ViewModel 中 StateFlow 的状态变化
     */
    open fun setObservers() {}

    /**
     * 加载数据的方法，子类可重写
     * 用于在页面创建时触发数据加载
     */
    open fun loadDatas() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = inflateBinding()
        setContentView(binding!!.root)
        // 2. 初始化视图组件
        initViews()
        // 3. 设置观察者
        setObservers()
        // 4. 加载初始数据
        loadDatas()
    }

    fun setToolbar(toolbar: Toolbar?, title: String?) {
        setSupportActionBar(toolbar)
        //        getSupportActionBar().setTitle(title);
        setTitle(title)
    }

    fun showNavigationIcon() {
        supportActionBar!!.setDisplayHomeAsUpEnabled(true) //就这一个也起作用，需要与 onOptionsItemSelected 配合使用
        supportActionBar!!.setHomeButtonEnabled(true) //必须与搭配第一个使用，不用这个，也行，目前没发现他的作用
        //getSupportActionBar().setDisplayShowHomeEnabled(true);//没啥用
    }

    override fun setCenterTitle(tv: TextView, title: String) {}
    override fun showToast(@StringRes resId: Int) {
        ToastUtils.showLong(this, resId)
    }

    override fun showToast(text: String) {
        ToastUtils.showLong(this, text)
    }

    override fun <T> getObject(text: String, value: T): T {
        return SPUtils.getInstance()[text, value] as T
    }

    override fun <T> setObject(text: String, value: T) {
        SPUtils.getInstance().put(text, value)
    }

    override fun showLoading() {
        if (isFinishing|| isDestroyed){
            return
        }
        DialogLoading.showDialog(this)
    }

    override fun disLoading() {
        DialogLoading.disDialog()
    }

    //Activity 自带的 menu 监听事件，toolbar不设置监听，默认使用的也是这个。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showToast("menu")
        when (item.itemId) {
            R.id.home ->{
                showToast("onbackpress")
                onBackPressed()//finish()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * 简化 collect StateFlow
     */
    protected fun <T> collectFlow(
        flow: StateFlow<T>,
        collector: (T) -> Unit
    ) {
//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.uiState.collect {state->
//                }
//            }
//        }
//        life启动协程，并和activity的生命周期绑定
        lifecycleScope.launch {
//          和activity的生命流程挂挂，一般指定 start
            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                collect 是顺序执行
//                collectLatest 只执行最新的
                flow.collectLatest(collector)
            }
        }
    }
}
