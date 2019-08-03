package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.db.Order;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OrderBy {

    String value();

    Order dir() default Order.DEFAULT;



}
