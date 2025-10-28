package com.sky.oa

import android.content.Context
import com.sky.base.utils.LogUtils
import com.sky.base.utils.SPUtils
import com.sky.base.BaseApplication

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/3/11 10:24 下午
 * @Version: 1.0
 */
class App : BaseApplication() {
    companion object {
        lateinit var app: App
    }
    override fun onCreate() {
        super.onCreate()
        app = this
        setDebug(BuildConfig.DEBUG)
        LogUtils.isDebug = BuildConfig.DEBUG
        SPUtils.init(this)
    }
}