package com.sky.oa.activity

import android.Manifest
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.color.DynamicColors
import com.sky.base.ui.BaseMActivity
import com.sky.base.utils.AppUtils
import com.sky.base.utils.FileUtils
import com.sky.base.utils.LogUtils
import com.sky.oa.R
import com.sky.oa.databinding.ActivityMainBinding
import com.sky.oa.entity.PoetryEntity
import com.sky.oa.vm.MainViewModel
import java.io.File
import java.text.Collator
import java.util.LinkedList
import java.util.Locale
import kotlin.reflect.typeOf

/**
 * @Author: 李彬
 * @TIME: 2025/10/10 15:49
 * @Description:
 */
class MainActivity : BaseMActivity<ActivityMainBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()

    override fun inflateBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 在 Activity 中启用动态颜色
        DynamicColors.applyToActivitiesIfAvailable(application)
        setSupportActionBar(binding.appBar.toolbar)
        title = ""//    绑定toolBar后，把左侧的标题置为空

        // 3. (可选) 获取 ActionBar 实例进行进一步操作
        supportActionBar?.apply {
            // 显示/隐藏标题
            setDisplayShowTitleEnabled(true)
            // 显示/隐藏导航图标(返回图标)
            setDisplayHomeAsUpEnabled(false)
            // 自定义左侧导航图标(返回图标)
            setHomeAsUpIndicator(R.mipmap.nav_bar_my)
            // 显示/隐藏应用图标
            setDisplayShowHomeEnabled(false)
        }


//            binding.appBar.toolbar.title = "标题"//    setSupportActionBar 后此方法失效。
//            binding.appBar.tvCenter.setText("标题测试");
//            binding.appBar.toolbar.setSubtitle("副标题");
//            binding.appBar.toolbar.setNavigationOnClickListener();
//            binding.appBar.toolbar.setOnMenuItemClickListener();

        //    配置 NavigationBottomBar ；navigation 文件中的 fragment id 要与 menu文件中 item 的 id 相对应，否则无响应。
        //    获取navigationFragment控制器
        val controller = findNavController(R.id.navFragment);
        //    1、直接使用 bottomBar 设置,kotlin 模式独有
//            binding.bottomBar.setupWithNavController(controller)
        //    2、使用 NavigationUI 配置 bottomBar 与 Fragment
        NavigationUI.setupWithNavController(binding.bottomBar, controller)


        //    配置 ToolBar 自带的标题，configuration 配置的是 navigation 文件与menu文件共同的id，配置好后相当于同页面切换，没有返回键。
        val configuration = AppBarConfiguration.Builder(
            R.id.nav_home, R.id.nav_notes, R.id.nav_article, R.id.nav_my
        ).build();
//            AppBarConfiguration configuration = new AppBarConfiguration.Builder(R.id.nav_home).build();
        //    1、使用 ToolBar 配置,kotlin 独有
//            binding.appBar.toolbar.setupWithNavController(controller, configuration)
        //    2、通过 navigationUI 配置
//            NavigationUI.setupActionBarWithNavController(this, controller, configuration);
        //    NavigationUI.setupWithNavController(binding.appBar.toolbar,controller,configuration)
        //    不配置 configuration ，切换页面后，会显示返回键
//            NavigationUI.setupActionBarWithNavController(this, controller)
        //    3、kotlin 衍生方法设置。
//            setupActionBarWithNavController(controller,configuration)
//            setupActionBarWithNavController(controller)

        //    ToolBar 设置menu 监听后，会顶掉原有的 onOptionsItemSelected 监听
//            binding.appBar.toolbar.setOnMenuItemClickListener {}

//            binding.appBar.toolbar.title = binding.bottomBar.menu.getItem(0).title
        binding.appBar.tvCenter.text = binding.bottomBar.menu.get(0).title
//        binding.appBar.tvCenter.text = binding.bottomBar.menu.getItem(0).title
        //    bottomBar 设置监听后，navigation 引导会失效，需要重新建立引导
        binding.bottomBar.setOnItemSelectedListener { item ->
            binding.appBar.tvCenter.text = item.title//    居中的标题
            NavigationUI.onNavDestinationSelected(item, controller)
            true
        }

        binding.fab.setOnClickListener {
<<<<<<< HEAD
//            viewModel.TestMethod(this)
=======
            viewModel.TestMethod(this)
>>>>>>> origin/main
        }
