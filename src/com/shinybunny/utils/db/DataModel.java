package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.Ignore;
import com.shinybunny.utils.Name;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataModel {

    String value() default "classname";

    boolean pluralize() default true;

    class Helper {
        public static Object init(Class<?> type, Table table, QueryResult data) {
            try {
                if (!data.isEmpty()) {
                    ResultRow row = data.next();
                    Object instance = type.newInstance();
                    for (Field f : getValidFields(type)) {
                        Column col = table.getColumn(Name.Helper.getName(f));
                        f.set(instance,col.getType().get(data.next(),col.getName()));
                    }
                    return instance;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static Array<Field> getValidFields(Class<?> type) {
            Array<Field> arr = new Array<>();
            for (Field f : type.getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers()) && !f.isAnnotationPresent(Ignore.class)) {
                    arr.add(f);
                }
            }
            return arr;
        }
    }
}
