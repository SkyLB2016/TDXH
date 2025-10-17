package com.sky.oa.repository

import android.content.res.AssetManager
import androidx.lifecycle.MutableLiveData
import com.sky.base.BaseApplication
import com.sky.base.utils.FileUtils
import com.sky.base.utils.LogUtils
import com.sky.oa.App
import com.sky.oa.entity.ChapterEntity
import com.sky.oa.entity.PoetryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Collator
import java.util.*

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/3/14 11:07 上午
 * @Version: 1.0
 */
class NotesRepository {
    /**
     * 判断路径是否为文件（尝试打开流成功即为文件）
     */
    private fun isFile(assetManager: AssetManager, path: String): Boolean {
        return try {
            assetManager.open(path).use { true }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 获取文件大小（通过读取流并计算长度）
     */
    private fun getFileSize(assetManager: AssetManager, filePath: String): Long {
        return try {
            assetManager.open(filePath).use { input ->
                input.readBytes().size.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * 递归扫描 assets 目录下所有文件（含子目录）
     */

    suspend fun getPoetries(
        assets: AssetManager,
        directory: String
    ): LinkedHashMap<String, ArrayList<PoetryEntity>> = withContext(Dispatchers.IO) {
//        根据目录名，缓存文件列表
        val note_maps: LinkedHashMap<String, ArrayList<PoetryEntity>> = linkedMapOf()

//        加入目录遍历列表，每次移除第一个
        val dirs: LinkedList<String> = LinkedList()
        dirs.add(directory)

        var dir: String
        //文件夹中返回的是个数组
        var fileNames: Array<String>? = null
        var poetry: PoetryEntity
        var poetries: ArrayList<PoetryEntity>? = arrayListOf()
        while (dirs.isNotEmpty()) {
            dir = dirs.removeFirst()
            poetries = note_maps[dir]
            //获取文件列表
            fileNames = assets.list(dir)
            //开始遍历
            fileNames?.forEach { fileName ->
                if (fileName.endsWith(".txt")) {
                    val title = fileName.substringBeforeLast('.', fileName)
//                    fileName.substring(0, fileName.length - 4)
//                    size = FileUtils.readCharFile(assets.open("$dir/$fileName")).length
                    val filePath = "$dir/$fileName"
                    val size = getFileSize(assets, filePath)

                    poetry = PoetryEntity(
                        name = title,
                        filePath = "$dir/$fileName",
                        parentDir = dir,
                        size = size
                    )

                    //当前目录尚未添加，初始化并添加到 map
                    if (poetries == null) {
                        poetries = arrayListOf()
                        note_maps[dir] = poetries
                    }

                    poetries.add(poetry)
                } else {
//                    文件夹加入遍历列表中
                    dirs.addLast("$dir/$fileName")
                }
            }
            poetries?.sortWith { o1, o2 ->
                o1?.size?.compareTo(o2.size)!!//升序排列
            }
        }
        LogUtils.i("")
        note_maps
    }
    suspend fun getArticles():List<PoetryEntity>{
        return withContext(Dispatchers.IO){
            val articles = mutableListOf<PoetryEntity>()
            var dir = "Documents"//assets初始路径
            var fileNames: Array<String>? = null//assets取出的目录名称
            val link = LinkedList<String>()
            link.add(dir)
            var item = PoetryEntity()
            //取出Documents下的所有文本文件
            while (link.isNotEmpty()) {
                dir = link.removeFirst()
                fileNames = App.context.assets.list(dir)
                fileNames?.forEach { it->
                    if (it.endsWith(".txt")) {
                        item = item.copy()
                        item.name = it.substring(0, it.length - 4)
                        item.filePath = "$dir/$it"
                        articles.add(item)
                    } else {
                        if (it != "other") {
                            link.add("$dir/$it")
                        }
                    }
                }
            }
            val collator = Collator.getInstance(Locale.CHINA)
            articles.sortWith(kotlin.Comparator { o1, o2 ->
                collator.compare(
                    o1.filePath,
                    o2.filePath
                )
            })
            articles
        }
    }
    suspend fun getChapter(fileName: String):MutableList<ChapterEntity> {
        return withContext(Dispatchers.IO) {
            getChapterList(FileUtils.readAssestToChar(BaseApplication.app, fileName))
        }
    }
    fun getChapterList(vararg params: String?): MutableList<ChapterEntity> {
        //文章分成多少行
        val list = params[0]!!.lines()
        //目录
        val catalogList: MutableList<ChapterEntity> = arrayListOf()
        //单一章节
        var chapter = ChapterEntity()
        chapter.chapter = list[0]//最开始的是文件名
        for (text in list) {
            //放在string.xml里的文档，开头会有个空格，需要先去除
//                val text = if (i.startsWith(" ")) i.substring(1) else i
            //是否是一章的开始
            if (text.startsWith("第") && text.contains("章")
                || text.startsWith("第") && text.contains("节")
                || text.startsWith("第") && text.contains("卦")
                || text.contains("篇") && text.contains("第")
                || text.startsWith("卷")
                || text.startsWith("【")
                || text.startsWith("●")
            ) {
                //进入此处代表又是一个新的章节开始，所以需要先保存上一章节的内容，同时也需要移除最后一个换行符
//                    if (chapter.content != null && chapter.content.isNotEmpty())
                chapter.content.setLength(chapter.content.length - 1)
                catalogList.add(chapter)

                //创建新的章节
                chapter = ChapterEntity()
                chapter.chapter = text//设置章节
            }
            //不是一章的开始就把内容保存在content中，因为是一行一行存的所以需要在末尾加入换行符号\n
            chapter.content.append("$text\n")//内容包含，章节名称与实际内容。
        }
        //如章节还是没有拆分且文章大于30行，则按10行一组拆分
//            var sign = -1
//            if (catalogList.isEmpty() && list.size > 30) {
//                chapter.content = StringBuilder()
//                for ((i,text) in list.withIndex()) {
////                    val text = if (list[i].startsWith(" ")) list[i].substring(1) else list[i]
//                    //以，作为正文起始处
//                    if (sign === -1 && text.contains("，")) sign = i % 10
//                    //记录章节
//                    if (i % 10 === sign) {
//                        //保存上一章节的内容
//                        chapter.content.setLength(chapter.content.length - 1)
//                        catalogList.add(chapter)
//                        //创建新的章节
//                        chapter = ChapterEntity()
//                        chapter.chapter = text
//                    }
//                    //保存内容
//                    chapter.content.append("$text\n")
//                }
//            }
        //保存最后一章的内容
        chapter.content.setLength(chapter.content.length - 1)
        catalogList.add(chapter)
        return catalogList
    }


}