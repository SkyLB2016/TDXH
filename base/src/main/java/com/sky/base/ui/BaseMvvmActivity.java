package com.sky.base.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewbinding.ViewBinding;

import com.sky.base.utils.SPUtils;
import com.sky.base.utils.ToastUtils;
import com.sky.base.api.IView;
import com.sky.base.widget.DialogLoading;
/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 6:43 下午
 * @Description:MVVM 模式下 activity 的基类，其中的 V 应用 viewbinding
 *              java 版本
 */
public abstract class BaseMvvmActivity<V extends ViewBinding, VM extends BaseViewModel> extends AppCompatActivity implements IView {
    public V binding;
    public VM viewModel;

    protected abstract V inflateBinding();

    protected abstract VM createViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding();
        setContentView(binding.getRoot());
        viewModel = createViewModel();
    }

    public void setToolbar(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle(title);
        setTitle(title);
    }

    public void showNavigationIcon() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//就这一个也起作用，需要与 onOptionsItemSelected 配合使用
        getSupportActionBar().setHomeButtonEnabled(true);//必须与搭配第一个使用，不用这个，也行，目前没发现他的作用
        //getSupportActionBar().setDisplayShowHomeEnabled(true);//没啥用
    }

    @Override
    public void setCenterTitle(TextView tv, String title) {
    }

    @Override
    public void showToast(@StringRes int resId) {
        ToastUtils.showLong(this, resId);
    }

    @Override
    public void showToast(@NonNull String text) {
        ToastUtils.showLong(this, text);
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
        DialogLoading.showDialog(this);
    }

    @Override
    public void disLoading() {
        DialogLoading.disDialog();
    }

//    @Override
//    public void showContent() {
//    }
//
//    @Override
//    public void onRefreshEmpty() {
//    }
//
//    @Override
//    public void onRefreshFailure(String msg) {
//    }

    //Activity 自带的 menu 监听事件，toolbar不设置监听，默认使用的也是这个。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
