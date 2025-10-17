package com.sky.oa.entity

import java.io.Serializable

data class ActivityEntity(
    var activityName: String?,//activity的名称
    var describe: String?,//activity的描述
    var img: Int,//代表图片
    var componentName: String?//跳转所需
) : Serializable