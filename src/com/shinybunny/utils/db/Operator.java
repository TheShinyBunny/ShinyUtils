package com.shinybunny.utils.db;

public enum Operator {
    LESS("<"),
    GREATER(">"),
    LESS_EQUAL("<="),
    GREATER_EQUAL(">="),
    EQUALS("="),
    NOT_EQUALS("<>"),
    STARTS_WITH("%s LIKE '%s\\%'",true),
    ENDS_WITH("%s LIKE '\\%%s'",true),
    CONTAINS("%s LIKE '\\%%s\\%'",true),
    LIKE("LIKE");

    private final String mySqlSign;
    private final boolean format;

    Operator(String mySqlSign, boolean format) {
        this.mySqlSign = mySqlSign;
        this.format = format;
    }

    Operator(String sign) {
        this(sign,false);
    }

    public String toMySQLString(String column, Object value) {
        if (format) {
            return String.format(mySqlSign,column, DatabaseUtils.toString(value));
        } else {
            return String.format("%s %s %s",column,mySqlSign, DatabaseUtils.toString(value));
        }
    }

    public String toRegex(Object value) {
        if (format) {
            switch (this) {
                case CONTAINS:
                    return ".*" + value + ".*";
                case STARTS_WITH:
                    return "^" + value + ".*";
                case ENDS_WITH:
                    return ".*" + value + "$";
                case LIKE:
                    return value.toString();
            }
        }
        return null;
    }
}
