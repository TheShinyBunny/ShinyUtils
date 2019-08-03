package com.shinybunny.utils.db;

import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.Name;
import com.shinybunny.utils.db.annotations.DataModel;
import com.shinybunny.utils.db.annotations.PrimaryKey;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUtils {

    public static final ExceptionFactory EXCEPTION_QUERY = ExceptionFactory.make("Failed to execute query","query");
    public static final ExceptionFactory EXCEPTION_EXECUTE = ExceptionFactory.make("Failed to execute statement","statement");

    public static String toString(Object value) {
        if (value instanceof String) return "\"" + value + "\"";
        return String.valueOf(value);
    }

    public static Map<String, Object> mapModel(Object model, Table table) {
        Class<?> type = model.getClass();
        Map<String,Object> map = new HashMap<>();
        for (Column c : table.getColumns()) {
            if (c.doesAutoIncrement()) continue;
            String name = c.getName();
            Field f = null;
            try {
                f = type.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                for (Field f2 : DataModel.Helper.getValidFields(type)) {
                    String name2 = Name.Helper.getName(f2);
                    if (name2.equals(name)) {
                        f = f2;
                    }
                }
            }
            if (f == null) {
                if (c.isNullable()) continue;
                // TODO: 01/06/2019 missing value
            } else {
                try {
                    f.setAccessible(true);
                    map.put(name,f.get(model));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
    }

    public static Field getPrimaryKey(Class<?> modelClass) {
        for (Field f : DataModel.Helper.getValidFields(modelClass)) {
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                return f;
            }
        }
        return null;
    }

    public static Object getPrimaryKey(Object model) {
        for (Field f : DataModel.Helper.getValidFields(model.getClass())) {
            if (f.isAnnotationPresent(PrimaryKey.class)) {
                try {
                    f.setAccessible(true);
                    return f.get(model);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
