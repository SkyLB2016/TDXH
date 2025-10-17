package com.sky.oa.proxy.factory.hair;


import com.sky.base.utils.LogUtils;
import com.sky.oa.proxy.factory.api.HairInterface;

/**
 * 中分发型
 *
 * @author Administrator
 */
public class InHair implements HairInterface {

    @Override
    public void draw() {
        LogUtils.i("-----------------中分发型-------------------");
    }

}
