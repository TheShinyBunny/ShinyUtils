package com.shinybunny.utils.db;

public class Comparison extends Where {

    private final String column;
    private final Operator operator;
    private final Object value;

    public Comparison(String column, Operator op, Object value) {
        super();
        this.column = column;
        this.operator = op;
        this.value = value;
    }

    public String getColumn() {
        return column;
    }

    public Object getValue() {
        return value;
    }

    public Operator getOperator() {
        return operator;
    }
}
