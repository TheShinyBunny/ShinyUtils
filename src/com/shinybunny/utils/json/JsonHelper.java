package com.shinybunny.utils.json;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.PojoHelper;
import com.shinybunny.utils.fs.File;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JsonHelper {

    public static final JsonHelper DEFAULT_HELPER = new JsonHelper();
    public char pathSeparator = '.';
    private Map<Class<?>, JsonAdapter<?>> adapters;

    public JsonHelper() {
        adapters = new HashMap<>();
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
        if (obj == null) return null;
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
            for (int i = 0; i < java.lang.reflect.Array.getLength(obj); i++) {
                arr.add(from(java.lang.reflect.Array.get(obj,i)));
            }
            return arr;
        }
        if (Array.class.isAssignableFrom(obj.getClass())) {
            return new JsonArray(this, new Array<>((Array<?>)obj).map(this::from));
        }
        if (Collection.class.isAssignableFrom(obj.getClass())) {
            return new JsonArray(this, new Array<>((Iterable<?>)obj).map(this::from));
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
        if (adapter == null) {
            try {
                return PojoHelper.serializeObject(obj,newObject(),Json::set);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return JsonAdapter.serializeCasted(adapter, obj, this);
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
        if (adapter == null) {
            try {
                return PojoHelper.deserializeObject(type,obj.keys(),obj::get);
            } catch (IllegalAccessException | InstantiationException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
        return adapter.deserialize(obj, this);
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
