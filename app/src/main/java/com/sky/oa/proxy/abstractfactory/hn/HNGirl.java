package com.sky.oa.proxy.abstractfactory.hn;

import com.sky.base.utils.LogUtils;
import com.sky.oa.proxy.abstractfactory.api.Girl;

/**
 * 新年系列的女孩子
 *
 * @author Administrator
 */
public class HNGirl implements Girl {

    @Override
    public void drawWomen() {
        LogUtils.i("-----------------新年系列的女孩子--------------------");
    }
}