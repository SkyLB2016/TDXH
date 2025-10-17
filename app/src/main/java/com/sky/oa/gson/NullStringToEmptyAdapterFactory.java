package com.sky.oa.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {

    public static NullStringToEmptyAdapterFactory getInstance() {
        return new NullStringToEmptyAdapterFactory();
    }

    public static String getTypeAdapterFactoryName(String modelClassName) {
        return modelClassName + "TypeAdapter";
    }

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
//        Class<T> rawType = (Class<T>) type.getRawType();
//        if (rawType == String.class) {
//            return (TypeAdapter<T>) new StringNullAdapter();
//        }

        String name = getTypeAdapterFactoryName(type.getRawType().getName());
        Class<?> typeAdapterClass;
        try {
            typeAdapterClass = Class.forName(name);
        } catch (ClassNotFoundException e) {
            return null;
        }

        Constructor<TypeAdapter<T>> constructor;
        try {
            constructor = (Constructor<TypeAdapter<T>>) typeAdapterClass.getDeclaredConstructor(Gson.class, TypeToken.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Missing constructor constructor(Gson, TypeToken) for " + name, e);
        }
        try {
            return constructor.newInstance(gson, type);
        } catch (IllegalAccessException | InstantiationException | ClassCastException | InvocationTargetException e) {
            throw new RuntimeException("Can't create an instance of " + name, e);
        }
    }
}