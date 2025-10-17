package com.sky.base.api;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Created by libin on 2020/05/09 2:04 PM Saturday.
 */
public interface IView {
    void setCenterTitle(TextView tv, @NonNull String title);

    void showToast(@StringRes int resId);

    void showToast(@NonNull String text);

    /**
     * @param key
     * @param value 获取不到数据时默认的value
     * @param <T>   对应的类型
     * @return 获取对应的字段数据
     */
    <T extends Object> T getObject(String key, T value);

    /**
     * @param key
     * @param value 需要写入数据
     * @param <T>   对应的何种类型
     */
    <T extends Object> void setObject(String key, T value);

    void showLoading();

    void disLoading();
}
