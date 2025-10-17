package com.sky.oa.data.model;

import java.io.Serializable;

/**
 * @Description:
 * @Author: 李彬
 * @CreateDate: 2022/5/25 6:33 下午
 * @Version: 1.0
 */
public class KeyValue implements Serializable {
    private String key;
    private String value;

    public KeyValue() {
    }

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
