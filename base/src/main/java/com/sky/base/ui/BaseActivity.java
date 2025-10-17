package com.sky.base.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.sky.base.api.IView;
import com.sky.base.utils.SPUtils;
import com.sky.base.utils.ToastUtils;
import com.sky.base.widget.DialogLoading;


/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 3:30 下午
 * @Description: MVC模式下 activity 的基类，其中的 v 应用 viewbingding
 */
public abstract class BaseActivity<V extends ViewBinding> extends AppCompatActivity implements IView {
    protected V binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = inflateBinding();
        setContentView(binding.getRoot());
        // 2. 初始化视图组件
        initViews();
        // 3. 设置观察者（用于监听 ViewModel 的状态变化）
        setObservers();
        // 4. 加载初始数据
        loadData();
    }

    protected abstract V inflateBinding();
    /**
     * 初始化视图组件的方法，子类可重写
     */
    protected  void initViews() {}

    /**
     * 设置观察者的方法，子类可重写
     * 用于监听 ViewModel 中 StateFlow 的状态变化
     */
    protected  void setObservers() {}

    /**
     * 加载数据的方法，子类可重写
     * 用于在页面创建时触发数据加载
     */
    protected  void loadData() {}

    public void showNavigationIcon() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
//            显示左侧图标
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//就这一个也起作用，需要与 onOptionsItemSelected 配合使用
            getSupportActionBar().setHomeButtonEnabled(true);//必须与搭配第一个使用，不用这个，也行，目前没发现他的作用
            //getSupportActionBar().setDisplayShowHomeEnabled(true);//没啥用
        }
    }

    @Override
    public void setCenterTitle(TextView tv, @NonNull String title) {

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
//                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
