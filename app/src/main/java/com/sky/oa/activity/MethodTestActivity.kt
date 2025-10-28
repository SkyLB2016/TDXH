package com.sky.oa.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Animatable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.tts.TextToSpeech
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sky.base.ApiResponse
import com.sky.base.utils.AppUtils
import com.sky.base.utils.LogUtils
import com.sky.base.utils.MD5Utils
import com.sky.oa.HttpUrl
import com.sky.oa.R
import com.sky.oa.databinding.ActivityMethodBinding
import com.sky.oa.data.model.ActivityEntity
import com.sky.oa.gson.GsonUtils
import com.sky.oa.proxy.Cuthair
import com.sky.oa.proxy.DynamicProxy
import com.sky.oa.proxy.Hair
import com.sky.oa.proxy.StaticProxy
import com.sky.oa.proxy.abstractfactory.HNFactory
import com.sky.oa.proxy.abstractfactory.MCFctory
import com.sky.oa.proxy.factory.HairFactory
import com.sky.oa.proxy.factory.hair.LeftHair
import com.sky.base.ui.BaseActivity
import com.sky.oa.data.model.ActivityModel
import java.net.URL
import java.text.Collator
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by SKY on 2018/3/6 16:43.
 */
class MethodTestActivity : BaseActivity<ActivityMethodBinding>(), View.OnClickListener {

    override fun inflateBinding() = ActivityMethodBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.appBar.toolbar)
        title = "方法组合类"
        showNavigationIcon()

        val method = arrayListOf(
            "hash相同",
            "字体颜色背景变换",
            "电池电量",
            "数组排序",
            "json转换",
            "工厂模式",
            "SVG与Value",
            "渐变的文字",
            "排序算法",
            "MD5加密",
            "科学计数法",
            "获取当前方法的名称",
            "输出View的位置信息",
            "URL的结构",
            "静态代理",
            "动态代理"
        )
        for (i in method) {
            val tvText =
                LayoutInflater.from(this).inflate(R.layout.item_tv, binding.flow, false) as TextView
            tvText.setPadding(20, 6, 20, 6)
            tvText.text = i
            tvText.tag = i
            binding.flow.addView(tvText)
            tvText.setOnClickListener(this)
        }
        binding.tvDisplay.movementMethod = LinkMovementMethod.getInstance()
        binding.tvDisplay.text = ""
        val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.anim_layout)

