package com.sky.oa.proxy.abstractfactory;

import com.sky.oa.proxy.abstractfactory.api.Boy;
import com.sky.oa.proxy.abstractfactory.api.Girl;
import com.sky.oa.proxy.abstractfactory.api.PersonFactory;
import com.sky.oa.proxy.abstractfactory.hn.HNBoy;
import com.sky.oa.proxy.abstractfactory.hn.HNGirl;

/**
 * 新年系列加工厂
 *
 * @author Administrator
 */
public class HNFactory implements PersonFactory {

    @Override
    public Boy getBoy() {
        return new HNBoy();
    }

    @Override
    public Girl getGirl() {
        return new HNGirl();
    }

}

