package com.shinybunny.utils;

import java.util.function.Consumer;

@FunctionalInterface
public interface Task<T,E> {

    void run(Consumer<T> success, Consumer<E> fail);

}
