package com.shinybunny.utils.json;

import com.shinybunny.utils.ListUtils;
import com.shinybunny.utils.PojoHelper;
import com.shinybunny.utils.fs.File;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class JsonHelper {

    public static final JsonHelper DEFAULT_HELPER = new JsonHelper();
    private boolean throwOnNoAdaptable = true;
    private char pathSeparator = '.';
    private Map<Class<?>, JsonAdapter<?>> adapters;

    public JsonHelper() {
        adapters = new HashMap<>();
    }

    public <T> Optional<T> getByPath(String path, Json json, boolean optional, JsonElementType<T> type) {
        if (json instanceof JsonPrimitive || path.isEmpty()) {
            return ensureTypeOptional(json,optional,type);
        }
        return findPath(path,json,(index,restOfPath)-> {
            Json element = ((JsonArray) json).get(index);
            if (element == null) {
                if (optional) return Optional.empty();
                throw JsonHelperException.ARRAY_INDEX_OUT_OF_BOUNDS.create(index, json);
            }
            return getByPath(restOfPath, element, optional, type);
        },(element,key,restOfPath)->{
            if (element == null) {
                if (optional) {
                    return Optional.empty();
                }
                throw JsonHelperException.UNKNOWN_KEY.create(key,json);
            }
            return getByPath(restOfPath,element,optional,type);
        });
    }

    public void setPath(String path, Json value, Json json, boolean force) {
        if (json instanceof JsonPrimitive || path.isEmpty()) return;
        findPath(path, json, (index, rest) -> {
            JsonArray arr = (JsonArray)json;
            Json element = arr.get(index);
            if (rest.isEmpty()) {
                arr.set(index,value);
            } else {
                setPath(rest,value,element,force);
            }
            return null;
        },(element,key,rest)->{
            if (rest.isEmpty()) {
                json.getEntries().put(key, value);
            } else {
                if (element == null) {
                    if (force) {
                        throw JsonHelperException.UNKNOWN_KEY.create(key, json);
                    }
                    return null;
                }
                setPath(rest,value,element,force);
            }
            return null;
        });
    }

    private <T> T findPath(String path, Json json, BiFunction<Integer,String,T> foundArray, TriFunction<Json,String,String,T> foundObject) {
        if (path.startsWith("[")) {
            return accessArray(path,json,foundArray);
        }
        return accessObject(path,json,foundObject);
    }

    private <T> T accessArray(String path, Json json, BiFunction<Integer,String,T> func) {
        if (json instanceof JsonArray) {
            if (!path.contains("]")) throw JsonHelperException.ARRAY_INDEXER_NOT_CLOSED.create(path);
            int closeIndex = path.indexOf(']');
            String indexStr = path.substring(1,closeIndex);
            try {
                int index = Integer.parseInt(indexStr);
                return func.apply(index,closeIndex > path.length() - 2 ? "" : path.substring(closeIndex + 2));
            } catch (Exception e) {
                if (e instanceof JsonHelperException) throw e;
                throw JsonHelperException.INVALID_INDEX.create(path,indexStr);
            }
        } else {
            throw JsonHelperException.ELEMENT_IS_NOT_ARRAY.create(json);
        }
    }

    private <T> T accessObject(String path, Json json, TriFunction<Json,String,String,T> func) {
        if (json.is(JsonElementType.OBJECT)) {
            int separator = path.indexOf(pathSeparator);
            int arrayIndexer = path.indexOf('[');
            if (separator == path.length() - 1) {
                throw JsonHelperException.PATH_SEPARATOR_LAST_CHAR.create(pathSeparator, path);
            }
            if (arrayIndexer == path.length() - 1) {
                throw JsonHelperException.ARRAY_INDEXER_NOT_CLOSED.create(path);
            }
            while (separator > 0 && path.charAt(separator - 1) == '\\') {
                separator = path.indexOf(pathSeparator, separator);
            }
            int index = separator < 0 ? arrayIndexer : arrayIndexer < 0 ? separator : arrayIndexer;
            if (index <= 0) {
                index = path.length();
            }
            String key = path.substring(0, index);
            Json element = json.getEntries().get(key);
            String restOfPath = index == path.length() ? "" : path.substring(index + 1);
            return func.apply(element, key, restOfPath);
        }
        throw JsonHelperException.ELEMENT_IS_NOT_OBJECT.create(json);
    }

    public static <T> Optional<T> ensureTypeOptional(Json element, boolean optional, JsonElementType<T> type) {
        if (element.is(type)) {
            return Optional.of(element.to(type));
        }
        if (optional) {
            return Optional.empty();
        }
        throw JsonHelperException.INVALID_EXPECTED_TYPE.create(element,type);
    }

    public Json from(Object obj) {
        if (obj instanceof Json) return (Json) obj;
        if (obj instanceof Integer) {
            return new JsonPrimitive(this,JsonElementType.INT,obj);
        }
        if (obj instanceof String) {
            return new JsonPrimitive(this,JsonElementType.STRING,obj);
        }
        if (obj instanceof Boolean) {
            return new JsonPrimitive(this,JsonElementType.BOOLEAN,obj);
        }
        if (obj instanceof Double) {
            return new JsonPrimitive(this,JsonElementType.DOUBLE,obj);
        }
        if (obj.getClass().isEnum()) {
            return JsonPrimitive.of(this,obj.toString().toLowerCase());
        }
        if (obj.getClass().isArray()) {
            JsonArray arr = new JsonArray(this);
            for (int i = 0; i < Array.getLength(obj); i++) {
                arr.add(from(Array.get(obj,i)));
            }
            return arr;
        }
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            System.out.println("creating json array from " + obj);
            return new JsonArray(this, ListUtils.map((Iterable<?>)obj,this::from));
        }
        if (Map.class.isAssignableFrom(obj.getClass())) {
            Json json = newObject();
            for (Map.Entry<?,?> e : ((Map<?,?>)obj).entrySet()) {
                json.set(e.getKey().toString(),e.getValue());
            }
            return json;
        }
        return serialize(obj);
    }

    public Json newObject() {
        return new Json(this);
    }

    public JsonArray newArray() {
        return new JsonArray(this);
    }

    public Json serialize(Object obj) {
        JsonAdapter<?> adapter = getAdapter(obj.getClass());
        if (adapter == null && obj.getClass().isAnnotationPresent(JsonAdaptable.class)) {
            try {
                return PojoHelper.serializeObject(obj,newObject(),Json::set);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (adapter != null) {
            return JsonAdapter.serializeCasted(adapter, obj, this);
        }
        return null;
    }



    public <T> JsonAdapter<T> getAdapter(Class<T> type) {
        JsonAdapter<?> adapter = adapters.get(type);
        if (adapter == null) {
            for (Map.Entry<Class<?>, JsonAdapter<?>> e : adapters.entrySet()) {
                if (e.getKey().isAssignableFrom(type)) return (JsonAdapter<T>) adapter;
            }
            return null;
        }
        return (JsonAdapter<T>) adapter;
    }

    public <T> JsonHelper withAdapter(JsonAdapter<T> adapter) {
        adapters.put(adapter.getType(),adapter);
        return this;
    }

    public <T> T adapt(Json obj, Class<T> type) {
        JsonAdapter<T> adapter = getAdapter(type);
        if (adapter == null && type.isAnnotationPresent(JsonAdaptable.class)) {
            try {
                return PojoHelper.deserializeObject(type,obj.keys(),obj::get);
            } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        if (adapter != null) {
            return adapter.deserialize(obj, this);
        }
        return null;
    }

    public <T> T adapt(File file, Class<T> type) {
        return adapt(readJson(file),type);
    }

    public static Json readJson(File file) {
        return DEFAULT_HELPER.load(file);
    }

    public Json load(File file) {
        String str = file.getContent();
        str = str.replaceAll("\n","");
        if (str.trim().isEmpty()) return DEFAULT_HELPER.newObject();
        return new JsonReader(DEFAULT_HELPER,str).read();
    }

    public static Json parse(String str) {
        if (str.trim().isEmpty()) return DEFAULT_HELPER.newObject();
        return new JsonReader(DEFAULT_HELPER,str).read();
    }

    private interface TriFunction<A, B, C, T> {

        T apply(A a, B b, C c);

    }
}
