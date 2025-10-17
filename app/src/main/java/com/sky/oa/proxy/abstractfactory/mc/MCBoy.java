package com.sky.oa.proxy.abstractfactory.mc;

import com.sky.base.utils.LogUtils;
import com.sky.oa.proxy.abstractfactory.api.Boy;

/**
 * 圣诞系列的男孩子
 *
 * @author Administrator
 */
public class MCBoy implements Boy {

    @Override
    public void drawMan() {
        LogUtils.i("-----------------圣诞系列的男孩子--------------------");
    }
}