package com.sky.base.utils;

import android.text.TextUtils;
import android.widget.TextView;

import com.sky.base.utils.ToastUtils;
import com.sky.base.utils.uu.ActivityLifecycle;

import java.text.DecimalFormat;

/**
 * Created by SKY on 16/5/10 下午3:50.
 */
public class StringUtils {
    /**
     * @param text 控件
     * @return 获取控件中的内容
     */
    public static String getText(TextView text) {
        if (text == null) return "";
        return text.getText().toString().trim();
    }

    /**
     * @param text  需要判断的文本
     * @param toast 提示内容
     * @return 空为true
     */
    public static boolean notNull(String text, String toast) {
        if (!TextUtils.isEmpty(text)) return false;
        ToastUtils.showShort(ActivityLifecycle.getInstance().getCurrent(), toast);
        return true;
    }

    public static boolean notNullObj(Object obj, String toast) {
        if (null != obj) return false;
        ToastUtils.showShort(ActivityLifecycle.getInstance().getCurrent(), toast);
        return true;
    }

    public static String keepTwoDecimalPlaces(String num) {
        if (TextUtils.isEmpty(num)) return "";
        return keepTwoDecimalPlaces(Double.parseDouble(num));
    }

    public static String keepTwoDecimalPlaces(Double num) {
        //new BigDecimal(dou).setScale(2, BigDecimal.ROUND_UP));
        return format(num, "#0.00");
    }

    /**
     * @param num
     * @param type #与0以及小数点三者自由组合，举例：
     *             两位小数： #0.00；
     *             整数：    #0；
     *             科学技术： #,###.00"
     * @return
     */
    public static String format(double num, String type) {
        return new DecimalFormat(type).format(num);
    }

    /**
     * 清除HTML
     *
     * @param content 包含html字符串
     * @return 纯字符串
     */
    public static String stripHtml(String content) {
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "");
        // 去掉其它的<>之间的东西
        content = content.replaceAll("\\<.*?>", "");
        content = content.replaceAll("\r\n", "");
        content = content.replaceAll("&nbsp;", "");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        return content;
    }
}
