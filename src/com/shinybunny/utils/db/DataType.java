package com.shinybunny.utils.db;

import com.shinybunny.utils.ListUtils;

import java.sql.*;
import java.time.Year;

public class DataType<T> {

    private final String name;
    private Class<? extends T> type;
    private DataGetter<T> getter;
    private final Object[] params;

    public DataType(String name, Class<T> type, Object... params) {
        this(name,(res, col) -> res.get(col,type),params);
        this.type = type;
    }

    public DataType(String name, DataGetter<T> getter, Object... params) {
        this.name = name;
        this.getter = getter;
        this.params = params;
    }

    private DataType<T> setType(Class<? extends T> type) {
        this.type = type;
        return this;
    }

    public static DataType<String> CHAR(int size) {
        return new DataType<>("CHAR",String.class,size);
    }

    public static DataType<String> VAR_CHAR(int size) {
        return new DataType<>("VARCHAR",String.class,size);
    }

    public static DataType<String> BINARY(byte size) {
        return new DataType<>("BINARY",String.class,size);
    }

    public static DataType<String> VAR_BINARY(byte size) {
        return new DataType<>("VARBINARY",String.class,size);
    }

    public static final DataType<String> TINY_TEXT = new DataType<>("TINYTEXT",String.class);
    public static final DataType<String> MEDIUM_TEXT = new DataType<>("MEDIUMTEXT",String.class);
    public static final DataType<String> LONG_TEXT = new DataType<>("LONGTEXT",String.class);

    public static DataType<String> TEXT(int size) {
        return new DataType<>("TEXT",String.class,size);
    }

    public static DataType<Byte> BIT(int size) {
        return new DataType<>("BIT", Byte.TYPE, size);
    }

    public static DataType<Integer> TINY_INT(int size) {
        return new DataType<>("TINYINT",Integer.TYPE, size);
    }

    public static final DataType<Boolean> BOOLEAN = new DataType<>("BOOL",Boolean.TYPE);

    public static DataType<Integer> SMALL_INT(int size) {
        return new DataType<>("SMALLINT",Integer.TYPE,size);
    }

    public static DataType<Integer> MEDIUM_INT(int size) {
        return new DataType<>("MEDIUMINT",Integer.TYPE,size);
    }

    public static DataType<Integer> INT(int size) {
        return new DataType<>("INT",Integer.TYPE,size);
    }

    public static DataType<Integer> BIG_INT(int size) {
        return new DataType<>("BIGINT",Integer.TYPE, size);
    }

    public static DataType<Float> FLOAT(int p) {
        return new DataType<>("FLOAT",Float.TYPE, p);
    }

    public static DataType<Double> DOUBLE(int size, int d) {
        return new DataType<>("DOUBLE",Double.TYPE, size,d);
    }

    public static final DataType<Date> DATE = new DataType<>("DATE",Date.class);

    public static final DataType<Timestamp> DATETIME = new DataType<>("DATETIME",Timestamp.class);

    public static final DataType<Timestamp> TIMESTAMP = new DataType<>("TIMESTAMP",Timestamp.class);

    public static final DataType<Time> TIME = new DataType<>("TIME",Time.class);

    public static final DataType<Year> YEAR = new DataType<>("YEAR",Year.class);

    public static DataType<?> of(Database db, Class<?> type) {
        if (type == Integer.TYPE) return INT(Integer.SIZE);
        if (type == Double.TYPE) return DOUBLE(Double.SIZE,2);
        if (type == Float.TYPE) return FLOAT(Float.SIZE);
        if (type == Byte.TYPE) return BIT(Byte.SIZE);
        if (type == String.class) return VAR_CHAR(767);
        if (type == Boolean.TYPE) return BOOLEAN;
        if (type == Date.class) return DATE;
        if (type == Timestamp.class) return TIMESTAMP;
        if (type == Time.class) return TIME;
        if (type == Year.class) return YEAR;
        return null; // TODO: 03/08/2019 add custom serializer type
    }
    public Column named(String name) {
        return new Column(name,this);
    }

    public T get(ResultRow res, String col) throws SQLException {
        return getter.get(res,col);
    }

    public Class<? extends T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + (params.length > 0 ? "(" + String.join(", ",ListUtils.mapArray(params, Object::toString)) + ")" : "");
    }

    @FunctionalInterface
    private interface DataGetter<T> {

        T get(ResultRow res, String col) throws SQLException;

    }
}
