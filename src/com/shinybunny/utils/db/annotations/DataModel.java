package com.shinybunny.utils.db.annotations;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.Ignore;
import com.shinybunny.utils.Name;
import com.shinybunny.utils.db.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A DataModel annotation is used on a class you wish to map to a table in the database.
 * Using a model class, you can define DALs to make it extra easy to insert items to the database.
 *
 * @see Dal
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DataModel {

    /**
     * The name of the table to create out of this data model. Defaults to the class's name.
     */
    String value() default "classname";

    /**
     * Whether the table name should be a pluralized version of the class name. Defaults to true.
     */
    boolean pluralize() default true;

}
