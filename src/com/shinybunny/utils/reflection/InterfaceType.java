package com.shinybunny.utils.reflection;

import java.lang.reflect.TypeVariable;
import java.util.*;

public class InterfaceType extends ReflectedType implements ITypeParamContainer {

    public InterfaceType(Class<?> cls) {
        super(cls);
    }

    public boolean doesExtend(InterfaceType type) {
        return getExtends().contains(type);
    }

    public List<InterfaceType> getExtends() {
        List<InterfaceType> interfaces = new ArrayList<>();
        for (Class<?> c : clazz.getInterfaces()) {
            interfaces.add(InterfaceType.of(c));
        }
        return interfaces;
    }

    @Override
    public TypeVariable<?>[] _typeVariables() {
        return clazz.getTypeParameters();
    }

    public static InterfaceType of(Class<?> cls) {
        return new InterfaceType(cls);
    }
}
