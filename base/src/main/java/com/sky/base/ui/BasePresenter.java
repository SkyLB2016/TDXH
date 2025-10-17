package com.sky.base.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.sky.base.R;
import com.sky.base.api.IMVPView;
import com.sky.base.api.IPresenter;
import com.sky.base.utils.NetworkUtils;
import com.sky.base.utils.SPUtils;

/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 3:32 下午
 * @Description: MVP 模式 P 的基类
 */
public class BasePresenter<V extends IMVPView> implements IPresenter {
    protected Context context;
    protected V mView;

    public BasePresenter(Context context) {
        this(context, (V) context);
    }

    //fragment时直接使用此构造器
    public BasePresenter(Context context, V view) {
        this.context = context;
        mView = view;
        setRxBus();
    }

    private void setRxBus() {
        if (!hasInternetConnected()) mView.showToast(R.string.toast_is_net);
    }

    public void onActivityCreated(Bundle savedInstanceState) {

    }

    public void onCreate(Bundle savedInstanceState) {
        loadData();
    }

    public void onCreateView(Bundle savedInstanceState) {
    }

    public void onViewCreate(Bundle savedInstanceState) {
        loadData();
    }

    public void onStart() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onStop() {
    }

    public void onDestroyView() {
    }

    public void onDestroy() {
//        RxBus.getInstance().unregister(this);
        recycle();
    }

    public void onDetach() {

    }

    public void loadData() {
    }

    @Override
    public Bundle getExtras() {
        return ((AppCompatActivity) context).getIntent().getExtras();
    }

    @Override
    public void sendEvent(int code) {
//        RxBus.getInstance().send(code);
    }

    @Override
    public <T> void sendEvent(int code, T event) {
//        RxBus.getInstance().send(code, event);
    }

//    @Override
//    public void onReceiveEvent(DefaultBus event) {
//
//    }

    @Override
    public <T> T getObject(String text, T value) {
        return (T) SPUtils.getInstance().get(text, value);
    }

    @Override
    public <T> void setObject(String text, T value) {
        SPUtils.getInstance().put(text, value);
    }

    @Override
    public boolean getUsertOnline() {
        return !TextUtils.isEmpty(getToken());
    }

    @Override
    public String getToken() {
        return getObject("TOKEN", "");
    }

    @Override
    public boolean hasInternetConnected() {
        return NetworkUtils.isConnected(context);
    }

    @Override
    public String getStringArray(int array, int position) {
        return context.getResources().getStringArray(array)[position];
    }

    @Override
    public String getString(@StringRes int resId) {
        return context.getString(resId);
    }

    @Override
    public void recycle() {

    }
}
