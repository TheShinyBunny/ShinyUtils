package com.shinybunny.utils.db;

public class ColumnSelector {

    private String name;
    private EnumMinMax minMax;
    private String alias;

    public ColumnSelector(String name, EnumMinMax minMax, String alias) {
        this.name = name;
        this.minMax = minMax;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public EnumMinMax getMinMax() {
        return minMax;
    }

    public String getAlias() {
        return alias;
    }

    public ColumnSelector as(String alias) {
        this.alias = alias;
        return this;
    }

    public enum EnumMinMax {

        MIN, MAX, NONE;

    }
}
