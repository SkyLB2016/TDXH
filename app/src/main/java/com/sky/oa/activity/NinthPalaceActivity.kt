package com.sky.oa.activity

import android.os.Bundle
import com.sky.oa.databinding.ActivityNinthpalaceBinding
import com.sky.base.ui.BaseActivity

/**
 * Created by SKY on 2018/5/2 10:49.
 */
class NinthPalaceActivity : BaseActivity<ActivityNinthpalaceBinding>() {
    override fun inflateBinding() = ActivityNinthpalaceBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.appBar.tvRight.text = "随机图案"
        binding.appBar.tvRight.setOnClickListener { binding.nine.shuffle() }
        binding.nine.onSuccess = { s ->
            if (s) showToast("成功")
            else showToast("失败")
        }
    }

}
