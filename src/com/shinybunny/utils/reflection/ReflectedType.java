package com.shinybunny.utils.reflection;

import com.shinybunny.utils.Array;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public abstract class ReflectedType {

    private Type type;
    protected Class<?> clazz;

    public ReflectedType(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ReflectedType(Type t) {
        this.type = t;
    }

    public static ReflectedType of(Class<?> type) {
        return type.isInterface() ? new InterfaceType(type) : new ClassType(type);
    }

    public Class<?> getHandle() {
        return clazz;
    }

    public String getName() {
        return clazz.getSimpleName();
    }

    public String getFullName() {
        return clazz.getName();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReflectedType && ((ReflectedType) obj).clazz.equals(clazz);
    }

    public ReflectedField getField(String name) {
        return new ReflectedField(null,this,name);
    }

    public boolean canBeCastTo(ReflectedType type) {
        return type.clazz.isAssignableFrom(clazz);
    }

    public Privacy getPrivacy() {
        return Privacy.get(clazz.getModifiers());
    }

    public ReflectedMethod getMethod(String name) {
        for (ReflectedMethod m : getMethods()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    private Array<ReflectedMethod> getMethods() {
        return new Array<>(clazz.getMethods()).map(ReflectedMethod::new);
    }

    public ReflectedMethod getMethod(String name, Class<?>... types) {
        try {
            Method m = clazz.getMethod(name,types);
            return new ReflectedMethod(m);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
