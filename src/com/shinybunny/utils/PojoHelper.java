package com.shinybunny.utils;

import com.shinybunny.utils.db.annotations.Adapter;

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
                Object value = Adapter.Helper.serialize(f,obj);
                propertySetter.accept(dataContainer,name,value);
            }
        }
        return dataContainer;
    }


    public static <T> T deserializeObject(Class<T> type, Iterable<String> keys, BiFunction<String, Class<?>, Object> propertyGetter) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        T t = type.newInstance();
        for (String key : keys) {
            Field f = null;
            for (Field f1 : type.getDeclaredFields()) {
                if (!f1.isAnnotationPresent(Ignore.class)) {
                    if (Name.Helper.getName(f1).equals(key)) {
                        f = f1;
                    }
                }
            }
            if (f == null) {
                System.out.println("[WARN]: no field in class " + type + " named " + key);
            } else if (!Modifier.isStatic(f.getModifiers())) {
                f.setAccessible(true);
                Object o = propertyGetter.apply(key, f.getType());
                f.set(t, Adapter.Helper.deserialize(f,o));
            }
        }
        return t;
    }
}
