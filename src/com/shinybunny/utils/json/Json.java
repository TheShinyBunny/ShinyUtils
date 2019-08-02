package com.shinybunny.utils.json;

import java.util.*;

public class Json {


    protected JsonHelper helper;
    private Map<String,Json> entries;

    public Json(JsonHelper helper, Map<String, Json> entries) {
        this.helper = helper;
        this.entries = entries;
    }

    public Json(JsonHelper helper) {
        this(helper,new HashMap<>());
    }

    public Json() {
        this(JsonHelper.DEFAULT_HELPER);
    }

    public static Json of(Object obj) {
        return JsonHelper.DEFAULT_HELPER.from(obj);
    }

    public static Json of(JsonHelper helper, Object obj) {
        return helper.from(obj);
    }

    public Map<String, Json> getEntries() {
        return entries;
    }

    public Json get(String path) {
        return get(path, JsonElementType.ANY);
    }

    public <T> T get(String path, JsonElementType<T> type) {
        Optional<T> optionalT = helper.getByPath(path,this,false,type);
        return optionalT.orElse(null);
    }

    public Optional<Json> getOptional(String path) {
        return helper.getByPath(path,this,true,JsonElementType.ANY);
    }

    public <T> Optional<T> getOptional(String path, JsonElementType<T> type) {
        return helper.getByPath(path,this,true,type);
    }

    public <T> T get(String path, Class<T> type) {
        JsonElementType<?> elementType = JsonElementType.valueOf(type);
        if (elementType == null) elementType = JsonElementType.OBJECT;
        Object t = get(path,elementType);
        if (t instanceof Json) {
            return helper.adapt((Json) t, type);
        }
        return (T) t;
    }

    public int getInt(String path) {
        return getInt(path,0);
    }

    public int getInt(String path, int def) {
        return getOptional(path,JsonElementType.INT).orElse(def);
    }

    public String getString(String path) {
        return getString(path,null);
    }

    public String getString(String path, String def) {
        return getOptional(path,JsonElementType.STRING).orElse(def);
    }

    public double getDouble(String path) {
        return getDouble(path,0);
    }

    public double getDouble(String path, double def) {
        return getOptional(path,JsonElementType.DOUBLE).orElse(def);
    }

    public boolean getBoolean(String path) {
        return getBoolean(path,false);
    }

    public boolean getBoolean(String path, boolean def) {
        return getOptional(path,JsonElementType.BOOLEAN).orElse(def);
    }

    public JsonArray getArray(String path) {
        Optional<JsonArray> array = getOptional(path, JsonElementType.ARRAY);
        if (!array.isPresent()) {
            System.out.println("optional it's empty");
            return new JsonArray();
        }
        return array.get();
    }

    public String getEnum(String path, String... values) {
        String s = getString(path);
        if (s == null) return null;
        for(String v : values) {
            if (v.equalsIgnoreCase(s)) {
                return s;
            }
        }
        throw JsonHelperException.INVALID_ENUM_EXCEPTION.create(s,values);
    }

    public <E extends Enum<E>> E getEnum(String path, Class<E> enumClass) {
        String s = getString(path);
        if (s == null) return null;
        try {
            return Enum.valueOf(enumClass, s);
        } catch (Exception e) {
            try {
                return Enum.valueOf(enumClass, s.toUpperCase());
            } catch (Exception e3) {
                throw JsonHelperException.INVALID_ENUM_EXCEPTION.create(s,enumClass.getEnumConstants());
            }
        }

    }

    public void set(String path, Object value) {
        set(path, value, false);
    }

    public void set(String path, Object value, boolean force) {
        helper.setPath(path,of(helper,value),this,force);
    }

    public boolean is(JsonElementType type) {
        return type == null || type == JsonElementType.ANY || this.getType() == type;
    }

    public JsonElementType getType() {
        return JsonElementType.OBJECT;
    }

    public boolean has(String path) {
        return getOptional(path).isPresent();
    }

    @Override
    public String toString() {
        return getType().toString(this,false);
    }

    public int toInt() {
        return entries.size();
    }

    public double toDouble() {
        return entries.size();
    }

    public boolean toBoolean() {
        return isEmpty();
    }

    public boolean isString() {
        return getType() == JsonElementType.STRING;
    }

    public boolean isInt() {
        return getType() == JsonElementType.INT;
    }

    public boolean isDouble() {
        return getType() == JsonElementType.DOUBLE;
    }

    public boolean isBoolean() {
        return getType() == JsonElementType.BOOLEAN;
    }

    public boolean isArray() {
        return getType() == JsonElementType.ARRAY;
    }

    public boolean isObject() {
        return getType() == JsonElementType.OBJECT;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public Collection<Json> values() {
        return new ArrayList<>(entries.values());
    }

    public <T> T to(JsonElementType<T> type) {
        if (type == JsonElementType.INT) return (T)(Integer)toInt();
        if (type == JsonElementType.DOUBLE) return (T)(Double)toDouble();
        if (type == JsonElementType.STRING) return (T) toString();
        if (type == JsonElementType.BOOLEAN) return (T) (Boolean)toBoolean();
        return (T)this;
    }

    public Object getValue() {
        return entries;
    }

    public Set<String> keys() {
        return new HashSet<>(entries.keySet());
    }

    public void print() {
        System.out.println(this);
    }

    public void addAll(Map<String, Json> entries) {
        this.entries.putAll(entries);
    }

    public void addAll(Json json) {
        addAll(entries);
    }
}
