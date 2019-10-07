package com.shinybunny.utils.reflection;

import java.lang.reflect.Field;

public class Reflection {

    public static Object get(Field f, Object owner) {
        try {
            return f.get(owner);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
