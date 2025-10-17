package com.sky.base.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.sky.base.utils.ToastUtils;
import com.sky.base.api.IMVPView;
import com.sky.base.widget.DialogLoading;

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 2:17 下午
 * @Description: MVP 模式下 activity 的基类
 */
public abstract class BasePActivity<P extends BasePresenter> extends AppCompatActivity implements IMVPView {
    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (presenter == null) presenter = creatPresenter();
        initialize(savedInstanceState);//需要初始化的成员变量
        presenter.onCreate(savedInstanceState);//请求数据
    }


    /**
     * 获取布局的resId
     */
    @CheckResult
    protected abstract int getLayoutId();

    protected abstract P creatPresenter();

    @Override
    public void setCenterTitle(@NonNull String title) {

    }

    @Override
    public void setRightTitle(@NonNull String title) {

    }

    protected abstract void initialize(@Nullable Bundle savedInstanceState);

    @Override
    public void showToast(@StringRes int resId) {
        ToastUtils.showShort(this, resId);
    }

    @Override
    public void showToast(String text) {
        ToastUtils.showShort(this, text);
    }

    @Override
    public void showLoading() {
        DialogLoading.showDialog(this);
    }

    @Override
    public void disLoading() {
        DialogLoading.disDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

}
