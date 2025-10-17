package com.sky.oa.vm

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import com.sky.base.ui.BaseViewModel
import com.sky.base.utils.LogUtils
import com.sky.oa.R
import com.sky.oa.entity.ActivityEntity
import java.text.Collator
import java.util.*
import kotlin.Comparator

class HomeViewModel : BaseViewModel() {
    val activitiesData = MutableLiveData<MutableList<ActivityEntity>>()

    /**
     * @return 从manifest中获取activity的信息
     */
    fun getActivities(context: Context) {
        val activityInfos = ArrayList<ActivityEntity>()
        //Intent mainIntent = new Intent(Intent.ACTION_MAIN);//获取action为ACTION_MAIN的activity
        //mainIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE);//筛选category为sample code的act
        //mainIntent.setPackage(getPackageName());//只选出自己应用的act
        val mainIntent = Intent("com.sky.oa") //自定义的action
//            mainIntent.action="com.sky.coustom"
//            mainIntent.addCategory(Intent.CATEGORY_CAR_MODE)
//            mainIntent.addCategory(Intent.CATEGORY_APP_MUSIC)
//            mainIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK
//            mainIntent.flags=Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
        val manager = context.packageManager
        val resolveInfos = manager!!.queryIntentActivities(mainIntent, PackageManager.MATCH_ALL)
//            val resolveInfos1 = manager?.resolveActivity(mainIntent, 0) ?: return activityInfos

        for (i in resolveInfos.indices) {
            val info = resolveInfos[i]
            //获取label,activity中未设置的话返回程序名称
            val labelSeq = info.loadLabel(manager)
            val label = labelSeq?.toString() ?: info.activityInfo.name
            //获取说明
            val descriptionRes = info.activityInfo.descriptionRes
            val describe = if (descriptionRes == 0) "未添加" else context.getString(descriptionRes)
            //获取icon  Drawable icon = info.loadIcon(pm);
            val iconRes = info.activityInfo.icon
            val icon = if (iconRes == 0) R.mipmap.ic_launcher else iconRes
            activityInfos.add(ActivityEntity(label, describe, icon, info.activityInfo.name))
            LogUtils.i("${info.activityInfo.name}")
        }
        //排序
        Collections.sort(activityInfos, comparator)
//            Collections.sort(activityInfos);//使用activityModel中的compareTo进行排序，java中使用的
//            activityInfos.sort();//使用activityModel中的compareTo进行排序，kotlin中使用的

        //去掉前边的数字。
//            var iterator = activityInfos.iterator()
//            while (iterator.hasNext())
//                val activityInfo = iterator.next()
//                var name = activityInfo.activityName!!
//                val index = name.indexOfFirst { char -> char == '.' }
//                name = name.substring(index + 1, name.length)
//                activityInfo.activityName = name
//            }
//        SPUtils.getInstance().getValue(activityInfos.get(0))
        activitiesData.value = activityInfos
    }

    private val comparator = Comparator<ActivityEntity> { first, second ->
        Collator.getInstance().compare(first.activityName, second.activityName)
    }
    private val sort =
        Comparator<Double> { first, second -> if (first > second) 1 else if (first < second) -1 else 0 }
}

