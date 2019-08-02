package com.shinybunny.utils.db;

public class Selectors {

    public static ColumnSelector column(String name) {
        return new ColumnSelector(name, ColumnSelector.EnumMinMax.NONE, null);
    }

    public static ColumnSelector min(String column) {
        return new ColumnSelector(column, ColumnSelector.EnumMinMax.MIN, null);
    }

    public static ColumnSelector max(String column) {
        return new ColumnSelector(column, ColumnSelector.EnumMinMax.MAX, null);
    }
}
