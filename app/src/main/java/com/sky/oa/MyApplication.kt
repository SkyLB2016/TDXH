//package com.sky.oa
//
//// ✅ 现代做法：分级 + 异步初始化
//class MyApplication : Application() {
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // 第1阶段：关键路径初始化（主线程，必须的）
//        initCriticalComponents()
//
//        // 第2阶段：非关键异步初始化
//        initNonCriticalAsync()
//
//        // 第3阶段：延迟初始化（按需）
//        setupLazyInitializers()
//    }
//
//    // 阶段1：关键路径（必须立即完成的）
//    private fun initCriticalComponents() {
//        val startTime = System.currentTimeMillis()
//
//        // 1. 安全相关
//        SecurityManager.init(this)
//
//        // 2. 基础日志（不含分析）
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        }
//
//        // 3. 异常捕获
//        initCrashReporting()
//
//        // 4. 进程判断（避免重复初始化）
//        if (isMainProcess()) {
//            // 只有主进程才初始化的
//            initAppScopeDependencies()
//        }
//
//        val duration = System.currentTimeMillis() - startTime
//        Log.d("AppInit", "关键路径初始化耗时: ${duration}ms")
//    }
//
//    // 阶段2：异步初始化
//    private fun initNonCriticalAsync() {
//        // 使用后台线程
//        CoroutineScope(Dispatchers.IO).launch {
//            val startTime = System.currentTimeMillis()
//
//            // 1. 数据库
//            initDatabaseAsync()
//
//            // 2. 网络层
//            initNetworkComponents()
//
//            // 3. 图片加载
//            initImageLoaderAsync()
//
//            // 4. 只在调试时初始化
//            if (BuildConfig.DEBUG) {
//                initDebugTools()
//            }
//
//            val duration = System.currentTimeMillis() - startTime
//            Log.d("AppInit", "异步初始化耗时: ${duration}ms")
//        }
//    }
//
//    // 阶段3：按需/延迟初始化
//    private fun setupLazyInitializers() {
//        // 使用懒加载或条件初始化
//        LazyComponentManager.setup { context ->
//            // 用户同意隐私政策后初始化
//            if (PrivacyManager.isUserConsentGiven()) {
//                initAnalyticsAndAds()
//            }
//
//            // 功能模块按需初始化
//            registerLazyInitializers()
//        }
//    }
//
//    // 具体的初始化方法示例
//    private fun initCrashReporting() {
//        // 使用 App Startup 延迟初始化
//        // 或者使用专门的崩溃收集线程
//        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
//            // 先记录到本地
//            logCrashLocally(throwable)
//
//            // 再交给原来的处理器
//            defaultUncaughtExceptionHandler?.uncaughtException(thread, throwable)
//        }
//    }
//
//    private fun initDatabaseAsync() {
//        // 异步初始化 Room
//        GlobalScope.launch(Dispatchers.IO) {
//            val db = AppDatabase.getInstance(this@MyApplication)
//            // 预加载必要数据
//            db.userDao().preloadIfNeeded()
//        }
//    }
//
//    private fun initAnalyticsAndAds() {
//        // 检查用户是否同意
//        if (PrivacyManager.isAnalyticsConsentGiven()) {
//            // 初始化分析SDK
//            Analytics.init(this)
//
//            // 初始化广告（如果需要）
//            if (shouldShowAds()) {
//                AdManager.init(this)
//            }
//        }
//    }
//
//    private fun initDebugTools() {
//        // 仅调试模式初始化
//        Stetho.initializeWithDefaults(this)
//        ChuckerCollector.install(this)
//
//        // 内存泄漏检测
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return
//        }
//        LeakCanary.install(this)
//    }
//}