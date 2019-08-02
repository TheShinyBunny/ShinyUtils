package com.shinybunny.utils.reflection;

public class ReflectedObject {

    private Object obj;
    private ReflectedType type;

    public ReflectedObject(Object obj) {
        this.obj = obj;
        this.type = ReflectedType.of(obj.getClass());
    }

    public ReflectedField get(String name, ReflectedType type) {
        return new ReflectedField(this,this.type,name).ensureType(type);
    }

    public void set(String field, Object value) {
        type.getField(field).set(obj,value);
    }

    public Object call(String method, Class<?>[] types, Object... params) {
        return type.getMethod(method,types).invoke(obj,params);
    }

}
