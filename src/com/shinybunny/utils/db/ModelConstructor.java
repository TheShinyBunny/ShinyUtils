package com.shinybunny.utils.db;

import com.shinybunny.utils.ExceptionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public interface ModelConstructor {

    ExceptionFactory NO_MODEL_CONSTRUCTOR_EXCEPTION = ExceptionFactory.make("No constructor found for data model ${type} to use for deserialization. " +
            "The class needs to have an empty constructor or a constructor that takes one ResultRow parameter.");

    static ModelConstructor createFor(Class<?> modelClass) {
        try {
            Constructor<?> ctor = modelClass.getDeclaredConstructor();
            return emptyConstructor(ctor);
        } catch (Exception e) {
            try {
                Constructor<?> ctor = modelClass.getDeclaredConstructor(ResultRow.class);
                return resultRowConstructor(ctor);
            } catch (NoSuchMethodException ex) {
                ex.printStackTrace();
            }
        }
        throw NO_MODEL_CONSTRUCTOR_EXCEPTION.create(modelClass);
    }

    static ModelConstructor emptyConstructor(Constructor<?> ctor) {
        return (model,row)->{
            try {
                Object instance = ctor.newInstance();
                for (String s : row.keys()) {
                    Object o = row.get(s);
                    Field f = model.getField(s);
                    f.setAccessible(true);
                    f.set(instance,o);
                }
                return instance;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    static ModelConstructor resultRowConstructor(Constructor<?> ctor) {
        return (model, row)->{
            try {
                return ctor.newInstance(row);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            return null;
        };
    }

    Object newInstance(Model model, ResultRow row);

}
