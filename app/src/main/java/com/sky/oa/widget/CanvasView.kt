package com.sky.oa.widget

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.widget.Scroller
import androidx.core.content.ContextCompat
import com.sky.base.utils.LogUtils
import com.sky.oa.R
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * Created by SKY on 2017/3/9 20:52.
 */
class CanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var lastX = -1f
    private var lastY = -1f
    private var cX = -1f
    private var cY = -1f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        velocity.addMovement(event)
//        velocity.clear()
//        velocity.recycle()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.rawX
                lastY = event.rawY
                cX = event.x
                cY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val dX = (lastX - event.rawX).toInt()
                val dY = (lastY - event.rawY).toInt()
                //移动view所在位置
//                layout(left + dX, top + dY, right + dX, bottom + dY)
//                offsetLeftAndRight(dX)
//                offsetTopAndBottom(dY)

//                smoothScrollTo(dX, dY)
//                scrollTo(dX, dY)//lastXY不能重置
//                (parent as View).scrollBy(dX, dY)
                scrollBy(dX, dY)

                //scrollTo中的xy是最终距离，所以使用scrollTo是，lastXY只能是按下时的点。
                lastX = event.rawX
                lastY = event.rawY

                //触摸点的圆心
                cX = event.x
                cY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocity.computeCurrentVelocity(1000)
                LogUtils.i("速度X==${velocity.xVelocity}")
                LogUtils.i("速度Y==${velocity.yVelocity}")

                lastX = -1f
                lastY = -1f
                cY = -1f
                cX = cY
            }
        }
