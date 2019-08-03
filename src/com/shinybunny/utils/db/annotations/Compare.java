package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.db.Operator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A constraint to compare a column to a given value.</br>
 * Assign to a parameter in a DAL method to compare a dynamic value to a field in the model, or use inside {@link Select} to compare a constant field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Compare {

    /**
     * The field name in the model to compare to.
     */
    String value();

    /**
     * The operator to compare with. Defaults to {@link Operator#EQUALS EQUALS}
     */
    Operator op() default Operator.EQUALS;

    /**
     * The constant string value to compare to. Used inside {@link Select}
     */
    String to() default "";

}
