package com.sky.base.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.sky.base.R

/**
 * Created by SKY on 2015/8/17 15:30.
 * 圆形图片
 */
class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    var borderColor = BORDER_COLOR //边颜色
        set(value) {
            if (value == field) return
            field = value
            invalidate()
        }
    var borderWidth = BORDER_WIDTH //边宽
        set(value) {
            if (value == field) return
            field = value
            invalidate()
        }
    private var bitmap: Bitmap? = null

    init {
        scaleType = SCALE_TYPE
        val array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0)
        borderWidth = array.getDimension(R.styleable.CircleImageView_borderWidth, borderWidth)
        borderColor = array.getColor(R.styleable.CircleImageView_borderColor, BORDER_COLOR)
        array.recycle()
    }

    override fun getScaleType() = SCALE_TYPE

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (drawable is BitmapDrawable) bitmap = drawable?.bitmap
    }

    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)//不继承，自己画
        bitmap ?: return
        //setLayerType(View.LAYER_TYPE_SOFTWARE, null)//关闭硬件加速
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = setBitmapShader()
        val radius = Math.min(width / 2 - borderWidth, height / 2 - borderWidth)
        canvas.drawCircle(pivotX, pivotY, radius, paint)

        paint.reset()//重置画笔
        //话空心圆，即外圆
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = borderColor
        paint.strokeWidth = borderWidth//
        //空心圆的半径等于内圆的半径加上边宽的一半
        val borderRadius = Math.min((width - borderWidth) / 2, (height - borderWidth) / 2)
        canvas.drawCircle(pivotX, pivotY, borderRadius, paint)
    }

    /**
     * 自适应状态下，因为控件宽高即是图片宽高，所以基本不需要计算，
     * 只在制定了明确宽高的情况下需要计算控件宽高与图片宽高的比例，缩放图片
     */
    private fun setBitmapShader(): BitmapShader {
        var localM = Matrix()
        //刨除边宽，计算图片的缩放大小
        localM.setScale((width - borderWidth * 2) / bitmap!!.width, (height - borderWidth * 2) / bitmap!!.height)
        //移动边宽的距离
        localM.postTranslate(borderWidth, borderWidth)
        val shader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader?.setLocalMatrix(localM)
        return shader
    }

    companion object {
        private val SCALE_TYPE = ImageView.ScaleType.CENTER_CROP
        private const val BORDER_WIDTH = 0f
        private const val BORDER_COLOR = Color.BLACK
    }
}
