package com.shinybunny.utils.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Select {

    int limit() default 0;

    OrderBy[] orderBy() default {};

    String min() default "";

    String max() default "";

    Class<?> model() default Object.class;

}
