package com.sky.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.sky.base.api.IMVPView;
import com.sky.base.widget.DialogLoading;

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 6:43 下午
 * @Description: MVP 模式的 fragment 基类
 */
public abstract class BasePFragment<P extends BasePresenter> extends Fragment implements IMVPView {
//    onAttach() -> onCreate -> onCreateView -> onViewCreate -> onActivityCreated -> onStart -> onResume -> onPause -> onStop -> onDestoryView -> onDestory -> onDetach

    protected AppCompatActivity activity;
    protected P p;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (p == null) p = createP();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        return view != null ? view : super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        p.recycle();
    }

    protected abstract P createP();

    protected abstract int getLayoutId();

    @Override
    public void setCenterTitle(@NonNull String title) {

    }

    @Override
    public void setRightTitle(@NonNull String title) {

    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(activity, resId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(@NonNull String text) {
        Toast.makeText(activity, text, Toast.LENGTH_LONG).show();
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
