package com.shinybunny.utils.reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;

public class ReflectedMethod implements ITypeParamContainer {

    private Method method;

    public ReflectedMethod(Method method) {
        this.method = method;
    }

    public String getName() {
        return method.getName();
    }

    public ReflectedType getOwner() {
        return ReflectedType.of(method.getDeclaringClass());
    }

    public boolean isDefault() {
        return !method.isDefault();
    }

    @Override
    public TypeVariable<?>[] _typeVariables() {
        return method.getTypeParameters();
    }

    public Object invoke(Object obj, Object... params) {
        try {
            method.setAccessible(true);
            return method.invoke(obj,params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
