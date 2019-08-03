package com.shinybunny.utils.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used for deserialization of fields in a database model class.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Deserializer {

    /**
     * Deserialize using a static method from the field's class, or from another class defined in {@link #clazz()}.
     */
    String staticMethod() default "";

    /**
     * Defines the class to get the deserializer from. Defaults to the field's type class.
     */
    Class<?> clazz() default Object.class;



}
