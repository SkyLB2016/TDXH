package com.sky.base.api;

/**
 * Created by libin on 2020/05/09 6:40 PM Saturday.
 */
public interface IMVVMViewModel<V> {
    void attachUI(V view);

    V getView();

    boolean isUIAttached();

    void detachUI();

}
