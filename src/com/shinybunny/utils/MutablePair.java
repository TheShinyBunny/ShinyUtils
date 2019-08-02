package com.shinybunny.utils;

public class MutablePair<A,B> extends Pair<A,B> {
    public MutablePair(A a, B b) {
        super(a, b);
    }

    public void setFirst(A a) {
        this.a = a;
    }

    public void setSecond(B b) {
        this.b = b;
    }

    public static <A,B> MutablePair<A,B> of(A a, B b) {
        return new MutablePair<>(a,b);
    }
}
