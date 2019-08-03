package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.db.DalMethod;
import com.shinybunny.utils.db.Database;
import com.shinybunny.utils.db.QueryResult;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * Counts the number of returned items from a DAL query result
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Count {

    /**
     * Defines the class of the model table to target
     */
    Class<?> value();

    /**
     * The select query to use the count on
     */
    Select query() default @Select;

    class Handler extends DalMethod<Count> {

        private final Select.Handler select;

        public Handler(Database<?> database, Method method, Count annotation) {
            super(database, method, annotation);
            select = new Select.Handler(database,method,annotation.query());
            select.model = database.getModel(annotation.value());
            select.postInit();
        }

        @Override
        public Class<?> getModelClass() {
            return annotation.value();
        }

        @Override
        public void postInit() {
            if (!Number.class.isAssignableFrom(method.getReturnType())) {
                throw new IllegalStateException("Dal method annotated with @Count has a return type that is not a number!");
            }
        }

        @Override
        public Object invoke(Object[] args) {
            QueryResult result = select.getQueryResult(args);
            return result.getRows().length();
        }
    }


}