//        invalidate()
        return true
    }

    val scroller = Scroller(context)
    val velocity = VelocityTracker.obtain()

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            postInvalidate()
        }
    }

    private fun smoothScrollTo(dX: Int, dY: Int) {
//        val scrollX = scrollX
//        val delta = dX - scrollX
//        //在1000ms内画像dX，慢慢的滑动
        scroller.startScroll(scrollX, scrollY, dX, dY, 1000)
        invalidate()
    }

    var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_panda)
    var sx = 0f
    var bw = 0
    var bh = 0
    var scale = 1f
    var isScale = false

    init {
        bw = bitmap.width
        bh = bitmap.height
        isFocusable = true

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawOval(canvas)//渐变圆
        drawBezier(canvas)//画贝赛尔曲线
        moveLayout(canvas)//移动布局坐标
        setProgress(canvas)//环形进度条
        drawClock(canvas)//画时钟，通过操作画布原点来画时钟
        maskFilter(canvas)//绘制遮罩
        canvasLine(canvas)//画直线虚线与pathEffect应用
        canvasPanda(canvas)//画熊猫

//        animate().alpha(1f)
//            .rotation(180f)
//            .setDuration(3000)
//            .withStartAction { }
//            .withEndAction { }
//            .start()
    }

    //画直线虚线：pathEffect应用
    private fun canvasLine(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 5f

        var list = (0..20).mapTo(ArrayList()) { Point(50 + it * 50, 1110) }
        //pathEffect 应用
        val effectPath = Path()
        effectPath.moveTo(list[0].x * 1f, list[0].y * 1f)//设置Path的起点
        for (i in 1 until list.size) effectPath.lineTo(list[i].x * 1f, list[i].y * 1f)
        canvas.drawPath(effectPath, paint)

        paint.pathEffect = CornerPathEffect(20f)//拐角圆角
        canvas.drawLine(50f, 1140f, 1028f, 1140f, paint)

        paint.pathEffect = DiscretePathEffect(2f, 3f)//毛茸茸的线
        canvas.drawLine(50f, 1170f, 1028f, 1170f, paint)

        paint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f, 5f, 10f), 0f)//虚线
        canvas.drawLine(50f, 1200f, 1028f, 1200f, paint)
        paint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f), 100f)//虚线
        canvas.drawLine(50f, 1210f, 1028f, 1210f, paint)

        paint.pathEffect = ComposePathEffect(CornerPathEffect(20f), DiscretePathEffect(2f, 3f))//组合
        canvas.drawLine(50f, 1230f, 1028f, 1230f, paint)

        paint.pathEffect = SumPathEffect(CornerPathEffect(20f), DiscretePathEffect(2f, 3f))//组合
        canvas.drawLine(50f, 1260f, 1028f, 1260f, paint)

        val pp = Path()
        pp.addRect(0f, 0f, 8f, 8f, Path.Direction.CCW)
        paint.pathEffect = PathDashPathEffect(pp, 12f, 0f, PathDashPathEffect.Style.ROTATE)//自定义的虚线
        canvas.drawLine(50f, 1290f, 1028f, 1290f, paint)
    }

    private fun canvasPanda(canvas: Canvas) {
        val matrix = matrix
        matrix?.reset()
        matrix.setTranslate(0f, 1320f)
//        matrix?.setScale(1.5f, 1.5f)
//        if (!isScale)
//            matrix?.setSkew(sx, 0f)
//        else matrix?.setScale(scale, scale)
        val b2 = Bitmap.createBitmap(bitmap, 0, 0, bw, bh, matrix, true)
        canvas.drawBitmap(b2, matrix, null)
    }

    /**
     * 按键操作视图
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_W -> {
                isScale = true
                if (scale < 2.0f) scale += 0.1f
                postInvalidate()
            }
            KeyEvent.KEYCODE_S -> {
                isScale = true
                if (scale > 0.5f) scale -= 0.1f
                postInvalidate()
            }
            KeyEvent.KEYCODE_A -> {
                isScale = false
                sx += 0.1f
                postInvalidate()
            }
            KeyEvent.KEYCODE_D -> {
                isScale = false
                sx -= 0.1f
                postInvalidate()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun maskFilter(canvas: Canvas) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.style = Paint.Style.FILL_AND_STROKE
        val textP = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textP.textSize = 32f
        textP.color = Color.BLUE
        textP.textAlign = Paint.Align.CENTER
        canvas.save()
        canvas.translate(0f, 800f)

        canvas.drawCircle(110f, 150f, 80f, paint)
        canvas.drawText("正常", 110f, 166f, textP)

        paint.maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawCircle(320f, 150f, 80f, paint)
        canvas.drawText("NORMAL", 320f, 166f, textP)

        paint.maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.SOLID)
        canvas.drawCircle(530f, 150f, 80f, paint)
        canvas.drawText("SOLID", 530f, 166f, textP)

        paint.maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.OUTER)
        canvas.drawCircle(740f, 150f, 80f, paint)
        canvas.drawText("OUTER", 740f, 166f, textP)

        paint.maskFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.INNER)
        canvas.drawCircle(950f, 150f, 80f, paint)
        canvas.drawText("INNER", 950f, 166f, textP)
        canvas.restore()
    }

    private fun moveLayout(canvas: Canvas) {
        val paint = Paint()
        paint.color = Color.RED
        if (lastX != -1f) canvas.drawCircle(cX, cY, 88f, paint)
        paint.color = Color.RED
        canvas.drawCircle(940f, 550f, 100f, paint)
        val num = canvas.saveLayerAlpha(0f, 0f, 1100f, 700f, 122, Canvas.ALL_SAVE_FLAG)
        paint.color = Color.GREEN
        canvas.drawCircle(1000f, 600f, 100f, paint)
        paint.color = Color.BLUE
        canvas.drawCircle(1100f, 700f, 100f, paint)
        canvas.restoreToCount(num)//移除
    }

    private fun drawClock(canvas: Canvas) {
        val paint = Paint()
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 10f
        val centerX = 600f
        val centerY = 600f
        canvas.drawCircle(centerX, centerY, 200f, paint)
        paint.style = Paint.Style.FILL

        for (i in 1..60) {
            canvas.rotate(6f, centerX, centerY)
            if (i % 5 == 0) {
                paint.strokeWidth = 8f
                canvas.drawLine(centerX, 410f, centerX, 440f, paint)
            } else {
                paint.strokeWidth = 6f
                canvas.drawLine(centerX, 410f, centerX, 420f, paint)
            }
        }

        val textP = TextPaint()
        textP.textAlign = Paint.Align.CENTER
//        var textWidth = 0f//文字宽度
        var textCenterX = 0.0
        var textCenterY = 0.0
        var startAngle = 270f//默认的角度在X，为0度，是3点所在的位置，12点，就应该在270度
        textP.textSize = 40f
        var metrics = textP.fontMetrics
        val textheight = metrics.bottom - metrics.top
        for (i in 1..12) {
            textCenterX = 135f * cos((startAngle + 30 * i) / 180 * Math.PI) + centerX
            textCenterY = 135f * sin((startAngle + 30 * i) / 180 * Math.PI) + centerY
//            canvas.drawCircle(textCenterX.toFloat(), textCenterY.toFloat() + textheight / 2 - metrics.bottom, 5f, paint)
            canvas.drawText("$i", textCenterX.toFloat(), textCenterY.toFloat() + textheight / 2 - metrics.bottom, textP)
        }
        //画表芯，时针，分针
        paint.strokeWidth = 12f
        canvas.save()
        canvas.translate(centerX, centerY)
        canvas.drawCircle(0f, 0f, 16f, paint)//表芯
        canvas.drawLine(0f, 0f, 75f, 25f, paint)//时针
        paint.strokeWidth = 8f
        canvas.drawLine(0f, 0f, 0f, 100f, paint)//分针
        canvas.restore()
    }

    private fun drawOval(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        val mShader = LinearGradient(
            0f,
            0f,
            100f,
            100f,
            intArrayOf(Color.RED, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.LTGRAY),
            null,
            Shader.TileMode.REPEAT
        ) //一个材质,打造出一个线性梯度沿著一条线。
        paint.shader = mShader
        val rect = RectF(0f, 0f, 300f, 400f)
        canvas.drawOval(rect, paint)
    }

    //画贝赛尔曲线
    private fun drawBezier(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 20f
        paint.color = Color.GREEN

        val random = Random()
        val list = (0..20).mapTo(ArrayList()) { Point(400 + it * 30, random.nextInt(390) + 10) }
        val bezierPath = calculateCubicPath(list)//获取三次方贝赛尔曲线
        canvas.drawPath(bezierPath, paint)//画出贝塞尔曲线

        //画出实际的线
        val effectPath = Path()
        effectPath.moveTo(list[0].x * 1f, list[0].y * 1f)//设置Path的起点
        for (i in 1 until list.size) effectPath.lineTo(list[i].x * 1f, list[i].y * 1f)
        paint.strokeWidth = 5f
        paint.color = Color.RED
        paint.pathEffect = CornerPathEffect(20f)//圆角
        //paint.pathEffect = DiscretePathEffect(2f, 3f)//毛茸茸的线
        //paint.pathEffect = DashPathEffect(floatArrayOf(20f, 10f, 5f, 10f), 0f)//虚线
        //val pp = Path()
        //pp.addRect(0f, 0f, 8f, 8f, Path.Direction.CCW)
        //paint.pathEffect = PathDashPathEffect(pp, 12f, 0f, PathDashPathEffect.Style.ROTATE)//自定义的虚线
        //paint.pathEffect = ComposePathEffect(CornerPathEffect(20f), DiscretePathEffect(2f, 3f))//组合
        //paint.pathEffect = SumPathEffect(CornerPathEffect(20f), DiscretePathEffect(2f, 3f))//组合
        canvas.drawPath(effectPath, paint)
    }

    private fun setProgress(canvas: Canvas) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = ContextCompat.getColor(context, R.color.color_CB03810F)
        paint.style = Paint.Style.FILL

        //圆所在矩形
        val area = RectF(50f, 450f, 350f, 750f)
        canvas.drawRect(area, paint)

        paint.style = Paint.Style.STROKE
        paint.color = Color.RED
        paint.strokeWidth = 10f

        //画圆，这里的半径是圆心到线中心的距离，所以圆的实际半径 = 线宽的一半+画圆的半径
        var start = 140f//起始角度
        var sweep = 260f//旋转角度
        paint.maskFilter = BlurMaskFilter(25f, BlurMaskFilter.Blur.SOLID)
        //真实的圆所在的矩形
        val areaCircle = RectF(area.left + 5f, area.top + 5f, area.right - 5f, area.bottom - 5f)
        canvas.drawArc(areaCircle, start, sweep, false, paint)

        //文字画笔
        val textP = TextPaint()
        textP.textSize = 40f
        textP.pathEffect = CornerPathEffect(20f)//圆角
        textP.textAlign = Paint.Align.CENTER

        val text = "进度条"
        val metrics = textP.fontMetricsInt//文本的基线数据
        val textHeight = metrics.bottom - metrics.top//文本框所占的高度
//        textP.getTextBounds(text, 0, text.length, textBound)//不建议用

        val radius = area.height() / 2
        //计算的文字所在的背景框的左侧，顶部，右侧，底部
        val leftX = radius * cos(start / 180 * Math.PI) + area.centerX() + 20//(20的偏移量，是向右移动的，所以加)
        val topY = radius * sin(start / 180 * Math.PI) + area.centerY() - textHeight
        val rightX = radius * Math.cos((start + sweep) / 180 * Math.PI) + area.centerX() - 20//(20的偏移量)
        val bottomY = topY + textHeight * 1.5//背景框一倍半的文字高度
        //文字背景所在矩形
        val textRect = Rect(leftX.toInt(), topY.toInt(), rightX.toInt(), bottomY.toInt())
        val textbg = ContextCompat.getDrawable(context, R.drawable.sel_rect_green)
        textbg?.bounds = textRect//为文字设置背景框
        textbg?.draw(canvas)//把背景框画入画布中

        //让文字居于背景中间，计算文字的左距离与底部距离
        val baseline = textRect.exactCenterY() + textHeight / 2 - metrics.bottom
        canvas.drawText(text, textRect.exactCenterX(), baseline, textP)//画入画布中
    }

    /**
     * 三次贝赛尔曲线计算
     */
    private fun calculateCubicPath(list: ArrayList<Point>): Path {
        val bezierPath = Path()
        val control = calculateCubicContro(list)
        for (i in 0..list.size - 2) {
            when (i) {
                0 -> {
                    bezierPath.moveTo(list[i].x * 1f, list[i].y * 1f)//移动到Path的起点，画下一个点
                    //第一条为二次贝赛尔曲线
                    bezierPath.quadTo(
                        control[i].x * 1f,
                        control[i].y * 1f,
                        list[i + 1].x * 1f,
                        list[i + 1].y * 1f
                    )
                }
                //最后一条为二次贝赛尔曲线
                list.size - 2 -> bezierPath.quadTo(
                    control[i * 2 - 1].x * 1f,
                    control[i * 2 - 1].y * 1f,
                    list[i + 1].x * 1f,
                    list[i + 1].y * 1f
                )
                else -> bezierPath.cubicTo(
                    control[i * 2 - 1].x * 1f, control[i * 2 - 1].y * 1f,//控制点
                    control[i * 2].x * 1f, control[i * 2].y * 1f,//控制点
                    list[i + 1].x * 1f, list[i + 1].y * 1f
                )//终点
            }
        }
        return bezierPath
    }

    /**
     * 三次贝赛尔曲线计算方法
     */
    private fun calculateCubicContro(list: ArrayList<Point>): ArrayList<Point> {
        val control = ArrayList<Point>()
        for (i in 0 until list.size - 2) {
            //通过三个点abc来确定两个中点ef
            val eX = (list[i].x + list[i + 1].x) / 2
            val eY = (list[i].y + list[i + 1].y) / 2
            val fX = (list[i + 1].x + list[i + 2].x) / 2
            val fY = (list[i + 1].y + list[i + 2].y) / 2
            //计算中点的中点g
            val gX = (eX + fX) / 2
            val gY = (eY + fY) / 2
            //计算公式:已知abc三点，efg三中点，求xy；e-x=f-b=g-y
            //计算中中点与第二个点的偏移量
            val offsetX = gX - list[i + 1].x
            val offsetY = gY - list[i + 1].y
            //计算出第一个控制点，此点用于控制a到b
            val conX1 = eX - offsetX
            val conY1 = eY - offsetY
            //计算出第二个控制点，此点用于控制b到c
            val conX2 = fX - offsetX
            val conY2 = fY - offsetY

            control.add(Point(conX1, conY1))
            control.add(Point(conX2, conY2))
        }
        return control
    }
}