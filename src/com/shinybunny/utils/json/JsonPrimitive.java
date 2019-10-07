package com.shinybunny.utils.json;

public class JsonPrimitive extends Json {

    private JsonElementType<?> type;
    private Object value;

    public JsonPrimitive(JsonHelper helper, JsonElementType type, Object value) {
        super(helper);
        this.type = type;
        this.value = value;
    }

    public static JsonPrimitive of(JsonHelper helper, String s) {
        return new JsonPrimitive(helper,JsonElementType.STRING,s);
    }
    public static JsonPrimitive of(JsonHelper helper, int i) {
        return new JsonPrimitive(helper,JsonElementType.INT,i);
    }
    public static JsonPrimitive of(JsonHelper helper, double d) {
        return new JsonPrimitive(helper,JsonElementType.DOUBLE,d);
    }
    public static JsonPrimitive of(JsonHelper helper, boolean b) {
        return new JsonPrimitive(helper,JsonElementType.BOOLEAN,b);
    }

    @Override
    public JsonElementType<?> getType() {
        return type;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public int toInt() {
        if (type == JsonElementType.INT) return (int)value;
        if (type == JsonElementType.DOUBLE) return (int)(double)value;
        if (type == JsonElementType.BOOLEAN) return (boolean)value ? 1 : 0;
        return toNormalString().length();
    }

    @Override
    public boolean toBoolean() {
        if (type == JsonElementType.BOOLEAN) return (boolean)value;
        if (type == JsonElementType.STRING) {
            if (value.toString().equalsIgnoreCase("true") || value.toString().equalsIgnoreCase("false")) return Boolean.parseBoolean(value.toString());
        }
        if (type == JsonElementType.INT) return toInt() != 0;
        if (type == JsonElementType.DOUBLE) return toDouble() != 0;
        return !toNormalString().isEmpty();
    }

    @Override
    public double toDouble() {
        if (type == JsonElementType.DOUBLE) return (double)value;
        if (type == JsonElementType.INT) return (double)(int)value;
        if (type == JsonElementType.BOOLEAN) return (boolean)value ? 1 : 0;
        return toNormalString().length();
    }

    @Override
    public boolean isPrimitive() {
        return true;
    }
}
