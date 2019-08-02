package com.shinybunny.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface Name {

    String value();

    class Helper {

        public static String getName(Field f) {
            Name n = f.getAnnotation(Name.class);
            if (n == null) return f.getName();
            return n.value();
        }

        public static String getName(Parameter p) {
            Name n = p.getAnnotation(Name.class);
            if (n == null) return p.getName();
            return n.value();
        }

    }

}
