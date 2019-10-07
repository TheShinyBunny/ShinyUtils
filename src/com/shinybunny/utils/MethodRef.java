package com.shinybunny.utils;

public @interface MethodRef {

    String value();

    Class<?> clazz() default Object.class;

    Class<?>[] params() default {};

}
