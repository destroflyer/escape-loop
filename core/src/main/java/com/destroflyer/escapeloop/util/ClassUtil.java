package com.destroflyer.escapeloop.util;

import java.lang.reflect.InvocationTargetException;

public class ClassUtil {

    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }
}
