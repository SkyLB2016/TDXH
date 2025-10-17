package com.sky.oa.adapter

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.Outline
import android.graphics.Typeface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter
import com.sky.oa.R
import com.sky.oa.databinding.AdapterUrlBinding
import com.sky.oa.entity.CourseEntity

/**
 * Created by SKY on 2015/12/9 20:52.
 */
class LoaderURLAdapter : RecyclerAdapter<AdapterUrlBinding, CourseEntity>() {
    //    private var layoutIds = ArrayList<Int>()//主体布局
//
//    init {
//        this.layoutIds = layoutIds as ArrayList<Int>
//    }
    override fun getBinding(context: Context?, parent: ViewGroup) = AdapterUrlBinding.inflate(LayoutInflater.from(context), parent, false)
    override fun onAchieveHolder(holder: MvvmHolder<AdapterUrlBinding>, binding: AdapterUrlBinding, position: Int) {
        val face = Typeface.createFromAsset(context.assets, "font/Lobster-Regular.ttf")
        binding.tvName.typeface = face
        binding.tvDescribe.typeface = face
        val viewType = getItemViewType(position)
        binding.tvName.text = "${position + 1}.${datas[position].name}"
        binding.tvDescribe.text = datas[position].description
        binding.image.tag = datas[position].picBig
//        binding.image.setBackgroundResource(R.mipmap.ic_launcher)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            //使用ripple
            //((CardView) holder.getView(R.id.cardView)).setRadius(new Random().nextInt(50));
            //((CardView) holder.getView(R.id.cardView)).setCardElevation(new Random().nextInt(50));
            binding.cardView.background = context.getDrawable(R.drawable.ripple)
            //点击效果，阴影效果
            //((CardView) holder.getView(R.id.cardView)).setCardElevation(new Random().nextInt(100));
            binding.cardView.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.drawable.state_list_animator)
            //视图裁剪
            binding.image.clipToOutline = true
            binding.image.outlineProvider = object : ViewOutlineProvider() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                override fun getOutline(view: View, outline: Outline) {
                    outline.setRoundRect(view.left, view.top, view.right, view.bottom, 30f)
                }
            }
        } else {
            binding.cardView.background = context.resources.getDrawable(R.drawable.sel_rect_10_stro_white_ff4081)
        }

//            when (viewType % layoutIds.size) {
//                0 -> setView(position)
//                1 -> setView1(position)
//                2 -> setView2(position)
//                3 -> setView3(position)
//            }
    }

//    private fun View.setView(position: Int) {
//        tvName.text = "${position + 1}.${datas[position].name}   第一种布局"
//        tvDescribe.text = datas[position].description
//        image.tag = datas[position].picBig
//        clipView()
//    }
//
//    private fun View.setView1(position: Int) {
//        tvName.text = "${position + 1}.${datas[position].name}   第二种布局"
//        tvDescribe.text = datas[position].description
//        image.tag = datas[position].picBig
//    }
//
//    private fun View.setView2(position: Int) {
//        tvName.text = "${position + 1}.${datas[position].name}   第三种布局"
//        tvDescribe.text = datas[position].description
//        image.tag = datas[position].picBig
//    }
//
//    private fun View.setView3(position: Int) {
//        tvName.text = "${position + 1}.${datas[position].name}   第四种布局"
//        tvDescribe.text = datas[position].description
//        image.tag = datas[position].picBig
//        clipView()
//    }

//    private fun View.clipView() {
//        image.setBackgroundResource(R.mipmap.ic_launcher)
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            //使用ripple
//            //((CardView) holder.getView(R.id.cardView)).setRadius(new Random().nextInt(50));
//            //((CardView) holder.getView(R.id.cardView)).setCardElevation(new Random().nextInt(50));
//            cardView.background = context.getDrawable(R.drawable.ripple)
//            //点击效果，阴影效果
//            //((CardView) holder.getView(R.id.cardView)).setCardElevation(new Random().nextInt(100));
//            cardView.stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.drawable.state_list_animator)
//            //视图裁剪
//            image.clipToOutline = true
//            image.outlineProvider = object : ViewOutlineProvider() {
//                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                override fun getOutline(view: View, outline: Outline) {
//                    outline.setRoundRect(view.left, view.top, view.right, view.bottom, 30f)
//                }
//            }
//        } else cardView.background = context.resources.getDrawable(R.drawable.sel_rect_10_stroke_white_ff4081)
//    }
}