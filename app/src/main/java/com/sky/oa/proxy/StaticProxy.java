package com.sky.oa.proxy;

import com.sky.base.utils.LogUtils;

/**
 * Created by libin on 2020/04/18 5:25 PM Saturday.
 */
public class StaticProxy implements Hair {
    private Hair hair;

    public StaticProxy(Hair hair) {
        this.hair = hair;
    }

    public Hair getHair() {
        return hair;
    }

    public void setHair(Hair hair) {
        this.hair = hair;
    }

    @Override
    public String cutHait(int money) {
        preSaleService();
        String text = hair.cutHait(money);
        afterSaleService();
        return text;
    }

    public void preSaleService() {
        LogUtils.i("精美包装，完整服务");
    }

    public void afterSaleService() {
        LogUtils.i("根据需求完成您的服务");
    }
}
