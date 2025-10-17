package com.sky.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.sky.base.api.IView
import com.sky.base.utils.LogUtils
import com.sky.base.utils.SPUtils
import com.sky.base.utils.ToastUtils
import com.sky.base.widget.DialogLoading
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 6:43 下午
 * @Description: MVVM 模式的 fragment 基类，其中v 使用 viewbinding
 */
abstract class BaseMFragment<V : ViewBinding, VM : BaseViewModel> : Fragment(), IView {
    private var _binding: V? = null
    protected val binding: V get() = _binding!!
    protected abstract val viewModel: VM

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): V
    open fun initViews() {}
    open fun loadDatas() {}
    open fun setObservers() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParameters()
    }

    open fun initParameters() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 1. 创建 ViewBinding 实例
        _binding = inflateBinding(inflater, container)
        LogUtils.i("mvvmfragment的oncreateview")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 2. 初始化视图组件
        initViews()
        // 3. 设置观察者
        setObservers()
        // 4. 加载初始数据
        loadDatas()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun setCenterTitle(tv: TextView?, title: String) {
    }

    override fun showToast(resId: Int) {
        ToastUtils.showLong(getContext(), resId)
    }

    override fun showToast(text: String) {
        ToastUtils.showLong(getContext(), text)
    }

    override fun <T> getObject(text: String?, value: T?): T? {
        return SPUtils.getInstance().get(text, value) as T?
    }

    override fun <T> setObject(text: String?, value: T?) {
        SPUtils.getInstance().put(text, value)
    }

    override fun showLoading() {
        DialogLoading.showDialog(getContext())
    }

    override fun disLoading() {
        DialogLoading.disDialog()
    }

    //    @Override
    //    public void showContent() {
    //    }
    //
    //    @Override
    //    public void onRefreshEmpty() {
    //    }
    //
    //    @Override
    //    public void onRefreshFailure(String msg) {
    //    }
    /**
     * 简化 collect StateFlow
     */
    protected fun <T> collectFlow(
        flow: StateFlow<T>,
        collector: (T) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                flow.collectLatest(collector)
            }
        }

    }

}
