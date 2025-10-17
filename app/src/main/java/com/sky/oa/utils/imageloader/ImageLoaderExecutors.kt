package com.sky.oa.utils.imageloader

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.sky.base.utils.BitmapUtils
import com.sky.base.utils.LogUtils
import com.sky.base.utils.SDCardUtils
import com.sky.oa.R
import com.sky.oa.widget.ZoomImageView
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore

/**
 * Created by SKY on 2015/1/6 17:21.
 * 线程池的使用
 */
class ImageLoaderExecutors @JvmOverloads constructor(type: FileType = FileType.LIFO) {

    private val imageCache: MemoryCache//内存缓存
    private var type = FileType.LIFO//任务执行方式
    private val runnables: LinkedList<Runnable>
    //UI主线程handler
    private val handlerUI = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0X01 -> {
                    val imageHolder = msg.obj as ImageHolder
                    if (imageHolder.view?.tag == imageHolder.path)
                        if (imageHolder.view is ImageView) {
                            val bitmap = imageHolder.bitmap
                            val view = imageHolder.view as ImageView?
//                            run {
//                                val params = view?.layoutParams
//                                params.width = bitmap?.width
//                                params.height = bitmap.height
//                                view.layoutParams = params
//                            }
                            view?.setImageBitmap(bitmap)
                        } else {
                            imageHolder.view?.background = BitmapUtils.getDrawableFromBitmap(imageHolder.view?.context, imageHolder.bitmap)
                        }
                }
            }
        }
    }
    //异步线程handler，检查runnable使用
    private var threadHandler: Handler? = null
    //线程池
    private var executors: ExecutorService? = null
    //异步线程信号量,主要用于FileType.LIFO模式
    private val threadSemaphore: Semaphore
    //因为异步线程与主线程是并行的，所以为防止threadHandler未创建，用信号量做一个限制
    private val semaphore = Semaphore(0)

    init {
        this.type = type
        runnables = LinkedList()
        imageCache = MemoryCache()
        //执行多少个线程
        val count=Runtime.getRuntime().availableProcessors()
        executors = Executors.newFixedThreadPool(count)
        threadSemaphore = Semaphore(count)
        object : Thread() {
            override fun run() {
                super.run()
                Looper.prepare()
                threadHandler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        when (msg.what) {
                            THREADH_CODE -> {
                                //获取一个任务，并执行
                                executors?.execute(runnable)
                                try {
                                    //同时请求一个信号量
                                    threadSemaphore.acquire()
                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                }

                            }
                        }
                    }
                }
                //threadHandler已创建，释放semaphore
                semaphore.release()
                Looper.loop()//开始轮询接收消息
            }
        }.start()
    }

    /**
     * 获取一个Runnable任务
     *
     * @return
     */
    private val runnable: Runnable
        get() {
            if (type == FileType.FIFO)
                return runnables.removeFirst()
            else if (type == FileType.LIFO)
                return runnables.removeLast()
            throw NullPointerException("runnables获取任务失败")
        }


    /**
     * 外部调用方法，为view添加图片
     *
     * @param view
     * @param path
     */
    fun loadImage(view: View, path: String) {
        view.tag = path
        //先从内存中查找是否含有图片
        if (imageCache.get(path) == null) {
            if (view is ImageView) {
                if (view !is ZoomImageView)
                    view.setImageBitmap(BitmapUtils.getBitmapFromId(view.getContext(), R.mipmap.ic_launcher))
            } else
                view.setBackgroundResource(R.mipmap.ic_launcher)
            //开启异步任务加载图片
            createRunnable(Runnable {
                //从网络或本地获取图片
                val size = getViewSize(view)
                val bitmap = getBitmap(path, size)

                //添加到缓存中
                imageCache.put(path, bitmap)
                //发送message消息，view，path，bitmap
                sendMessage(view, path, bitmap)
                //释放异步线程中使用的信号量
                threadSemaphore.release()
                LogUtils.i(threadSemaphore.availablePermits().toString() + "个")
            })
        } else {
            val bitmap = imageCache.get(path)
            //发送message消息，view，path，bitmap
            sendMessage(view, path, bitmap)
            //不为空直接可以加载图片，但传递给UIhandler来加载为好，统一规则
            //if (view instanceof ImageView) ((ImageView) view).setImageBitmap(bitmap);
        }
    }

    /**
     * 取得图片后通知UIHandler更新
     *
     * @param view
     * @param path
     * @param bitmap
     */
    private fun sendMessage(view: View, path: String, bitmap: Bitmap?) {
        val holder = ImageHolder()
        holder.bitmap = bitmap
        holder.view = view
        holder.path = path
        val msg = handlerUI.obtainMessage()
        //        Message msg = Message.obtain();
        //        Message msg = Message.obtain(handlerUI);
        msg.obj = holder
        msg.what = UIH_CODE
        msg.sendToTarget()
        //        handlerUI.sendMessage(msg);
    }

    /**
     * 获取图片
     *
     * @param path
     * @param size
     * @return
     */
    private fun getBitmap(path: String, size: ImageSize): Bitmap? {
        if (path.startsWith("http"))
            return BitmapUtils.scaleBitmap(BitmapUtils.getBitmapFromUrl(path), size.width, size.height)
        else if (path.startsWith(SDCardUtils.getSDCardPath())) {
            return BitmapUtils.getBitmapFromPath(path, size.width, size.height)
        }
        throw NullPointerException("图片的路径应该是全路径")
    }

    private fun createRunnable(runnable: Runnable) {
        try {
            //检查threadHandler是否为空，在thread创建完成后释放
            if (threadHandler == null)
                semaphore.acquire()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        runnables.add(runnable)
        //添加完成后发送handler，轮询添加到线程池中
        threadHandler?.sendEmptyMessage(THREADH_CODE)
    }



    /**
     * 根据View获得适当的压缩的宽和高
     *
     * @param view
     * @return
     */
    private fun getViewSize(view: View): ImageSize {
        val imageSize = ImageSize()
        val displayMetrics = view.context.resources.displayMetrics
        val params = view.layoutParams
        var width = 0
        if (params != null)
            width = if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT)
                0
            else
                view.width // Get actual image width
        if (width <= 0 && params != null)
            width = params.width // Get layout width parameter
        // maxWidth
        if (width <= 0)
            width = getViewFieldValue(view, "mMaxWidth") // Check
        if (width <= 0)
            width = view.minimumWidth // Check// parameter
        if (width <= 0)
            width = displayMetrics.widthPixels
        var height = 0
        if (params != null)
            height = if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT)
                0
            else
                view.height // Get actual image height
        if (height <= 0 && params != null)
            height = params.height // Get layout height parameter
        // maxHeight
        if (height <= 0)
            height = getViewFieldValue(view, "mMaxHeight") // Check
        // minHeight
        if (height <= 0)
            height = view.minimumHeight // Check
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels
        imageSize.width = width
        imageSize.height = height
