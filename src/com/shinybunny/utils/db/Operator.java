package com.shinybunny.utils.db;

public enum Operator {
    LESS("<"),
    GREATER(">"),
    LESS_EQUAL("<="),
    GREATER_EQUAL(">="),
    EQUALS("="),
    NOT_EQUALS("<>"),
    STARTS_WITH("%s LIKE '%s\\%'",true),
    ENDS_WITH("%s LIKE '\\%%s'"),
    CONTAINS("%s LIKE '\\%%s\\%'"),
    LIKE("LIKE");

    private final String sign;
    private final boolean format;

    Operator(String sign, boolean format) {
        this.sign = sign;
        this.format = format;
    }

    Operator(String sign) {
        this(sign,false);
    }

    public String toString(String column, Object value) {
        if (format) {
            return String.format(sign,column, DatabaseUtils.toString(value));
        } else {
            return String.format("%s %s %s",column,sign, DatabaseUtils.toString(value));
        }
    }
}
