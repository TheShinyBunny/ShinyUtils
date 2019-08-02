package com.shinybunny.utils.json;

@FunctionalInterface
public interface JsonDeserializer<T> {

    T deserialize(Json json, JsonHelper helper);

}
