package com.sky.base.widget

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import com.sky.base.R
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by SKY on 2015/4/9 21:10:39.
 * 卫星式菜单，以最后一个子控件为菜单控件
 * Math的三角函数算法：
var angle = 30.0//角度
val radians = Math.PI * angle / 180//转换成弧度
val ix = 200 * Math.cos(radians)
val iy = 200 * Math.sin(radians)
0-360 度，Cos取值范围1..0..-1..0..1，即+- -+，Sin 取值范围0..1..0..-1..0,即++- -
以 x=Cos,y=Sin 算为++，-+，- -，+-，即一二三四象限，以X正轴1为起点，在正常象限中为逆时针方向画圆，画布上Y轴正负颠倒，所以是顺时针方向计算xy的坐标
以 x=Sin,y=Cos 算为++，+-，- -，-+，即一四三二象限，以Y正轴1为起点，在正常象限中为顺时针方向画圆，画布上Y轴正负颠倒，所以是逆时针方向计算xy的坐标
val ix = 200 * Math.sin(radians)
val iy = 200 * Math.cos(radians)
 */
class SolarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    var radius: Int = RADIUS
    var position: Int = CENTER
        set(value) {
            if (value == field) return
            if (isOpen) toggleMenu(TIME)
            field = value
            requestLayout()
        }
    var mState = State.CLOSE
    var rotateMenu = true//中心菜单控件旋转
    var isRecoverChild = true//点击子View，是否需要收回子控件
    var isRotating = false//是否正在执行动画

    lateinit var menuState: (Boolean) -> Unit  //中心菜单的点击事件
    lateinit var onItemClick: (View, Int) -> Unit //子控件的点击事件

    private val isOpen: Boolean get() = mState === State.OPEN

    init {
        val style = context.obtainStyledAttributes(attrs, R.styleable.solar_layout)
        //            context.theme.obtainStyledAttributes(attrs, R.styleable.solar_layout, defStyleAttr, 0)
        radius = style.getDimensionPixelSize(R.styleable.solar_layout_radius, RADIUS)
        position = style.getInt(R.styleable.solar_layout_position, CENTER)
        style.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        //1.初始化 子控件 的宽高，
        // 同时也需要测量 AT_MOST模式即wrap_content时测量父控件的宽高
        var measureWidth = 0
        var measureHeight = 0
        var child: View
        var lp: MarginLayoutParams
        var childWidth = 0
        var childHeight = 0
        val childCount = childCount // 内部子控件的总数
        for (i in 0 until childCount) {
            child = getChildAt(i)
            //获取之前需要先计算 子View 所占的空间
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            //获取 带有 margin 的lp
            lp = child.layoutParams as MarginLayoutParams
            //计算child 的宽高
            childWidth = child.measuredWidth + lp.leftMargin + lp.rightMargin
            childHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin

            //计算 子控件 覆盖区域 所占的宽高
            measureWidth = measureWidth.coerceAtLeast(childWidth)
            measureHeight = measureHeight.coerceAtLeast(childHeight)
        }

        //2.子view初始化完毕，如果group 的宽高都是 EXACTLY 模式，测量可以结束了。
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY)
            return setMeasuredDimension(widthSize, heightSize)

        //3.如果有AT_MOST 模式还需要计算实际的宽高
        //控件的起始位置在四个角上时,group 的宽高
        measureWidth += radius
        measureHeight += radius
        when (position) {
            CENTER -> {//居中，半径翻倍
                measureWidth += radius
                measureHeight += radius
            }
            CENTER_LEFT, CENTER_RIGHT -> measureHeight += radius //中左，中右，高的半径翻倍
            CENTER_TOP, CENTER_BOTTOM -> measureWidth += radius //中上，中下，宽的半径翻倍
        }
        //加入 padding 间隔
        measureWidth += paddingLeft + paddingRight
        measureHeight += paddingTop + paddingBottom

        //4.宽高计算完毕，还要判断测量宽高是否超过最大值
        if (measureWidth > widthSize) measureWidth = widthSize
        if (measureHeight > heightSize) measureHeight = heightSize
        setMeasuredDimension(
            if (widthMode == MeasureSpec.EXACTLY) widthSize else measureWidth,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else measureHeight
        )
    }

    override fun generateLayoutParams(p: LayoutParams?) = MarginLayoutParams(p)
    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childCount = childCount
        //获取菜单控件，定义点击事件
        val menu = getChildAt(childCount - 1)
        val menuWidth = menu.measuredWidth
        val menuHeight = menu.measuredHeight
        menu.setOnClickListener { onMenuClick(it) }
        //开始布局控件的位置
        var child: View
        var childWidth: Int
        var childHeight: Int
        var childLeft: Int //子控件的左间距
        var childTop: Int //子控件的右间距
        var lp: MarginLayoutParams
        for (i in 0 until childCount) {
            child = getChildAt(i)
            childWidth = child.measuredWidth
            childHeight = child.measuredHeight
            lp = child.layoutParams as MarginLayoutParams
//            LogUtils.i("childWidth==$childWidth")
//            LogUtils.i("childWidth1==${lp.width}")
//            LogUtils.i("childHeight==$childHeight")
//            LogUtils.i("childHeight1==${lp.height}")
            childLeft = getChildPaddingLeft(menuWidth, childWidth, lp)
            childTop = getChildPaddingTop(menuHeight, childHeight, lp)
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

    //计算左间距，九个位置，分三列
    private fun getChildPaddingLeft(menuWidth: Int, childWidth: Int, lp: MarginLayoutParams): Int {
        return when (position) {
            LEFT_TOP, CENTER_LEFT, LEFT_BOTTOM -> {//左侧的一列
                paddingLeft + lp.leftMargin + (menuWidth - childWidth) / 2
            }
            CENTER_TOP, CENTER, CENTER_BOTTOM -> {//中间的一列
                (width - paddingLeft - paddingRight - childWidth) / 2 + paddingLeft
            }
            RIGHT_TOP, CENTER_RIGHT, RIGHT_BOTTOM -> {//右侧的一列
                width - paddingRight - lp.rightMargin - childWidth - (menuWidth - childWidth) / 2
            }
            else -> 0
        }
    }

    //计算顶部间距，九个位置，分三行
    private fun getChildPaddingTop(menuHeight: Int, childHeight: Int, lp: MarginLayoutParams): Int {
        return when (position) {
            LEFT_TOP, CENTER_TOP, RIGHT_TOP -> {//上一行
                paddingTop + lp.topMargin + (menuHeight - childHeight) / 2
            }
            CENTER_LEFT, CENTER, CENTER_RIGHT -> {//中间行
                (height - paddingTop - paddingBottom - childHeight) / 2 + paddingTop
            }
            LEFT_BOTTOM, CENTER_BOTTOM, RIGHT_BOTTOM -> {//最下行
                height - paddingBottom - lp.bottomMargin - (menuHeight - childHeight) / 2 - childHeight
            }
            else -> 0
        }
    }

    //菜单的点击事件
    private fun onMenuClick(v: View) {
        if (isRotating) return
//        menuState.invoke(!isOpen)
        menuState(!isOpen)//打开状态点击后，就关闭了，在这个位置上，mState还没更新，所以取反
        if (rotateMenu)
            ObjectAnimator.ofFloat(v, "rotation", 0f, 720f)
                .setDuration(1000)
                .start()
        toggleMenu(TIME)//开始处理子菜单
    }

    fun toggleMenu(time: Long) {
        isRotating = true //设定动画的状态，不设置的话，会多次执行，呈现一种混乱的效果
        val childCount = childCount
        var tranX: ObjectAnimator
        var tranY: ObjectAnimator
        var set: AnimatorSet
        var childAt: View
        var num = childCount - 2//分成多少份弧度
        //每份的弧度
        var radians = when (position) {
            LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM -> Math.PI / 2 / num
            CENTER_TOP, CENTER_BOTTOM, CENTER_LEFT, CENTER_RIGHT -> Math.PI / num
            else -> Math.PI * 2 / ++num//居中时，相当于画了一个圆，份数多一份
        }
        //偏移弧度+起始角度
        var offsetR = when (position) {
            LEFT_BOTTOM, CENTER_LEFT -> Math.PI * 3 / 2
            RIGHT_TOP, CENTER_RIGHT -> Math.PI / 2
            RIGHT_BOTTOM, CENTER_BOTTOM -> Math.PI
            else -> 0.0//其他时候为0
        }
        for (i in 0 until childCount - 1) {
            childAt = getChildAt(i)
            childAt.setOnClickListener { v ->
                onItemClick?.invoke(v, i)
                childAnimator(i)//子view点击之后的效果
                if (isRecoverChild) toggleMenu(TIME)
            }
            //开始计算每个view的弹出位置
            val ix = radius * cos(offsetR + radians * i).toFloat()
            val iy = radius * sin(offsetR + radians * i).toFloat()
            // 根据菜单的状态判断是弹出还是收回，平移的动画定义
            if (mState === State.CLOSE) {
                tranX = ObjectAnimator.ofFloat(childAt, "translationX", 0f, ix)
                tranY = ObjectAnimator.ofFloat(childAt, "translationY", 0f, iy)
            } else {
                tranX = ObjectAnimator.ofFloat(childAt, "translationX", ix, 0f)
                tranY = ObjectAnimator.ofFloat(childAt, "translationY", iy, 0f)
            }
            //把几个动画组合在一起
            set = AnimatorSet()
            set.interpolator = BounceInterpolator()//差值器
            set.playTogether(tranX, tranY, ObjectAnimator.ofFloat(childAt, "rotation", 0f, 360f))
            set.duration = time
            set.startDelay = (i + 1) * 100L//每个的子view的延迟时间
            set.start()
            if (isRotating && i === childCount - 2)
                set.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        isRotating = false//动画停止至为false
                    }
                })
        }
        // 执行完成后切换菜单状态
        mState = if (mState === State.OPEN) State.CLOSE else State.OPEN
    }

    /**
     * 子view的动画效果
     *
     * @param pos 选中的view
     */
    private fun childAnimator(pos: Int) {
        var scale: ObjectAnimator
        var childAt: View
        var set: AnimatorSet
        for (i in 0 until childCount - 1) {
            childAt = getChildAt(i)
            scale = when (i) {
                pos -> ObjectAnimator.ofFloat(childAt, "scaleX", 1f, 2f, 1f)
                else -> ObjectAnimator.ofFloat(childAt, "scaleY", 1f, 0f, 1f)
            }
            set = AnimatorSet()
            set.playTogether(scale, ObjectAnimator.ofFloat(childAt, "alpha", 1f, 0f, 1f))
            set.duration = 300 * (i + 1L)
            set.start()
        }
    }

    companion object {
        const val LEFT_TOP = 0
        const val LEFT_BOTTOM = 1
        const val RIGHT_TOP = 2
        const val RIGHT_BOTTOM = 3
        const val CENTER_TOP = 4
        const val CENTER_BOTTOM = 5
        const val CENTER = 6
        const val CENTER_LEFT = 7
        const val CENTER_RIGHT = 8

        const val RADIUS = 300
        const val TIME = 500L
    }
}