//        权限请求
        AppUtils.isPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1119
        )
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            1119
        )
    }

    /**
     * 统计目录下，每个文本数据的字数
     */
    fun calculationTextLength() {
        val poetries = ArrayList<PoetryEntity>()
        var dir = "Documents"//assets初始路径
        var array: Array<String>? = null//assets取出的目录名称
        val link = LinkedList<String>()
        link.add(dir)
//            val stack = Stack<String>();
//            stack.add(dir)
        var item = PoetryEntity()
        //取出Documents下的所有文本文件
        while (link.isNotEmpty()) {
            dir = link.removeFirst()
            array = assets?.list(dir)
            for (i in array!!) {
                if (i.endsWith(".txt")) {
                    item = item.copy()
//                    item = PoetryEntity()
                    item.name = i.substring(0, i.length - 4)
                    item.filePath = "$dir/$i"
                    poetries.add(item)
                } else {
                    link.add("$dir/$i")
                }
            }
        }
        val collator = Collator.getInstance(Locale.CHINA)
        poetries.sortWith(Comparator { o1, o2 -> collator.compare(o1.filePath, o2.filePath) })

        var text = ""
        for (poetry in poetries) {
//            text = FileUtils.readAssestToByte(context, poetry.path)//字节流也可以但是不建议。
            text = FileUtils.readAssestToChar(this, poetry.filePath)
                .replace("　".toRegex(), "")
                .replace("\n".toRegex(), "")
            LogUtils.i("${poetry.name}==${text.length}")
        }
        //        String poetry = FileUtils.readAssestToChar(this, "Documents/文学/道家/道德经.txt")
    }

    fun testMethod(str1: String, str2: String, operation: (String) -> String) {
        println("$str1,附加操作：{${operation(str2)}}")
    }

    /**
     * 统计目录下，每个文本数据的字数
     */
    fun calculationTextLength() {
        val poetries = ArrayList<PoetryEntity>()
        var dir = "Documents"//assets初始路径
        var array: Array<String>? = null//assets取出的目录名称
        val link = LinkedList<String>()
        link.add(dir)
//            val stack = Stack<String>();
//            stack.add(dir)
        var item = PoetryEntity()
        //取出Documents下的所有文本文件
        while (link.isNotEmpty()) {
            dir = link.removeFirst()
            array = assets?.list(dir)
            for (i in array!!) {
                if (i.endsWith(".txt")) {
                    item = item.copy()
//                    item = PoetryEntity()
                    item.name = i.substring(0, i.length - 4)
                    item.filePath = "$dir/$i"
                    poetries.add(item)
                } else {
                    link.add("$dir/$i")
                }
            }
        }
        val collator = Collator.getInstance(Locale.CHINA)
        poetries.sortWith(Comparator { o1, o2 -> collator.compare(o1.filePath, o2.filePath) })

        var text = ""
        for (poetry in poetries) {
//            text = FileUtils.readAssestToByte(context, poetry.path)//字节流也可以但是不建议。
            text = FileUtils.readAssestToChar(this, poetry.filePath)
                .replace("　".toRegex(), "")
                .replace("\n".toRegex(), "")
            LogUtils.i("${poetry.name}==${text.length}")
        }
        //        String poetry = FileUtils.readAssestToChar(this, "Documents/文学/道家/道德经.txt")
    }

    private fun fileTest() {
        val folder = File("/storage/emulated/0/AFile")
        val file = File("/storage/emulated/0/AFile/ICP备案.txt")
//            val flag = file.delete();
//            LogUtils.i("文件删除==$flag")
//            if (flag) {
//                showToast("删除成功")
//            }
        val long = FileUtils.getDirSize(folder)
        LogUtils.i("文件大小==$long")
        LogUtils.i("文件大小==${folder.getTotalSpace()}")
        LogUtils.i("文件路径==${folder.canonicalFile}")
        LogUtils.i("文件是否可读==${folder.canRead()}")
        LogUtils.i("文件是否可写==${folder.canWrite()}")
        LogUtils.i("文件是否可写==${file.canWrite()}")
        LogUtils.i("文件夹数量==${folder.listFiles().size}")
        val num = FileUtils.getNumberOfFiles(File("/storage/emulated/0/AFile"))
        LogUtils.i("文件数量==$num")
        LogUtils.i("sd卡路径==" + Environment.getExternalStorageDirectory().getAbsolutePath())
        LogUtils.i("文件夹是否存在==" + folder.exists())
        FileUtils.copyFile(
            File("/storage/emulated/0/AFile/ICP备案.txt"),
            File("/storage/emulated/0/AFile"),
            "copy.txt"
        );
//            FileUtils.deleteFile("/storage/emulated/0/AFile")
    }
}