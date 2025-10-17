package com.sky.oa.utils.imageloader

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import com.sky.base.utils.BitmapUtils
import com.sky.base.utils.LogUtils
import java.util.*
import kotlin.math.log

/**
 * Created by SKY on 2015/12/16 13:45.
 * 图片预加载缓存类，async的实现
 */
object ImageLoaderAsync {
    private var handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val holder = msg.obj as ImageHolder
            imageCache.put(holder.path, holder.bitmap)

            if (holder.view!!.tag == holder.path) {
//                if (holder.view is ImageView) {
                val bitmap = holder.bitmap
                val view = holder.view as ImageView?
                //ViewGroup.LayoutParams params = view.getLayoutParams();
                ////params.width = bitmap.getWidth();
                //params.height = bitmap.getHeight();
                //view.setLayoutParams(params);
                view!!.setImageBitmap(bitmap)
//                } else {
//                    holder.view!!.background = BitmapUtils.getDrawableFromBitmap(holder.view!!.context, holder.bitmap)
//                }
            }
        }
    }

    private val tasks: MutableSet<ImageAsyncTask>?
    private val imageCache: ImageCache

    init {
        imageCache = DoubleCache()
        tasks = HashSet()
    }

    fun showAsyncImage(view: ImageView, path: String) {
        view.tag = path
        if (view ==null){
            LogUtils.i("dalflkajdjflakjdfjasdk")
        }
        //先从内存中查找是否含有图片
        if (imageCache.get(path) == null) {
            //开启异步任务加载图片
            val task = ImageAsyncTask(view)
            tasks?.add(task)
            task.execute(path)//多任务串行运行方式。
            //AsyncTaskCompat.executeParallel(task, path);//多任务并行方式
        } else {
            //发送message消息，view，path，bitmap
            sendMessage(view, path, imageCache.get(path))
        }
    }

    fun cancelAlltasks() {
        if (tasks == null || tasks.isEmpty()) return
        //标记为cancel状态，并没有取消，如要取消，在doInBackground中调用isCancelled（）
        for (task in tasks) task.cancel(true)
    }

    /**
     * 取得图片后通知UIHandler更新
     *
     * @param view
     * @param path
     * @param bitmap
     */
    private fun sendMessage(view: View, path: String, bitmap: Bitmap) {
        //发送message消息，view，path，bitmap
        val holder = ImageHolder()
        holder.bitmap = bitmap
        holder.view = view
        holder.path = path
        val msg = Message.obtain()
        msg.obj = holder
        //msg.sendToTarget();
        handler.sendMessage(msg)
    }

    private class ImageAsyncTask(internal var view: View) : AsyncTask<String, Void, Bitmap>() {
        internal var url: String = ""
        override fun doInBackground(vararg params: String): Bitmap? {
            if (isCancelled) return null
            url = params[0]
            //publishProgress();
            LogUtils.i("图片缓存==" + params[0])
            return BitmapUtils.getBitmapUP(params[0])
        }

        override fun onPostExecute(bitmap: Bitmap) {
            super.onPostExecute(bitmap)
            sendMessage(view, url, bitmap)
            tasks?.remove(this)
        }
    }

    private class ImageHolder {
        internal var view: View? = null
        internal var bitmap: Bitmap? = null
        internal var path: String? = null
    }
}