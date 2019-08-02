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

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        if (minMax != EnumMinMax.NONE) {
            b.append(minMax).append("(");
        }
        b.append(name);
        if (minMax != EnumMinMax.NONE) {
            b.append(')');
        }
        if (alias != null) {
            b.append(" AS ").append(escape(alias));
        }
        return b.toString();
    }

    private static String escape(String alias) {
        if (alias.contains(" ")) {
            return "[" + alias + "]";
        }
        return alias;
    }

    public enum EnumMinMax {

        MIN, MAX, NONE;

    }
}
