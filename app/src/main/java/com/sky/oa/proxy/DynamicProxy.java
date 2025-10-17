package com.sky.oa.proxy;


import com.sky.base.utils.LogUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by libin on 2020/04/18 5:25 PM Saturday.
 */
public class DynamicProxy implements InvocationHandler {
    private Object factory;

    public Object getFactory() {
        return factory;
    }

    public void setFactory(Object factory) {
        this.factory = factory;
    }

    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                factory.getClass().getClassLoader(),
//                DynamicProxy.class.getClassLoader(),
                factory.getClass().getInterfaces(),
//                new Class[]{Hair.class},
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        preSaleService();
        Object result = method.invoke(factory, args);
        afterSaleService();

        return result; 
    }

    public void preSaleService() {
        LogUtils.i("精美包装，完整服务");
    }

    public void afterSaleService() {
        LogUtils.i("根据需求完成您的服务");
    }
}
