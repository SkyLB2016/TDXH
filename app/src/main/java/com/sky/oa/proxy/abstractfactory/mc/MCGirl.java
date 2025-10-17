package com.sky.oa.proxy.abstractfactory.mc;

import com.sky.base.utils.LogUtils;
import com.sky.oa.proxy.abstractfactory.api.Girl;

/**
 * 圣诞系列的女孩
 *
 * @author Administrator
 */
public class MCGirl implements Girl {
    @Override
    public void drawWomen() {
        LogUtils.i("-----------------圣诞系列的女孩子--------------------");
    }
}
