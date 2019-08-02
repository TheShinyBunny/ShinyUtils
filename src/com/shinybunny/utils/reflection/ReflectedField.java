package com.shinybunny.utils.reflection;

import com.sun.istack.internal.Nullable;

import java.lang.reflect.Field;

public class ReflectedField {

    private ReflectedType ownerType;
    @Nullable
    private ReflectedObject owner;
    private Field field;

    public ReflectedField(ReflectedObject obj, ReflectedType objType, String name) {
        this.owner = obj;
        this.ownerType = objType;
        try {
            this.field = objType.getHandle().getField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public ReflectedField ensureType(ReflectedType type) {
        if (getType().canBeCastTo(type)) {
            return this;
        }
        throw new ReflectionException("Requested field " + getName() + " of type " + type + ", but is of type " + getType());
    }

    public String getName() {
        return field.getName();
    }

    public ReflectedType getType() {
        return ReflectedType.of(field.getType());
    }

    public Object get() {
        field.setAccessible(true);
        try {
            return field.get(owner);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T get(Class<T> cast) {
        return cast.cast(get());
    }

    public void set(Object obj, Object value) {
        field.setAccessible(true);
        try {
            field.set(obj,value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
