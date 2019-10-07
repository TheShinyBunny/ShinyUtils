package com.shinybunny.utils;

import java.util.function.Consumer;

public class Check {

    public static boolean inRange(int num, int min, int max) {
        return num >= min && num <= max;
    }

    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static <T> void notNull(T obj, Consumer<T> action) {
        if (obj != null) action.accept(obj);
    }

    public static void isNull(Object obj, Runnable action) {
        if (obj == null) action.run();
    }

    public static <T extends Throwable> void isNull(Object obj, T t) throws T {
        if (obj == null) throw t;
    }

    public static <T> T defaultTo(T obj, T def) {
        return obj == null ? def : obj;
    }

    public static <T> T notNull(T obj) {
        return notNull(obj,(String)null);
    }

    public static <T> T notNull(T obj, String msg) {
        if (obj == null) {
            if (msg == null) throw new NullPointerException();
            throw new NullPointerException();
        }
        return obj;
    }
}
