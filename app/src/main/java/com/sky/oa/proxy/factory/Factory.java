package com.sky.oa.proxy.factory;


import com.sky.oa.proxy.factory.api.HairInterface;

/**
 * Created by SKY on 2018/6/27 16:42.
 */
public abstract class Factory {

    public abstract <T extends HairInterface> T create(Class<T> cla);
}
