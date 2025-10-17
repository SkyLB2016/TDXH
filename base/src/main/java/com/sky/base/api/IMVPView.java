package com.sky.base.api;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * @Description: mvp的view基类
 * @Author: 李彬
 * @CreateDate: 2021/8/10 2:18 下午
 * @Version: 1.0
 */
public interface IMVPView {

    void setCenterTitle(@NonNull String title);

    void setRightTitle(@NonNull String title);

    void showToast(@StringRes int resId);

    void showToast(@NonNull String text);

    void showLoading();

    void disLoading();

//    void finish();
}
