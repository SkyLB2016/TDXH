package com.sky.oa.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * Created by SKY on 2018/2/24 14:49.
 */
class ShaderText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    internal var width: Int = 0
    private var linear: LinearGradient? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = measuredWidth
        if (width > 0) {
            linear =
                LinearGradient(0f, 0f, width.toFloat(), 0f, intArrayOf(Color.RED, Color.GREEN, Color.BLUE), null, Shader.TileMode.CLAMP)
            paint?.shader = linear!!
        }
    }

    private var tran: Int = 0

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val matrix = matrix
        tran += width / 20
        if (tran > width * 1.02) tran = -width
        matrix?.setTranslate(tran.toFloat(), 0f)
        linear?.setLocalMatrix(matrix)
        postInvalidateDelayed(300)
    }
}
