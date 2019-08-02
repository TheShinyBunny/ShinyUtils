package com.shinybunny.utils.db;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OrderBy {

    String value();

    Order dir() default Order.DEFAULT;



}