//        LogUtils.i("width==$width")
//        LogUtils.i("height==$height")
        return imageSize

    }

    fun closeExecutors() {
        executors?.shutdown()//结束空闲的线程interrupt
        //        executors.shutdownNow();//中断部分未在执行的线程
        executors = null
    }

    private inner class ImageHolder {
        internal var bitmap: Bitmap? = null
        internal var view: View? = null
        internal var path: String? = null
    }

    private inner class ImageSize {
        internal var width: Int = 0
        internal var height: Int = 0
    }

    enum class FileType {
        FIFO, //==LILO,先进先出，后进后出
        LIFO//==FILO，后进先出，先进后出
    }

    companion object {
        private const val UIH_CODE = 0X01//UI线程用
        private const val THREADH_CODE = 0X02//异步线程用

        /**
         * 反射获得View设置的最大宽度和高度
         *
         * @param object
         * @param fieldName
         * @return
         */
        private fun getViewFieldValue(`object`: Any, fieldName: String): Int {
            var value = 0
            try {
                if (`object` is ImageView) {
                    val field = ImageView::class.java.getDeclaredField(fieldName)
                    field.isAccessible = true
                    val fieldValue = field.get(`object`) as Int
                    if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                        value = fieldValue
                        LogUtils.i(value.toString() + "")
                    }
                }
            } catch (e: Exception) {
            }
            return value
        }
    }
}
