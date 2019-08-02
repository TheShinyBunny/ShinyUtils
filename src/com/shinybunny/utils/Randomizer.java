package com.shinybunny.utils;

import java.util.Random;
import java.util.function.Predicate;

public class Randomizer {

    public static final Random RANDOM = new Random();

    public static int randomInt() {
        return RANDOM.nextInt();
    }

    public static int next(int bound) {
        return RANDOM.nextInt(bound);
    }

    public static int range(int min, int max) {
        min = Math.min(min,max);
        max = Math.max(min,max);
        return RANDOM.nextInt(max - min + 1) + min;
    }

    public static <T> T fromArray(T[] arr) {
        return arr.length == 0 ? null : arr.length == 1 ? arr[0] : arr[next(arr.length)];
    }

    public static <E extends Enum<E>> E randomEnum(Class<E> enumClass) {
        return ListUtils.randomItem(ListUtils.enumList(enumClass));
    }

    public static <E extends Enum<E>> E randomEnum(Class<E> enumClass, Predicate<E> filter) {
        return ListUtils.randomItem(ListUtils.enumList(enumClass),filter);
    }

}
