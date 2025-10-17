package com.sky.oa.vm;

import android.Manifest;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.sky.base.ui.BaseViewModel;
import com.sky.base.utils.FileUtils;
import com.sky.base.utils.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 5:44 下午
 * @Description:
 */
public class MainViewModel extends BaseViewModel {
    public void TestMethod(Context context) {
        collectionsTest();
//        hashCollision();
//        LogUtils.i(context.getFilesDir().getAbsolutePath());
//        FileUtils.serialize(context.getFilesDir().getAbsolutePath() + "/data", list);
//        FileUtils.serialize(context.getFilesDir().getAbsolutePath() + "/data1", new KeyValue("1", "2"));
//        字符串操作
}

private void mathTest() {
    LogUtils.i("abs()：绝对值。==" + Math.abs(-19));
    LogUtils.i("pow(a,n)：a的n次方幂。==" + Math.pow(2, 4));
    LogUtils.i("ceil()：向上取整==" + Math.ceil(10.7));
    LogUtils.i("floor()：向下取整==" + Math.floor(10.7d));
    LogUtils.i("round()：四舍五入。==" + Math.round(10.6));
    LogUtils.i("random()：随机数。==" + Math.random());
    LogUtils.i("min()：两个数的最小值。==" + Math.min(10, 11));
    LogUtils.i("max()：两个数的最大值。==" + Math.max(10, 11));
    LogUtils.i("sqrt()：求平方根。==" + Math.sqrt(16));
    LogUtils.i("sin()：正弦值。==" + Math.sin(30));
    LogUtils.i("cos()：余弦值。==" + Math.cos(30));
    LogUtils.i("tan()：正切值。==" + Math.tan(30));
    LogUtils.i("rint()：取最近的整数。他妈的，变相的四舍五入，但是10.5会舍弃。==" + Math.rint(10.51));
    LogUtils.i("exp()：以自然数底数e的n次方。==" + Math.exp(2));
    LogUtils.i("log()：以e为底数的对数值。==" + Math.log(100));
    LogUtils.i("log10()：以10为底的对数值。==" + Math.log10(1000));
    LogUtils.i("asin()：反正弦值。==" + Math.asin(30));
    LogUtils.i("acos()：反余弦值。==" + Math.acos(30));
    LogUtils.i("atan()：反正切值。==" + Math.atan(30));
    LogUtils.i("atan2()：将笛卡尔坐标转换为极坐标，并返回极坐标的角度值。==" + Math.atan2(30, 30));
    LogUtils.i("toDegrees()：将参数转化为角度。==" + Math.toDegrees(1));
    LogUtils.i("toRadians()：将角度转换为弧度。==" + Math.toRadians(30));
}

//hash 碰撞的算法
private void hashCollision() {
    int a = 55;     //110111
    int b = 16;     //010000
    int c = 16 - 1; //001111
    int d = 63;     //111111
    LogUtils.i("a==" + Integer.toBinaryString(a));
    LogUtils.i("b==" + Integer.toBinaryString(b));
    LogUtils.i("c==" + Integer.toBinaryString(c));
    LogUtils.i("d==" + Integer.toBinaryString(d));
    LogUtils.i("a & b==" + (a & b));//16
    LogUtils.i("a % b==" + (a % b));//7
    LogUtils.i("a & c==" + (a & c));//7
    LogUtils.i("a % c==" + (a % c));//10
    LogUtils.i("d & c==" + (d & c));//15
}

private void collectionsTest() {
    List<String> list = new ArrayList<>(Arrays.asList(
            "1", "q", "w", "e", "r", "t", "y", "u", "i", "d", "c", "v"
    ));
    LogUtils.i("Collections 排序方法测试");
    LogUtils.i("list原顺序==" + list);
    LogUtils.i("String 已实现 Comparable接口");
    Collections.sort(list);
    LogUtils.i("list的sort排序，默认是升序==" + list);
    Collections.reverse(list);
    LogUtils.i("list的reverse 翻转排序==" + list);
    Collections.shuffle(list);
    LogUtils.i("list的shuffle 随机排序==" + list);
//        Collections.sort(list, String::compareTo);//我靠，简单是够简单了，但这他妈谁看的懂啊。
    Collections.sort(list, (o1, o2) -> {
        return o1.compareTo(o2);
    });
    LogUtils.i("sort 的 comparator 中 o1.compareTo(o2) 排序是升序==" + list);
    Collections.sort(list, (o1, o2) -> o2.compareTo(o1));
    LogUtils.i("sort 的 comparator 中 o2.compareTo(o1) 排序是降序==" + list);
    Collections.sort(list, (o1, o2) -> Collator.getInstance().compare(o1, o2));
    LogUtils.i("sort 的 comparator 中用 Collator 中的compare(o1,o2) 排序是升序==" + list);
    Collections.sort(list, (o1, o2) -> Collator.getInstance().compare(o2, o1));
    LogUtils.i("sort 的 comparator 中用 Collator 中的compare(o2,o1) 排序是降序==" + list);

}

public void outPutException(Exception e) {
    Writer writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    e.printStackTrace(printWriter);
    Throwable cause = e.getCause();
    while (cause != null) {
        cause.printStackTrace(printWriter);
        cause = cause.getCause();
    }
    printWriter.close();
    String result = writer.toString();
    LogUtils.i("mqtt+" + result);
}

}