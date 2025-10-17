package com.sky.oa.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import com.sky.base.utils.BitmapUtils
import com.sky.base.utils.LogUtils
import com.sky.base.utils.ScreenUtils
import com.sky.oa.Constants
import com.sky.oa.R
import com.sky.oa.databinding.ActivitySolarBinding
import com.sky.base.ui.BaseActivity
import com.sky.base.widget.SolarLayout

/**
 * Created by SKY on 15/12/9 下午8:54.
 * 卫星菜单栏
 */
class SolarActivity : BaseActivity<ActivitySolarBinding>() {
    private var layoutDraw: AnimationDrawable? = null

    override fun inflateBinding() = ActivitySolarBinding.inflate(layoutInflater)

    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (msg.what == Constants.SOLAR) layoutDraw?.stop()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appBar.toolbar)
        title = ""
        binding.appBar.tvCenter.text = "卫星式菜单栏"
        showNavigationIcon()

        val width = ScreenUtils.getWidthPX(this)
        //自定义Menu 菜单的宽高
        val lp = binding.relative.layoutParams
        lp.height = width / 3
        lp.width = lp.height
        binding.relative.layoutParams = lp

        //设置 menu 的背景动画
        binding.relative.setBackgroundResource(R.drawable.solar_rect_list)
        layoutDraw = binding.relative.background as AnimationDrawable

        //自定义 子View 的宽高
        val childCount = binding.solar!!.childCount
//        val childParams = ViewGroup.LayoutParams(width / 5, width / 5)
        val childParams = ViewGroup.MarginLayoutParams(width / 5, width / 5)
        for (i in 0 until childCount - 1) binding.solar!!.getChildAt(i).layoutParams = childParams

        binding.solar?.radius = width / 3
        binding.solar?.rotateMenu = true //按钮是否旋转
        binding.solar?.isRecoverChild = false //点击子View 不收回
        binding.solar.menuState = {
            showToast(if (it) "打开" else "关闭")
            layoutDraw?.start()
            handler.sendEmptyMessageDelayed(Constants.SOLAR, 600)
        }
        binding.solar?.onItemClick = { view, position ->
            //可以把所需要跳转的activity的全称写在tag里
            when (view.tag) {
                "flow" -> showToast("position==$position")
                "list" -> showToast("position==$position")
                "viewpager" -> showToast("position==$position")
                "recyclerview" -> showToast("position==$position")
                "bitmap" -> showToast("position==$position")
                "slidingmenu" -> {
                    pickFile()
                    showToast("position==$position")
                }
            }
        }
        binding.solar?.toggleMenu(300)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_solar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_center -> binding.solar?.position = SolarLayout.CENTER
            R.id.action_left_top -> binding.solar?.position = SolarLayout.LEFT_TOP
            R.id.action_left_bottom -> binding.solar?.position = SolarLayout.LEFT_BOTTOM
            R.id.action_right_top -> binding.solar?.position = SolarLayout.RIGHT_TOP
            R.id.action_right_bottom -> binding.solar?.position = SolarLayout.RIGHT_BOTTOM
            R.id.action_center_top -> binding.solar?.position = SolarLayout.CENTER_TOP
            R.id.action_center_bottom -> binding.solar?.position = SolarLayout.CENTER_BOTTOM
            R.id.action_center_left -> binding.solar?.position = SolarLayout.CENTER_LEFT
            R.id.action_center_right -> binding.solar?.position = SolarLayout.CENTER_RIGHT
        }
        return super.onOptionsItemSelected(item)
    }

    // 打开系统的文件选择器
    private fun pickFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        this.startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val error = "文件获取失败"
        if (resultCode != Activity.RESULT_OK) return
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val uri = data!!.data //获取uri
            if (uri == null) {
                showToast(error)
                return
            }

//                int index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(index);
            LogUtils.i("columnValue==" + uri.path)

            val path = BitmapUtils.getRealPathFromURI(this, uri) //获取路径
            if (TextUtils.isEmpty(path)) {
                showToast(error)
                return
            }
            if (path.endsWith(".xlsx") || path.endsWith(".xls")) {
                showToast(path)
                showLoading()
            } else {
                showToast("文件不是Excel格式")
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1212) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showToast("选择文件需要读写文件权限")
            }
        }
    }
}