//        val controller = LayoutAnimationController(animation)
        controller.delay = 0.5f
        controller.order = LayoutAnimationController.ORDER_NORMAL
        binding.flow.layoutAnimation = controller
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv -> {

            }
        }
        binding.tvDisplay.text = when (v?.tag) {
            "hash相同" -> equalHashCode()
            "字体颜色背景变换" -> changeText()
            "电池电量" -> "电池电量==$battery"
            "数组排序" -> sortList()
            "json转换" -> changeJson()
            "排序算法" -> sort()
            "MD5加密" -> MD5Utils.encryption("http://img.mukewang.com/55237dcc0001128c06000338.jpg")
            "科学计数法" -> format("0")
            "输出View的位置信息" -> outPutViewParameter(v)
            "URL的结构" -> getURL()
            "静态代理" -> staticProxy()
            "动态代理" -> dynamicProxy()
            else -> ""
        }
        binding.image.visibility = View.GONE
        binding.shader.visibility = View.GONE
        when (v?.tag) {
            "工厂模式" -> factoryModel()
            "SVG与Value" -> setSvg()
            "渐变的文字" -> shaderText()
            "获取当前方法的名称" -> getMethodName()
        }
    }


    private fun staticProxy(): String {
        val hair = Cuthair()
        val proxy = StaticProxy(hair)
        return proxy.cutHait(30)
    }

    private fun dynamicProxy(): String {
        val company = DynamicProxy()
//
        val hair = Cuthair()
        company.factory = hair
        val factory = company.proxyInstance as Hair
        return factory.cutHait(20)
//
//        val hair = Cuthair()
//        val proxy = Proxy.newProxyInstance(
//            MethodTestActivity::class.java.classLoader,
//            arrayOf<Class<*>>(Hair::class.java)
//        ) { proxy, method, args ->
//            method?.invoke(hair, *args)
//        }
//        val h = proxy as Hair
//        return h.cutHait(29)
    }

    /**
     * 输出view的left、top、right、bottom、x、y、translationX、translationY。
     */
    private fun outPutViewParameter(v: View): String {

        val builder = StringBuilder()
        builder.append("left == ${v.left}\n")
        builder.append("top == ${v.top}\n")
        builder.append("right == ${v.right}\n")
        builder.append("bottom == ${v.bottom}\n")
        builder.append("x == ${v.x}\n")
        builder.append("y == ${v.y}\n")
        builder.append("translationX == ${v.translationX}\n")
        builder.append("translationY == ${v.translationY}\n")
        builder.append("touchSlop == ${ViewConfiguration.get(this).scaledTouchSlop}\n")
        val layoutParams: ViewGroup.LayoutParams = v.layoutParams
        builder.append("width == ${layoutParams.width}\n")
        builder.append("height == ${layoutParams.height}\n")

        builder.append("width == ${v.width}\n")
        builder.append("height == ${v.height}\n")


        val params: ViewGroup.MarginLayoutParams = v.layoutParams as ViewGroup.MarginLayoutParams
        builder.append("width == ${params.width}\n")
        builder.append("height == ${params.height}\n")
        builder.append("layoutDirection == ${params.layoutDirection}\n")
        builder.append("leftMargin == ${params.leftMargin}\n")
        builder.append("topMargin == ${params.topMargin}\n")
        builder.append("rightMargin == ${params.rightMargin}\n")
        builder.append("bottomMargin == ${params.bottomMargin}\n")
        builder.append("marginEnd == ${params.marginEnd}\n")
        builder.append("marginStart == ${params.marginStart}\n")
        return builder.toString()
    }

    private fun getMethodName() {
        //获取当前方法的名称的两种方法。
        //1）StackTraceElement[] traceElement = Thread.currentThread().getStackTrace();
        //此方法取得的StackTraceElement栈：
        //第零条数据是VmStack的getThreadStackTrace；
        //第一条数据是Thread的getStackTrace
        //第二条数据是当前方法的所在位置。

        val builder = StringBuilder()
        builder.append(Thread.currentThread().name)

        var traceElement = Thread.currentThread().stackTrace
        builder.append("第一种")
        builder.append("\n")
        for (i in traceElement.indices) {
            val stack = traceElement[i]
            var className = stack.className
            className = className.substring(className.lastIndexOf(".") + 1)
//            builder.setLength(0)//清空字符串
            builder.append("位置：")
            builder.append(i)
            builder.append("；")
            builder.append(className)
            builder.append(".")
            builder.append(stack.methodName)
            builder.append("\n")
//            Log.i("logutils", "第" + i + "个" + builder)
        }
        traceElement = Throwable().stackTrace
        builder.append("\n")
        builder.append("第二种")
        builder.append("\n")
        for (i in traceElement.indices) {
            val stack = traceElement[i]
            var className = stack.className
            className = className.substring(className.lastIndexOf(".") + 1)
//            builder.setLength(0)
            builder.append("位置：")
            builder.append(i)
            builder.append("；")
            builder.append(className)
            builder.append(".")
            builder.append(stack.methodName)
            builder.append("\n")
//            Log.i("logutils", "第" + i + "个" + builder)
        }
        binding.tvDisplay.text = builder.toString()
    }


    /**
     * @param num 科学技术： #,###.00"
     * 0 表示如果位数不足则以 0 填充，# 表示只要有可能就把数字拉上这个位置
     * @return
     */
    private fun format(num: Double): String {
        return DecimalFormat("#,##0.00").format(num)
    }

    private fun format(num: String): String {
        return format(num.toDouble())
    }

    //观察者模式
    private fun setObserver(@LayoutRes item: Int?) {
        //观察者
        val observer =
            Observer { o, arg -> LogUtils.i("${javaClass.simpleName}==${Throwable().stackTrace[0].methodName}==$arg") }

        //被观察者
        val observable = object : Observable() {
            fun send(content: Any) {
                setChanged()
                //通知观察者
                notifyObservers(content)
            }
        }
        //把观察者注册到被观察者中
        observable.addObserver(observer)
        observable.addObserver { o, arg ->
            LogUtils.i("${javaClass.simpleName}==${Throwable().stackTrace[0].methodName}==$arg")
        }

        //被观察者发送消息
        observable.send("观察者模式")
    }

     private fun sort(): CharSequence? {
        val array = intArrayOf(99, 12, 35, 44, 5, 9, 54, 44, 10, 66)
//        val array = getArray()
        return "原始数据：共一千条随机数据；\n" +
//                "原始数据：${getArrayString(array)}；\n" +
                "冒泡排序：${bubbleSorting(array.clone())}；\n" +
//                "冒泡排序：${bubbleSort(array.clone())}；\n" +
                "选择排序：${selectSort(array.clone())}；\n" +
                "插入排序：${insertSort(array.clone())}；\n" +
                "希尔排序：${shellSort(array.clone())}；\n" +
                "归并排序：${mergeSort(array.clone())}；\n" +
                "快速排序：${quickSort(array.clone())}；\n" +
                "堆排序：${heapSort(array.clone())}；\n" +
                "计数排序：${counterSort(array.clone())}；\n" +
                "桶排序：${bucketSort(array.clone())}；\n" +
                "基数排序：${cardinalitySort(array.clone())}；\n" +
                "。"
    }

    private fun getArray(): IntArray {
        val array = IntArray(1000)
        val random = Random(10000)
        for (i in 0..499) {
            array[i] = random.nextInt(10000)
        }
        for (i in 500..999) {
            array[i] = -random.nextInt(10000)
        }
//        LogUtils.i("array==${array.toList()}")
        return array
    }

    private fun getArrayString(array: IntArray): String {
//        builder.replace(0, 1, "")//移除开始的逗号
        var builder = StringBuilder()
        for (element in array) builder.append("，$element")
        builder.deleteCharAt(0)
        builder.append("；")
        return builder.toString()
    }

    private fun getNumberOfTimes(count: Int, start: Long) =
        "${count}次；耗时：${System.currentTimeMillis() - start}ms"

    /**
     * 冒泡排序
     */
    private fun bubbleSorting(array: IntArray): String {
        val time = System.currentTimeMillis()
//        LogUtils.i("数据==${getArrayString(array)}")
        var temp: Int//临时变量
        var flag: Boolean//中断标志，如果这次的没有过交换，就证明已经排好序，跳出循环。
        var count = 0//执行了多少次
        for (i in array.indices) {
            flag = false
            for (j in 0 until array.size - 1 - i) {//减1是因为j会加1，减i是因为队尾的数据就是最终数据，不需要重复比较
                if (array[j] > array[j + 1]) {//位置1大于位置2，数组就是升序排列，换成小于号就是降序排列了
                    temp = array[j]
                    array[j] = array[j + 1]
                    array[j + 1] = temp
                    flag = true
                    count++
                }
            }
            if (!flag) break
        }
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    /**
     * 冒泡排序
     */
    private fun bubbleSort(array: IntArray): String {
//        LogUtils.i("数据==${getArrayString(array)}")
        val time = System.currentTimeMillis()
        var temp: Int//临时变量
        var flag: Boolean
        var count = 0
        for (i in 0 until array.size - 1) { //表示趟数，一共arr.length-1次。
            flag = false
            for (j in array.size - 1 downTo i + 1) {//从后往前跑
                if (array[j] < array[j - 1]) {
                    temp = array[j]
                    array[j] = array[j - 1]
                    array[j - 1] = temp
                    flag = true
                    count++
                }
            }
            if (!flag) break
        }
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    /**
     * 选择排序
     */
    private fun selectSort(array: IntArray): String {
        val time = System.currentTimeMillis()
        var min: Int//临时变量
        var temp: Int//临时变量
        var count = 0//执行了多少次
        for (i in 0..array.size - 2) {//最后就剩一个数了，交换也是自己交换，不需要比了
            min = i
            for (j in i + 1 until array.size) {//j加1，因为min=i，j==i，不用比。
                if (array[min] > array[j]) {
                    min = j//位置1大于位置2，选的是最小数，数组就是升序排列，换成小于号就是降序排列了
                    count++
                }
            }
            if (min === i) continue//min没有变化，结束此次循环
            temp = array[i]
            array[i] = array[min]
            array[min] = temp
        }
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    /**
     * 插入排序 升序排列
     */
    private fun insertSort(array: IntArray): String {
//        LogUtils.i("数据==${getArrayString(array)}")
        val time = System.currentTimeMillis()
        var count = 0//执行了多少次
        var index: Int
        var item: Int
        for (i in 1 until array.size) {//默认第零个就是排好序的，
            index = i - 1//已排好序的队尾
            item = array[i]//取得当前元素
            //升序排列就是当前元素小于之前的元素，之前的元素就向后移动一位；每一次循环前边的数据都是排好序的，所以当item大时，就放在index+1的位置。
            while (index >= 0 && array[index] > item) {
                array[index + 1] = array[index]
                index--
                count++
            }
            array[index + 1] = item
        }
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    /**
     * 希尔排序 升序排列
     */
    private fun shellSort(array: IntArray): String {
//        LogUtils.i("数据==${getArrayString(array)}")
        val time = System.currentTimeMillis()
        var count = 0//执行了多少次
        var index: Int
        var item: Int
        var gap = array.size / 2
        while (gap > 0) {
            for (i in gap until array.size) {//默认第零个就是排好序的，从增量gap开始循环
                index = i - gap//已排好序的队尾
                item = array[i]//取得当前元素
                //升序排列就是当前元素小于之前的元素，之前的元素就向后移动gap位；每一次循环前边的数据都是排好序的，所以当item大时，就放在index+gap的位置。
                while (index >= 0 && array[index] > item) {
                    array[index + gap] = array[index]
                    index -= gap
                    count++
                }
                array[index + gap] = item
            }
            gap /= 2
        }

//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    /**
     * 归并排序
     */
    private fun mergeSort(array: IntArray): String {
        sortCount = 0
        val time = System.currentTimeMillis()
        if (array == null || array.size < 2) return ""
        val result = splitArray(array)
//        return getArrayString(result) + "${sortCount}次"
        return getNumberOfTimes(sortCount, time)
    }

    /**
     * 拆分数组
     */
    private fun splitArray(array: IntArray): IntArray {
        if (array.size < 2) return array //只剩一个数组的时候返回
        val mid = array.size / 2
        val left = Arrays.copyOfRange(array, 0, mid)
        val right = Arrays.copyOfRange(array, mid, array.size)
        return mergeArray(splitArray(left), splitArray(right))
    }

    /**
     * 合并数据，数组内只有一个元素时，两个数组对比后，合并成一个肯定是有序的。
     */
    private fun mergeArray(left: IntArray, right: IntArray): IntArray {
        val result = IntArray(left.size + right.size)
        var leftIndex = 0;
        var rightIndex = 0;
        for (i in result.indices) {
            sortCount++
            when {
                //当leftIndex大于left数组长度时，左边的肯定已经加载完了，就剩右边的了，左右肯定会有一个先加载完的。
                leftIndex >= left.size -> result[i] = right[rightIndex++]
                rightIndex >= right.size -> result[i] = left[leftIndex++]
                //升序情况下，先加载小的元素。
                left[leftIndex] > right[rightIndex] -> result[i] = right[rightIndex++]
                else -> result[i] = left[leftIndex++]
            }
        }
        return result
    }

    var sortCount = 0//执行了多少次

    /**
     * 快速排序
     */
    private fun quickSort(array: IntArray): String {
        sortCount = 0
        val time = System.currentTimeMillis()
        quickArray(array, 0, array.size - 1)
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
        return getArrayString(array) + "${sortCount}次"
//        return getNumberOfTimes(sortCount, time)
    }

    private fun quickArray(array: IntArray, start: Int, end: Int) {
        //跳出递归的判断
//        if (array == null || array.isEmpty() || start < 0 || end > array.size || start >= end) return
        if (array.size < 2 || start >= end) return
        val index = partionArray(array, start, end)//分割指示器
        //当分割指示器大于起始位置的时候，证明左侧元素还未排序完成
        if (index > start) {
            quickArray(array, start, index - 1)
        }
        //当分割指示器小于尾部位置的时候，证明右侧元素还未排序完成
        if (index < end) {
            quickArray(array, index + 1, end)
        }
    }

    private fun partionArray(array: IntArray, start: Int, end: Int): Int {
//        val pivot = start + (Math.random() * (end - start + 1)).toInt()
        //选取基准数的几种方式
        //第一种：
//        val pivot = end//取最后一个，好处是不用交换了
        //第二种：
//        val pivot = start//取第一个
//        swap(array, pivot, end)//基准数移到尾部
        //第三种：取中位数
//        val pivot = getPivot(array, start, end) //取第一个与中间位置以及最后一个位置的中位数

        var index = start - 1
        val endNum = array[end]
        for (i in start..end) {
            if (array[i] <= endNum) {
                index++
                if (i > index) {
                    swap(array, index, i)
                }
            }
        }
        return index
    }

    private fun getPivot(array: IntArray, start: Int, end: Int) {
//        val mid = array.size / 2
//        val s = array[start]
//        val m = array[mid]
//        val e = array[end]
//        var max = s
//        var index = 0;
//
//        if (m > max) {
//            max = m
//        }
//        if (e > max) {
//            max = e
//        }

    }

    private fun swap(array: IntArray, pivot: Int, end: Int) {
        val temp = array[pivot]
        array[pivot] = array[end]
        array[end] = temp
        sortCount++
    }

    var length = 0;

    /**
     * 堆排序
     */
    private fun heapSort(array: IntArray): String {
        sortCount = 0
        val time = System.currentTimeMillis()
        length = array.size
        //首先构建最大堆
        buildMaxHeap(array)
//        LogUtils.i("数据==${getArrayString(array)}")
        while (length > 0) {
            swap(array, 0, --length)
            adjustHeap(array, 0)
        }
//        LogUtils.i("count==$sortCount")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(sortCount, time)
    }

    private fun buildMaxHeap(array: IntArray) {
        val lastRoot = length / 2 - 1
        for (i in lastRoot downTo 0) {
            adjustHeap(array, i)
        }
    }

    private fun adjustHeap(array: IntArray, root: Int) {
        val left = 2 * root + 1
        val right = 2 * (root + 1)
        //获取最大值的下标
        var max = root
        if (left < length && array[left] > array[max]) {
            max = left
        }
        if (right < length && array[right] > array[max]) {
            max = right
        }
        if (max != root) {//如果不相等，则根节点i上的元素需要与子结点max上的元素交换，同时因为子树的根节点max上的元素改变，所以需要判断子树的其他结点是否更大。
            swap(array, root, max)
            //子树的元素是否需要交换
            adjustHeap(array, max)
        }
    }

    /**
     * 计数排序
     */
    private fun counterSort(array: IntArray): String {
//        LogUtils.i("数据==${getArrayString(array)}")
        val time = System.currentTimeMillis()
        var count = 0//执行了多少次
        var min = array[0]
        var max = array[0]
        for (i in array) {
            max = i.coerceAtLeast(max)
            min = i.coerceAtMost(min)
        }
        val countArray = IntArray(max - min + 1)
        for (i in array) {
            countArray[i - min]++
            count++
        }
//        LogUtils.i("数据==${getArrayString(countArray)}")
        var index = 0
        var i = 0
        while (index < array.size) {
            if (countArray[i] != 0) {
                array[index++] = i + min
                countArray[i]--
                count++
            } else {
                i++
            }
        }
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    /**
     * 桶排序
     */
    private fun bucketSort(array: IntArray): String {
        sortCount = 0
        val time = System.currentTimeMillis()
        val result = bucketArray(array.toList(), 5)
//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${result}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(sortCount, time)
    }

    private fun bucketArray(array: List<Int>, bucketSize: Int): List<Int> {
        if (array == null || array.isEmpty()) return array
        var max = array[0]
        var min = array[0]
        for (i in array) {
            max = i.coerceAtLeast(max)
            min = i.coerceAtMost(min)
        }
        var bucketSize = bucketSize
        val bucketCount = (max - min) / bucketSize + 1
        val bucket = ArrayList<ArrayList<Int>>(bucketCount)
        for (i in 0 until bucketCount) {
            bucket.add(ArrayList<Int>())
        }
        for (i in array) {
            bucket[(i - min) / bucketSize].add(i)
        }

        val result = ArrayList<Int>()
//        for (i in bucket.indices)
//            LogUtils.i("第" + i + "个桶==" + bucket[i])
        for (item in bucket) {
            if (bucketSize === 1) {
                for (i in item) {
                    result.add(i)
                    sortCount++
                }
            } else {
                //如果bucketCount等于1，表明bucketSize分配过大，减一
                if (bucketCount === 1) {
                    bucketSize--
                }
                //如果bucketSize 大于当前桶的长度，那取一半作为size，
//                if (bucketSize > item.size){
//                    bucketSize = item.size / 2
//                }

                val temp = bucketArray(item, bucketSize)
                for (i in temp) {
                    result.add(i)
                    sortCount++
                }
            }
        }
        return result
    }

    /**
     * 基数排序
     */
    private fun cardinalitySort(array: IntArray): String {
//        LogUtils.i("数据==${getArrayString(array)}")
        val time = System.currentTimeMillis()
        var count = 0//执行了多少次
        var max = array[0]
        for (i in array) {
            max = max.coerceAtLeast(i)
        }
        var maxDigit = 0
        while (max != 0) {
            max /= 10
            maxDigit++
        }
        //-9..0..9一共是19个数
        val bucket = ArrayList<ArrayList<Int>>(19)
        for (i in 0..18) {
            bucket.add(ArrayList<Int>())
        }
        var remainder = 10//取余使用
        var divisor = 1//除数
        for (i in 0 until maxDigit) {
            for (item in array) {

                bucket[(item % remainder) / divisor + 9].add(item)
                count++
            }
            var index = 0
            for (list in bucket) {
                for (item in list) {
                    array[index++] = item
                    count++
                }
                list.clear()
            }
            remainder *= 10
            divisor *= 10
        }

//        LogUtils.i("count==$count")
//        LogUtils.i("数据==${getArrayString(array)}")
//        return getArrayString(array) + "${count}次"
        return getNumberOfTimes(count, time)
    }

    private fun changeStrToId(): CharSequence? {
        val text = StringBuilder()

        //第一种方法：调用resources
        val id1 = resources.getIdentifier("jsonobj", "string", packageName)
        text.append(getString(id1).replace(" ", ""))

        //第二种方法：反射
        //val id = R.string::class.java.getField("jsonobj").getInt(R.string())
        val id = R.string::class.java.getField("jsonobj").getInt("string")
        text.append(getString(id).replace(" ", ""))

        return text
    }

    private fun shaderText() {
        binding.shader.visibility = View.VISIBLE
//        shader.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.shader.text =
            "天地玄黄，宇宙洪荒，日月盈仄，辰宿列张。寒来暑往，秋收冬藏。闰余成岁，律吕调阳。云腾致雨，露结为霜。金生丽水，玉出昆冈。"
    }

    private fun setSvg() {
        binding.image.visibility = View.VISIBLE
        val animator = ValueAnimator.ofFloat(0f, binding.flow.height.toFloat())
//        value.setTarget(image)
        animator.duration = 2000
        animator.addUpdateListener { animation ->
            //            animation!!.animatedFraction//当前百分比
            val lp = binding.image.layoutParams
            lp.height = animation!!.animatedValue.toString().toFloat().toInt()
            binding.image.layoutParams = lp
        }
        animator.start()
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                (binding.image.drawable as Animatable).start()
            }
        })
    }


    /**
     * 工厂模式应用，待优化
     */
    private fun factoryModel() {
        //工厂模式
        val leftHair = LeftHair()
        leftHair.draw()
        val factory = HairFactory()
        val right = factory.getHair("right")
        right?.draw()
        val left = factory.getHairByClass("com.sky.oa.other.factory.factory.hair.LeftHair")
        left?.draw()
        val hair = factory.getHairByClassKey("in")
        hair?.draw()
        val ddddd = factory.create(LeftHair::class.java)
        ddddd.draw()

        //抽象工厂模式
        val facoty = MCFctory()
        val girl = facoty.girl
        girl.drawWomen()
        val boyfacoty = HNFactory()
        val boy = boyfacoty.boy
        boy.drawMan()
    }

    private fun changeText(): SpannableStringBuilder {
//        val text = "天行健，君子以自强不息；地势坤，君子以厚德载物。"
//        val span = SpannableString(text)
        val span = SpannableStringBuilder()
        var start = 0
        var end = 0
        span.append("天地玄黄，宇宙洪荒。\n")
        end = span.length
        span.setSpan(
            AbsoluteSizeSpan(resources.getDimensionPixelSize(R.dimen.text_18)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        span.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                showToast("始制文字，乃服衣裳。")
            }
        }, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//点击事件
//        v.setMovementMethod(LinkMovementMethod.getInstance())//点击事件还需要加入这句，好像不加也没事

        start = span.length
        span.append("日月盈昃，辰宿列张。\n")
        end = span.length
        span.setSpan(
            ForegroundColorSpan(getColor(R.color.color_3599f4)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )//字体颜色

        start = span.length
        span.append("寒来暑往，秋收冬藏。\n")
        end = span.length
        span.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//字体风格

        start = span.length
        span.append("闰余成岁，律吕调阳。\n")
        end = span.length
        span.setSpan(
            BackgroundColorSpan(Color.rgb(55, 155, 200)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        start = span.length
        span.append("云腾致雨，露结为霜。\n")
        end = span.length
        span.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//下划线

        start = span.length
        span.append("金生丽水，玉出昆冈。\n")
        end = span.length
        span.setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//中划线

        start = span.length
        span.append("剑号巨阙，珠称夜光。")
        end = span.length
        span.setSpan(SuperscriptSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//上标

        start = span.length
        span.append("果珍李柰，菜重芥姜。\n")
        end = span.length
        span.setSpan(SubscriptSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)//下标

        start = span.length
        span.append("海咸河淡，鳞潜羽翔。\n")
        end = span.length
        span.setSpan(
            RelativeSizeSpan(1.2f),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )//字号按比例翻倍

        start = span.length
        span.append("龙师火帝，鸟官人皇。\n")
        end = span.length
        span.setSpan(
            URLSpan("http://www.baidu.com"),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )//超链接

        span.append(getText(R.string.ibu))
        return span
    }

    private fun changeJson(): String {
        val model = GsonUtils.fromJson(getString(R.string.jsonobj), ActivityModel::class.java)
        val entity = GsonUtils.fromJson<ApiResponse<List<ActivityModel>>>(
            getString(R.string.jsonlist),
            object : TypeToken<ApiResponse<List<ActivityModel>>>() {}.type
        )
        val list = GsonUtils.fromJson<List<ActivityModel>>(
            getString(R.string.jsonarray),
            object : TypeToken<ArrayList<ActivityModel>>() {}.type
        )

        val array = Gson().fromJson(getString(R.string.jsonarray), Array<ActivityModel>::class.java)

        val builder = StringBuilder()
//
        builder.append("单个类：")
        builder.append("\n")
        builder.append(model.toString())
        builder.append("\n")
        builder.append("\n")

        builder.append("类中套列表：")
        builder.append("\n")
        builder.append(entity.objList.toString())
        builder.append("\n")
        builder.append("\n")

        builder.append("列表：")
        builder.append("\n")
        builder.append(list.toString())
        builder.append("\n")
        builder.append("\n")

        builder.append("数组：")
        builder.append("\n")
        builder.append("[")
        for (i in array) builder.append(i.toString())
        builder.append("]")
        builder.append("\n")
        return builder.toString()
    }

    private fun sortList(): String {
        val list = ArrayList<SortModel>()
        ('g' downTo 'a').mapTo(list) { SortModel("$it") }
        (22222 downTo 22217).mapTo(list) { SortModel("${it.toChar()}") }
        ('G' downTo 'A').mapTo(list) { SortModel("$it") }
        val builder = StringBuilder()
        builder.append("原数据：$list\n")
        list.reverse()//逆序
        builder.append("逆序：$list\n")
        list.shuffle()//随机
        builder.append("随机：$list\n")
        list.sort()//排序
        builder.append("sort排序（大写小写文字）：$list\n")
        Collections.sort(list, ascending)
        builder.append("Comparable升序(文字小大写)：$list\n")
        Collections.sort(list, descending)
        builder.append("Comparable降序(大小写文字)：$list")
        return builder.toString()
    }

    class SortModel(var className: String?) : Comparable<SortModel> {
        override fun compareTo(another: SortModel): Int = className!!.compareTo(another.className!!)
        override fun toString(): String = "$className"
    }

    companion object {
        private val collator = Collator.getInstance()

        //升序
        private val ascending =
            Comparator<SortModel> { first, second ->
                collator.compare(
                    first.className,
                    second.className
                )
            }
        private val ascending1 =
            Comparator<SortModel> { first, second -> first.className!!.compareTo(second.className!!) }

        //降序
        private val descending =
            Comparator<SortModel> { first, second ->
                collator.compare(
                    second.className,
                    first.className
                )
            }
    }

    //获取电量百分比
    private val battery: Int
        get() {
            val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val currLevel = battery!!.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val total = battery.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            return currLevel * 100 / total
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
            )
            val cursor = contentResolver.query(data?.data!!, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                val numberIndex =
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                val name = cursor.getString(nameIndex);
                val number = cursor.getString(numberIndex);
                showToast(number)
                binding.tvDisplay.text = "姓名:$name;\n电话:$number"
            }
            cursor?.close()
        }
    }

    private fun equalHashCode(): String =
        "Aa的hashCode:${"Aa".hashCode()}==BB的hashCode:${"BB".hashCode()};\n" +
                "Aa的内存地址==${System.identityHashCode("Aa")}\n" +//内存地址
                "BB的内存地址==${System.identityHashCode("BB")}\n" +//内存地址
                "Bb的hashCode:${"Bb".hashCode()}==CC的hashCode:${"CC".hashCode()};\n" +
                "Bb的内存地址==${System.identityHashCode("Bb")}\n" +//内存地址
                "CC的内存地址==${System.identityHashCode("CC")}\n" +//内存地址
                "Cc的hashCode:${"Cc".hashCode()}==DD的hashCode:${"DD".hashCode()};\n" +
                "Cc的内存地址==${System.identityHashCode("Cc")}\n" +//内存地址
                "DD的内存地址==${System.identityHashCode("DD")}\n" +//内存地址
                "字符串的hashcode是重写过的"


    /**
     * URL的结构
     */
    private fun getURL(): String {
        val builder = StringBuilder()
        val url = URL(HttpUrl.URL_MUKE + HttpUrl.URL_MUKE1)
        builder.append("资源名 ： ${url.file}\n")
        builder.append("主机名 ： ${url.host}\n")
        builder.append("路径 ： ${url.path}\n")
        builder.append("端口 ： ${url.port}\n")
        builder.append("协议名称 ： ${url.protocol}\n")
        builder.append("查询字符串 ： ${url.query}\n")
        return builder.toString()
    }

    private fun ttsTest() {
        tts = TextToSpeech(this, TextToSpeech.OnInitListener { status -> checkTTS(status) })
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak("gesture", TextToSpeech.QUEUE_ADD, null, "speech")
            //                tts?.synthesizeToFile("gesture",null,File(""),"record")
        }
    }

    var tts: TextToSpeech? = null
    private fun checkTTS(status: Int) {

    }

    /**
     * 文件属性
     */
    private fun fileTest() {

    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
    }

}
