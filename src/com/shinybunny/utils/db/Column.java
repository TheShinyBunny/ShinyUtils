package com.shinybunny.utils.db;

import com.shinybunny.utils.Name;

import java.lang.reflect.Field;

public class Column {

    private String name;
    private DataType<?> type;
    private boolean autoIncrement;
    private boolean nullable;
    private boolean primaryKey;
    private Object defaultValue;
    private boolean unique;

    public Column(String name, DataType<?> type) {
        this.name = name;
        this.type = type;
        nullable = true;
    }

    public static Column of(String name, DataType<?> type) {
        return new Column(name,type);
    }

    public static Column fromField(Database db, Field f) {
        Column c = Column.of(Name.Helper.getName(f),DataType.of(db,f.getType()));
        if (f.isAnnotationPresent(AutoIncrement.class)) c.autoIncrement();
        if (f.isAnnotationPresent(NotNull.class)) c.notNull();
        if (f.isAnnotationPresent(PrimaryKey.class)) c.primaryKey();
        if (f.isAnnotationPresent(Unique.class)) c.unique();
        return c;
    }

    public String getName() {
        return name;
    }

    public DataType<?> getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public Column notNull() {
        nullable = false;
        return this;
    }

    public Column primaryKey() {
        primaryKey = true;
        return this;
    }

    public Column autoIncrement() {
        autoIncrement = true;
        return this;
    }

    public Column unique() {
        unique = true;
        return this;
    }


    public Column defaultValue(Object def) {
        this.defaultValue = def;
        return this;
    }

    void setType(DataType<?> type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " " + type +
                (autoIncrement ? " AUTO_INCREMENT" : "") +
                (nullable ? "" : " NOT NULL") +
                (defaultValue == null ? "" : " " + DatabaseUtils.toString(defaultValue)) +
                (unique ? " UNIQUE" : "");
    }

    public boolean doesAutoIncrement() {
        return autoIncrement;
    }
}
