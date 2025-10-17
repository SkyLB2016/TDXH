package com.sky.base.ui;


/**
 * @Author: 李彬
 * @CreateDate: 2021/8/10 3:32 下午
 * @Description: MVVM模式，带刷新功能的 vm
 */
public abstract class RefreshVM extends BaseViewModel {
    protected int page = 1;
    protected int totalCount = 0;

    public void getData() {
        page = 1;
    }

    public void onRefresh() {
        page = 1;
    }

}
