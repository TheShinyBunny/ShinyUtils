package com.shinybunny.utils;

import java.util.HashMap;

public class MapBuilder<K,V> extends HashMap<K,V> {

    public static <K,V> MapBuilder<K,V> of(K key, V value) {
        return new MapBuilder<K,V>().and(key,value);
    }

    public MapBuilder<K,V> and(K key, V value) {
        put(key,value);
        return this;
    }
}
