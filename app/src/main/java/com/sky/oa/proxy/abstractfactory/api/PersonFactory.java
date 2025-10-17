package com.sky.oa.proxy.abstractfactory.api;

/**
 * 人物的实现接口
 *
 * @author Administrator
 */
public interface PersonFactory {

    //男孩接口
    Boy getBoy();

    //女孩接口
    Girl getGirl();

}
