package com.shinybunny.utils.db.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface OrGroup {

    Compare[] value();

}
