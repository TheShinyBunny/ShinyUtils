package com.shinybunny.utils.db;

import com.shinybunny.utils.ExceptionFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static com.shinybunny.utils.ExceptionFactory.make;

public enum DataSetType {
    SINGLE, COLLECTION, ARRAY;

    private static final ExceptionFactory RAW_COLLECTION_EXCEPTION = make("Dal data set type ${type} is a Collection with no type parameters!");

    public static DataSetType infer(Database db, Class<?> type, Type genericType) {
        if (db.getModel(type) == null) {
            if (Collection.class.isAssignableFrom(type)) {
                if (genericType instanceof ParameterizedType) {
                    Type[] typeParameters = ((ParameterizedType) genericType).getActualTypeArguments();
                    if (typeParameters.length != 1) throw RAW_COLLECTION_EXCEPTION.create(type);
                } else {
                    throw RAW_COLLECTION_EXCEPTION.create(type);
                }
                return COLLECTION;
            } else if (type.isArray()) {
                return ARRAY;
            }
        }
        return SINGLE;
    }

    public Class<?> getComponentType(Method m) {
        switch (this) {
            case SINGLE:
                return m.getReturnType();
            case ARRAY:
                return m.getReturnType().getComponentType();
            case COLLECTION:
                return ((Class<?>) ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0]);
        }
        return null;
    }

    public Class<?> getComponentType(Parameter p) {
        switch (this) {
            case SINGLE:
                return p.getType();
            case ARRAY:
                return p.getType().getComponentType();
            case COLLECTION:
                return ((Class<?>) ((ParameterizedType) p.getParameterizedType()).getActualTypeArguments()[0]);
        }
        return null;
    }
}
