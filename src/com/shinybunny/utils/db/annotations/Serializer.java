package com.shinybunny.utils.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serializer {

    /**
     * The method to use in the field's instance to serialize it to a native data type.
     */
    String method() default "toString";



}
