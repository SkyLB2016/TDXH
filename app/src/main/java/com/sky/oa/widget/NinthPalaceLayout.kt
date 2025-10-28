package com.sky.oa.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.sky.base.utils.LogUtils
import com.sky.base.utils.ScreenUtils
import com.sky.oa.R
import com.sky.oa.data.model.PointEntity

/**
 * Created by SKY on 2015/12/24 10:58.
 * 拼图游戏
 */
class NinthPalaceLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val orginal = ArrayList<PointEntity>()//原数据
    private val select = ArrayList<PointEntity>()//选中的
    private var passWord = ArrayList<PointEntity>()//密码

    var isSure = true//默认是正确的

    var onSuccess: (Boolean) -> Unit = {}

    private val piece = 3//几行几列
    private var once = true

    init {
        setBackgroundResource(R.color.alpha_99)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (once) {
            once = false
            val width = ScreenUtils.getWidthPX(context)
            val height = ScreenUtils.getHeightPX(context)
            var offsetsX = 0
            var offsetsY = 0
            var pieceWidth = 0
            if (width > height) {
                offsetsX = (width - height) / 2
                pieceWidth = height / piece
            } else {
                offsetsY = (height - width) / 2
                pieceWidth = width / piece
            }
            val radius = pieceWidth / 4;
            for (i in 0..8) {
                val left = i % piece * pieceWidth + offsetsX
                val top = i / piece * pieceWidth + offsetsY
                val rect = Rect(
                    left + radius,
                    top + radius,
                    left + pieceWidth - radius,
                    top + pieceWidth - radius
                )
                orginal.add(PointEntity(i, rect, radius * 1f))
            }
            butterfly()
        }
        setMeasuredDimension(ScreenUtils.getWidthPX(context), ScreenUtils.getHeightPX(context)!!)
    }

    fun shuffle() {
        select.clear()
        for (i in orginal) select.add(i)
        select.shuffle()
        invalidate()
    }

    private fun fiveStar() {
        select.clear()
        //五角星
        select.add(orginal[3])
        select.add(orginal[4])
        select.add(orginal[5])
        select.add(orginal[6])
        select.add(orginal[1])
        select.add(orginal[8])
        select.add(orginal[3])
        invalidate()
    }

    //蜻蜓dragonfly或者蝴蝶
    private fun butterfly() {
        select.clear()
        select.add(orginal[0])
        //        select.add(orginal[8])
        select.add(orginal[4])
        select.add(orginal[7])
        select.add(orginal[1])
        select.add(orginal[6])
        select.add(orginal[2])
        select.add(orginal[3])
        select.add(orginal[5])

        passWord = select.clone() as ArrayList<PointEntity>
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint()
        paint.color = Color.BLUE
        //原点
        for (i in orginal) {
            canvas?.drawCircle(i.rect.centerX() * 1f, i.rect.centerY() * 1f, i.radius, paint)
        }

        paint.color = if (isSure) Color.CYAN else Color.RED
        paint.strokeWidth = 5f
        //选中的点与线
        for (i in select.indices) {
            canvas?.drawCircle(
                select[i].rect.centerX() * 1f,
                select[i].rect.centerY() * 1f,
                select[i].radius,
                paint
            )
            if (i + 1 < select.size) {
                canvas?.drawLine(
                    select[i].rect.centerX() * 1f,
                    select[i].rect.centerY() * 1f,
                    select[i + 1].rect.centerX() * 1f,
                    select[i + 1].rect.centerY() * 1f,
                    paint
                )
            } else if (endX !== 0f && endY !== 0f) {
                canvas?.drawLine(
                    select[i].rect.centerX() * 1f,
                    select[i].rect.centerY() * 1f,
                    endX,
                    endY,
                    paint
                )
            }
        }
        isSure = true
    }

    private var endX = 0f
    private var endY = 0f
    private var orginalCopy = ArrayList<PointEntity>()//原数据拷贝，
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                endX = event.x
                endY = event.y
                select.clear()
                //原数据拷贝，select每添加一个，移除一个
                orginalCopy = orginal.clone() as ArrayList<PointEntity>
                for (point in orginal) if (point.rect.contains(endX.toInt(), endY.toInt())) {
                    select.add(point)
                    orginalCopy.remove(point)
                    invalidate()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                for (point in orginalCopy) {
                    if (point.rect.contains(
                            endX.toInt(),
                            endY.toInt()
                        ) && !select.contains(point)
                    ) {
                        if (select.isEmpty()) {
                            select.add(point)
                            orginalCopy.remove(point)
                            break
                        }
                        //检查上一个点与现在的点之间是否有中间点
                        val middle = checkMiddlePoint(select.last().id, point.id)
                        //判断是否已包含中间过点
                        if (middle != null && !select.contains(middle)) {
                            select.add(middle)
                            orginalCopy.remove(middle)
                        }
                        select.add(point)
                        orginalCopy.remove(point)
                        break
                    }
                }
                invalidate()
            }

            MotionEvent.ACTION_UP -> {
                endX = 0f
                endY = 0f
                if (passWord.isEmpty()) {
                    passWord = select.clone() as ArrayList<PointEntity>
                } else {
                    isSure = select == passWord
                    val flag = onSuccess(isSure)
//                    LogUtils.i("flag==$flag")
                }
                invalidate()
            }
        }
        return true
    }

    /**
     * upId: 上一个点的id
     * currentId: 上一个点的id
     * 直接连第0个与第8个点，会绕过他们之间的中点4，所以应该判断一下中间点是否已加入
     *获取中间点；
     */
    private fun checkMiddlePoint(upId: Int, currentId: Int): PointEntity? {
        val eq = EqualEntity(upId, currentId)
        //0-8,1-7,2-6,3-5中间点为4
        return if (eq.equalsTwo(0, 8)
            || eq.equalsTwo(1, 7)
            || eq.equalsTwo(2, 6)
            || eq.equalsTwo(3, 5)
        ) {
            orginal[4]
        } else if (eq.equalsTwo(0, 2)) {//0-2为1；
            orginal[1]
        } else if (eq.equalsTwo(0, 6)) {//0-6为3；
            orginal[3]
        } else if (eq.equalsTwo(8, 2)) {//8-2为5；
            orginal[5]
        } else if (eq.equalsTwo(8, 6)) {//8-6为7；
            orginal[7]
        } else {
            null
        }
    }

    class EqualEntity(var a: Int, var b: Int) {
        fun equalsTwo(c: Int, d: Int) = (a == c && b == d) || (a == d && b == c)
    }
}
