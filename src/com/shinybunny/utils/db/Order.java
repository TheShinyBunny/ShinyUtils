package com.shinybunny.utils.db;

public enum Order {
    DEFAULT(""),
    ASCENDING("ASC"),
    DESCENDING("DESC");

    private String label;

    Order(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
