package com.sky.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.sky.base.api.IView;
import com.sky.base.utils.SPUtils;
import com.sky.base.utils.ToastUtils;
import com.sky.base.widget.DialogLoading;


/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 6:43 下午
 * @Description:MVC 模式下fragment 的基类，其中的 V 应用 viewbinding
 */
public abstract class BaseFragment<V extends ViewBinding> extends Fragment implements IView {
    //    onAttach() -> onCreate -> onCreateView -> onViewCreate -> onActivityCreated -> onStart -> onResume -> onPause -> onStop -> onDestoryView -> onDestory -> onDetach
    protected AppCompatActivity activity;
    protected V binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(getLayoutId(), container, false);
//        return view != null ? view : super.onCreateView(inflater, container, savedInstanceState);
        binding = getBinding(inflater, container);
        return binding.getRoot();
    }

    protected abstract V getBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @Override
    public void setCenterTitle(TextView tv, String title) {
    }

    @Override
    public void showToast(int resId) {
        ToastUtils.showLong(activity, resId);
    }

    @Override
    public void showToast(@NonNull String text) {
        ToastUtils.showLong(activity, text);
    }

    @Override
    public <T> T getObject(String text, T value) {
        return (T) SPUtils.getInstance().get(text, value);
    }

    @Override
    public <T> void setObject(String text, T value) {
        SPUtils.getInstance().put(text, value);
    }

    @Override
    public void showLoading() {
        DialogLoading.showDialog(activity);
    }

    @Override
    public void disLoading() {
        DialogLoading.disDialog();
    }
}
