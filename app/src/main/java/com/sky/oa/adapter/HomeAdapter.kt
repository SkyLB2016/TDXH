package com.sky.oa.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LayoutAnimationController
import android.view.animation.ScaleAnimation
import com.sky.oa.R
import com.sky.oa.adapter.itemtouch.ItemTouchHelperListener
import com.sky.oa.databinding.AdapterHomeBinding
import com.sky.oa.data.model.ActivityEntity
import com.sky.base.ui.MvvmHolder
import com.sky.base.ui.RecyclerAdapter
import java.util.*

/**
 *
 * @Description: 主页homeFragment 的adapter
 * @Author: 李彬
 * @CreateDate: 2022/3/18 5:10 下午
 * @Version: 1.0
 */
class HomeAdapter : RecyclerAdapter<AdapterHomeBinding, ActivityEntity>(), ItemTouchHelperListener {
    private val fontIcon = intArrayOf(
        R.string.font,
        R.string.font01,
        R.string.font02,
        R.string.font03,
        R.string.font04,
        R.string.font05,
        R.string.font06,
        R.string.font07,
        R.string.font08
    )

    override fun getBinding(context: Context?, parent: ViewGroup) =
        AdapterHomeBinding.inflate(LayoutInflater.from(context), parent, false)

    override fun onAchieveHolder(holder: MvvmHolder<AdapterHomeBinding>, binding: AdapterHomeBinding, position: Int) {

        //设置加载时的动画效果
        val scale = ScaleAnimation(0f, 1f, 0f, 1f)
        scale.duration = 1001
        if (position % 2 == 1) {
            val controller = LayoutAnimationController(scale, 0.5f)
            controller.order = LayoutAnimationController.ORDER_RANDOM
            (holder?.itemView as ViewGroup).layoutAnimation = controller
//            face = Typeface.createFromAsset(context.assets, "font/lobster_regular.ttf")//不对应fonticon
        } else holder?.itemView?.startAnimation(scale)
        binding.tvName.text =
            "${datas?.get(position)?.activityName}" + context?.resources?.getString(fontIcon[position % 9])

        binding.tvDescribe.text =
            context!!.resources.getString(fontIcon[8 - position % 9]) + datas?.get(position)?.describe
        binding.tvImage.text = context!!.resources.getString(fontIcon[position % 9])

        //字体，icomoon
        val face = Typeface.createFromAsset(context?.assets, "font/icomoon.ttf")
        binding.tvName.typeface = face
        binding.tvDescribe.typeface = face
        binding.tvImage.typeface = face
        binding.tvImage.textSize = 50f

        //对应的是adapter_main01与adapter_main02
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                //使用ripple
//                //((CardView) holder.getView(R.id.cardView)).setRadius(new Random().nextInt(50));
//                //((CardView) holder.getView(R.id.cardView)).setCardElevation(new Random().nextInt(50));
//                cardView.background = context.getDrawable(R.drawable.ripple)
//                //点击效果，阴影效果
//                //((CardView) holder.getView(R.id.cardView)).setCardElevation(new Random().nextInt(100));
//                cardView.stateListAnimator =
//                    AnimatorInflater.loadStateListAnimator(context, R.drawable.state_list_animator)
//                //视图裁剪
//                image.clipToOutline = true
//                image.outlineProvider = object : ViewOutlineProvider() {
//                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                    override fun getOutline(view: View, outline: Outline) {
//                        outline.setRoundRect(view.left, view.top, view.right, view.bottom, 60f)
//                    }
//                }
//            } else{
//                cardView.background = context.resources.getDrawable(R.drawable.bg_card)}
//            setOnClickListener { v -> LogUtils.i("lkjdflkajdkf") }

    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        notifyItemMoved(fromPosition, toPosition)
        Collections.swap(datas, fromPosition, toPosition)
    }

    override fun onItemSwiped() {
    }
}