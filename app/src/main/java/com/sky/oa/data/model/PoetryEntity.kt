package com.sky.oa.entity

import java.io.Serializable

/**
 *
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2021/8/12 1:47 下午
 * @Version: 1.0
 */
data class PoetryEntity(
    var name: String = "",      // 文件名
    var format: String = ".txt",// 格式
    var filePath: String = "",  // 路径
    var parentDir: String = "", // 父路径
    var size: Long = 0          // 大小
) : Serializable, Cloneable


//data class PoetryEntity(
//    val name: String,
//    val format: String = ".txt",
//    val path: String,
//    val parentDir: String,
//    val total: Int = 0
//) : Serializable, Comparable<PoetryEntity> {
//    override fun toString(): String {
//        return "poetyr==$total"
//    }
//
//    override fun compareTo(other: PoetryEntity): Int {
//        LogUtils.i("${total}-${other.total}==${total?.compareTo(other!!.total)!!}")
////                LogUtils.i("flag2==${other?.total?.compareTo(o1!!.total)!!}")
////        return total?.compareTo(other!!.total)!!
//        return other!!.total?.compareTo(total)!!
//    }
//}