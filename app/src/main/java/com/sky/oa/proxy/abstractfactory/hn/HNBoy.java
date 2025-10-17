package com.sky.oa.proxy.abstractfactory.hn;

import com.sky.base.utils.LogUtils;
import com.sky.oa.proxy.abstractfactory.api.Boy;

/**
 * 新年系列的男孩子
 *
 */
public class HNBoy implements Boy {

    @Override
    public void drawMan() {
        LogUtils.i("-----------------新年系列的男孩子--------------------");
    }
}
