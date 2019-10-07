package com.shinybunny.utils.json;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.UtilsTest;

import java.util.*;

public class Json implements Iterable<Map.Entry<String,Json>> {


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

    public static Json serialize(Object obj) {
        return JsonHelper.DEFAULT_HELPER.serialize(obj);
    }

    public Map<String, Json> getEntries() {
        return entries;
    }

    public Json get(String path) {
        return get(path, JsonElementType.ANY);
    }

    public <T> T get(String path, JsonElementType<T> type) {
        Optional<T> optionalT = getInternal(path,false,type);
        return optionalT.orElse(null);
    }

    public Optional<Json> getOptional(String path) {
        return getInternal(path,true,JsonElementType.ANY);
    }

    public <T> Optional<T> getOptional(String path, JsonElementType<T> type) {
        return getInternal(path,true,type);
    }

    public <T> T get(String path, Class<T> type) {
        JsonElementType<?> elementType = JsonElementType.valueOf(type);
        if (elementType == null) elementType = JsonElementType.ANY;
        Object t = get(path,elementType);
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

    public Json set(String path, Object value) {
        return set(path, value, false);
    }

    public Json set(String path, Object value, boolean force) {
        return setInternal(path,of(helper,value),force);
    }

    public Json remove(String path) {
        return set(path,null);
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
        return getType().toString(this,true);
    }

    public String toNormalString() {
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

    public Array<Json> values() {
        return new Array<>(entries.values());
    }

    public <T> T to(JsonElementType<T> type) {
        if (type == JsonElementType.INT) return (T)(Integer)toInt();
        if (type == JsonElementType.DOUBLE) return (T)(Double)toDouble();
        if (type == JsonElementType.STRING) return (T) toNormalString();
        if (type == JsonElementType.BOOLEAN) return (T) (Boolean)toBoolean();
        if (type == JsonElementType.ANY) return (T) getValue();
        return (T)this;
    }

    public Object getValue() {
        return this;
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
        addAll(json.entries);
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<Map.Entry<String, Json>> iterator() {
        return entries.entrySet().iterator();
    }

    public boolean isPrimitive() {
        return false;
    }

    public void replaceAllWith(Json other) {
        this.entries = new HashMap<>(other.entries);
    }


    protected void onEntryChanged(String key, Json value) {

    }

    private <T> Optional<T> getInternal(String path, boolean optional, JsonElementType<T> type) {
        if (this instanceof JsonPrimitive || path.isEmpty()) {
            return JsonHelper.ensureTypeOptional(this,optional,type);
        }
        if (path.startsWith("[")) {
            if (this instanceof JsonArray) {
                if (!path.contains("]")) throw JsonHelperException.ARRAY_INDEXER_NOT_CLOSED.create(path);
                int closeIndex = path.indexOf(']');
                String indexStr = path.substring(1,closeIndex);
                try {
                    int index = Integer.parseInt(indexStr);
                    String rest = closeIndex > path.length() - 2 ? "" : path.substring(closeIndex + 2);
                    JsonArray arr = (JsonArray)this;
                    Json element = arr.get(index);
                    if (element == null) {
                        if (optional) return Optional.empty();
                        throw JsonHelperException.ARRAY_INDEX_OUT_OF_BOUNDS.create(index, this);
                    }
                    return element.getInternal(rest,optional,type);
                } catch (Exception e) {
                    if (e instanceof JsonHelperException) throw e;
                    throw JsonHelperException.INVALID_INDEX.create(path,indexStr);
                }
            } else {
                throw JsonHelperException.ELEMENT_IS_NOT_ARRAY.create(this);
            }
        } else {
            int index = keyEndIndex(path);
            String key = path.substring(0,index);
            Json element = entries.get(key);
            String restOfPath = index == path.length() ? "" : path.substring(index + 1);
            if (element == null) {
                if (optional) {
                    return Optional.empty();
                }
                throw JsonHelperException.UNKNOWN_KEY.create(key,this);
            }
            return element.getInternal(restOfPath,optional,type);
        }
    }

    private Json setInternal(String path, Json value, boolean force) {
        if (path.isEmpty()) return null;
        if (this instanceof JsonPrimitive) {
            throw JsonHelperException.ELEMENT_IS_NOT_OBJECT.create(this);
        }
        if (path.startsWith("[")) {
            if (this instanceof JsonArray) {
                if (!path.contains("]")) throw JsonHelperException.ARRAY_INDEXER_NOT_CLOSED.create(path);
                int closeIndex = path.indexOf(']');
                String indexStr = path.substring(1,closeIndex);
                try {
                    int index = Integer.parseInt(indexStr);
                    String rest = closeIndex > path.length() - 2 ? "" : path.substring(closeIndex + 2);
                    JsonArray arr = (JsonArray)this;
                    Json element = arr.get(index);
                    if (rest.isEmpty()) {
                        Json prev = arr.set(index,value);
                        onEntryChanged(index + "",value);
                        return prev;
                    } else {
                        Json prev = element.setInternal(rest,value,force);
                        onEntryChanged(index + "",value);
                        return prev;
                    }
                } catch (Exception e) {
                    if (e instanceof JsonHelperException) throw e;
                    throw JsonHelperException.INVALID_INDEX.create(path,indexStr);
                }
            } else {
                throw JsonHelperException.ELEMENT_IS_NOT_ARRAY.create(this);
            }
        } else {
            int index = keyEndIndex(path);
            String key = path.substring(0,index);
            Json element = entries.get(key);
            String restOfPath = index == path.length() ? "" : element == null || !element.isArray() ? path.substring(index + 1) : path.substring(index);
            if (restOfPath.isEmpty()) {
                Json prev;
                if (value == null) {
                    prev = entries.remove(key);
                } else {
                    prev = entries.put(key, value);
                }
                onEntryChanged(key, value);
                return prev;
            } else {
                if (element == null) {
                    if (force) {
                        throw JsonHelperException.UNKNOWN_KEY.create(key, this);
                    }
                    return null;
                }
                Json prev = element.setInternal(restOfPath, value, force);
                onEntryChanged(key, value);
                return prev;
            }
        }
    }

    private int keyEndIndex(String path) {
        int separator = path.indexOf(helper.pathSeparator);
        int arrayIndexer = path.indexOf('[');
        if (separator == path.length() - 1) {
            throw JsonHelperException.PATH_SEPARATOR_LAST_CHAR.create(helper.pathSeparator, path);
        }
        if (arrayIndexer == path.length() - 1) {
            throw JsonHelperException.ARRAY_INDEXER_NOT_CLOSED.create(path);
        }
        while (separator > 0 && path.charAt(separator - 1) == '\\') {
            separator = path.indexOf(helper.pathSeparator, separator);
        }
        int index = separator < 0 ? arrayIndexer : arrayIndexer < 0 ? separator : arrayIndexer;
        if (index <= 0) {
            index = path.length();
        }
        return index;
    }

    public <T> T deserialize(Class<T> type) {
        return helper.adapt(this,type);
    }
}
