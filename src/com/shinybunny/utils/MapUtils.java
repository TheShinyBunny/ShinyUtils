package com.shinybunny.utils;

import com.shinybunny.utils.json.Json;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtils {

    public static <K,V> Map<K,V> createMap(Iterable<K> keys) {
        return fillMap(keys,null);
    }

    public static <V, K> Map<K, V> fillMap(Iterable<K> keys, V with) {
        Map<K,V> map = new HashMap<>();
        for (K k : keys) {
            map.put(k,with);
        }
        return map;
    }


    public static <K,V> MapBuilder<K,V> build(K key, V value) {
        return MapBuilder.of(key,value);
    }


    public static String join(String separator, Map<?,?> data, Function<Object, String> toString) {
        return data.entrySet().stream().map(e->e.getKey() + "=" + toString.apply(e.getValue())).collect(Collectors.joining(separator));
    }

    public static <K, V> K getKey(Map<K, V> map, V value) {
        for (Map.Entry<K,V> e : map.entrySet()) {
            if (e.getValue().equals(value)) {
                return e.getKey();
            }
        }
        return null;
    }

    public static <K, V, R> Array<R> remap(Map<K,V> map, Function<Map.Entry<K,V>,R> mapper) {
        Array<R> arr = new Array<>();
        for (Map.Entry<K,V> e : map.entrySet()) {
            arr.add(mapper.apply(e));
        }
        return arr;
    }
}
