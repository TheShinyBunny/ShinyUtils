package com.shinybunny.utils.json;

@FunctionalInterface
public interface JsonSerializer<T> {

    Json serialize(T obj, JsonHelper helper);

}
