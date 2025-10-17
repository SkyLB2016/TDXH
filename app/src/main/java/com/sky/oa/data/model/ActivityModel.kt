package com.sky.oa.entity

import com.sky.oa.gson.GsonUtils
import java.io.Serializable

/**
 * Created by SKY on 15/12/9 下午8:54.
 * activity信息类
 */
//@JsonAdapter(ActivityDeserial::class)
class ActivityModel(
    var activityName: String?//activity的名称
    , var describe: String?//activity的描述
    , var img: Int//代表图片
    , var componentName: String?//跳转所需
) : Serializable, Comparable<ActivityModel>, Cloneable {
    companion object {
        private const val serialVersionUID = -6504989616188082278L//保证增加属性后，依然可以读取之前的属性
    }

    //    @Transient
//    val objList: List<ActivityModel>? = null;
    var version: Double = 0.0

    /**
     *  排序
     */
    override fun compareTo(model: ActivityModel): Int = activityName!!.compareTo(model.activityName!!)

    override fun toString(): String {
//        return "{\"activityName\":\"$activityName\",\"describe\":\"$describe\",\"img\":$img,\"componentName\":\"$componentName\"}"
        return GsonUtils.toJson(this)
    }

    override fun clone(): Any {
        val activity = super.clone() as ActivityModel
        activity.activityName = this.activityName
        activity.describe = this.describe
        activity.img = this.img
        activity.componentName = this.componentName
        return activity
    }
}
