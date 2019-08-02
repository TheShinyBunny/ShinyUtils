package com.shinybunny.utils.reflection;

import com.shinybunny.utils.Array;

import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.*;

public class ClassType extends ReflectedType implements ITypeParamContainer {

    public ClassType(Class<?> clazz) {
        super(clazz);
    }

    public boolean isSuperOf(ClassType type) {
        return Objects.equals(type.getSuper(), this);
    }

    public ClassType getSuper() {
        if (clazz.getSuperclass() == null) return null;
        return ClassType.of(clazz.getSuperclass());
    }

    public boolean doesExtend(ClassType type) {
        return Objects.equals(getSuper(), type);
    }

    public boolean isDescendantOf(ClassType type) {
        if (type.isObjectClass()) return true;
        ClassType sup = getSuper();
        if (sup == null) return false;
        while (!sup.equals(type)) {
            sup = sup.getSuper();
            if (sup == null) return false;
        }
        return true;
    }

    private boolean isObjectClass() {
        return clazz == Object.class;
    }

    public boolean doesImplement(InterfaceType type) {
        return getImplements().contains(type,false);
    }

    public Array<InterfaceType> getImplements() {
        Array<InterfaceType> interfaces = new Array<>();
        for (Class<?> c : clazz.getInterfaces()) {
            interfaces.add(InterfaceType.of(c));
        }
        return interfaces;
    }

    public static ClassType of(Class<?> cls) {
        return new ClassType(cls);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(clazz.getModifiers());
    }

    public boolean isFinal() {
        return Modifier.isFinal(clazz.getModifiers());
    }


    @Override
    public TypeVariable<?>[] _typeVariables() {
        return clazz.getTypeParameters();
    }
}
