package com.sky.oa.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sky.base.ui.BaseFragment
import com.sky.base.utils.LogUtils
import com.sky.oa.databinding.FragmentMyBinding
import com.sky.oa.data.model.PoetryEntity

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/3/11 10:25 下午
 * @Version: 1.0
 */
class MyFragment : BaseFragment<FragmentMyBinding>() {
    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMyBinding.inflate(inflater, container, false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
        PoetryEntity(
            "测试名称", ".txt",
            "dir/fileName",
            "父级目录",
            10000
        ).also {
            LogUtils.i("name==${it.name}")
            binding.tv.text = it.name
        }
        binding.tv.text = "我的页面"
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStart() {
        super.onStart()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onResume() {
        super.onResume()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onPause() {
        super.onPause()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onStop() {
        super.onStop()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDestroy() {
        super.onDestroy()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }

    override fun onDetach() {
        super.onDetach()
        LogUtils.i(javaClass.simpleName + id + Thread.currentThread().stackTrace[2].methodName)
    }
}