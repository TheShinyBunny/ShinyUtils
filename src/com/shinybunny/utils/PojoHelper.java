package com.shinybunny.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.function.BiFunction;

public class PojoHelper {

    public static <Data, T> Data serializeObject(T obj, Data dataContainer, TriConsumer<Data, String, Object> propertySetter) throws IllegalAccessException {
        for (Field f : obj.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers()) && !f.isAnnotationPresent(Ignore.class)) {
                String name = f.getName();
                Name key = f.getAnnotation(Name.class);
                if (key != null) {
                    name = key.value();
                }
                f.setAccessible(true);
                propertySetter.accept(dataContainer,name,f.get(obj));
            }
        }
        return dataContainer;
    }


    public static <T> T deserializeObject(Class<T> type, Iterable<String> keys, BiFunction<String, Class<?>, Object> propertyGetter) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        T t = type.newInstance();
        for (String key : keys) {
            Field f = null;
            try {
                f = type.getDeclaredField(key);
            } catch (NoSuchFieldException e) {
                for (Field fi : type.getFields()) {
                    if (!fi.isAnnotationPresent(Ignore.class) && fi.isAnnotationPresent(Name.class)) {
                        f = type.getDeclaredField(fi.getAnnotation(Name.class).value());
                    }
                }
            }
            if (f != null && !Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                f.set(t, propertyGetter.apply(key, f.getType()));
            }
        }
        return t;
    }
}
