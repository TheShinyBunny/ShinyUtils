package com.shinybunny.utils.json;

import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonElementType<T> {
    public static final JsonElementType<Json> OBJECT = new JsonElementType<>("JsonObject",json-> "{" + json.getEntries().entrySet().stream().map(e->"\"" + e.getKey() + "\": " + e.getValue().getType().toString(e.getValue(),true)).collect(Collectors.joining(", ")) + "}");
    public static final JsonElementType<JsonArray> ARRAY = new JsonElementType<>("JsonArray",json -> "[" + json.values().join(",") + "]");
    public static final JsonElementType<Integer> INT = new JsonElementType<>("JsonInt");
    public static final JsonElementType<Double> DOUBLE = new JsonElementType<>("JsonDouble");
    public static final JsonElementType<String> STRING = new JsonElementType<>("JsonString");
    public static final JsonElementType<Boolean> BOOLEAN = new JsonElementType<>("JsonBoolean");
    public static final JsonElementType<Json> ANY = new JsonElementType<>("JsonAny");

    private String name;
    private Function<Json,String> toString;

    private JsonElementType(String name) {
        this.name = name;
        this.toString = json->String.valueOf(json.getValue());
    }

    private JsonElementType(String name, Function<Json, String> toString) {
        this.name = name;
        this.toString = toString;
    }

    public static <T> JsonElementType<T> valueOf(Class<T> type) {
        if (type == Json.class) return (JsonElementType<T>) OBJECT;
        if (type == JsonArray.class) return (JsonElementType<T>) ARRAY;
        if (type == Integer.TYPE) return (JsonElementType<T>) INT;
        if (type == Double.TYPE) return (JsonElementType<T>) DOUBLE;
        if (type == String.class) return (JsonElementType<T>) STRING;
        if (type == Boolean.TYPE) return (JsonElementType<T>) BOOLEAN;
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public String toString(Json json, boolean quote) {
        if (quote && this == STRING) return "\"" + json.getValue() + "\"";
        return toString.apply(json);
    }
}
