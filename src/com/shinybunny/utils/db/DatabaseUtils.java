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
}
