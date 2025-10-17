package com.sky.oa.proxy;

import com.sky.base.utils.LogUtils;

/**
 * Created by libin on 2020/04/18 5:35 PM Saturday.
 */
public class Cuthair implements Hair {
    @Override
    public String cutHait(int money) {
        LogUtils.i("剪个头发"+money+"元钱啊:");
//        System.out.println("剪个头发"+money+"元钱啊:");
        return "剪个头发" + money + "元钱啊:";

    }
}
