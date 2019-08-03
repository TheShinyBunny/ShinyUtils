package com.shinybunny.utils.db;

import java.lang.reflect.Field;

public interface ModelHelper {

    default void update() {
        Model.updateModel(this);
    }

    Object serialize(Field field, Object value);

    Object deserialize(Field field, Object value);

}
