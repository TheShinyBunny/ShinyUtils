package com.shinybunny.utils;

@FunctionalInterface
public interface TriPredicate<T,A,B> {

    boolean test(T t, A a, B b);

}
