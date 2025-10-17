package com.sky.oa.proxy.factory.hair;

import com.sky.base.utils.LogUtils;
import com.sky.oa.proxy.factory.api.HairInterface;

/**
 * 左偏分发型
 *
 * @author Administrator
 */
public class LeftHair implements HairInterface {

    @Override
    public void draw() {
        LogUtils.i("-----------------左偏分发型-------------------");
    }

}
