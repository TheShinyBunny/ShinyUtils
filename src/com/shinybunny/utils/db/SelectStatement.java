package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.ListUtils;

import java.util.HashMap;
import java.util.Map;

public class SelectStatement {

    private Table table;
    private Array<ColumnSelector> selectors;
    private Where where;
    private Map<String, Order> orderBy;
    private int limit;

    public SelectStatement(Table table) {
        this.table = table;
        this.selectors = new Array<>();
        orderBy = new HashMap<>();
    }

    public Table getTable() {
        return table;
    }

    public SelectStatement orderBy(String column, Order by) {
        orderBy.put(column,by);
        return this;
    }

    public SelectStatement columns(String... columns) {
        this.selectors.addAll(ListUtils.mapArray(columns, Selectors::column));
        return this;
    }

    public SelectStatement field(ColumnSelector selector) {
        this.selectors.add(selector);
        return this;
    }

    public SelectStatement where(Where where) {
        this.where = where;
        return this;
    }

    public void limit(int limit) {
        this.limit = limit;
    }

    public Array<ColumnSelector> getSelectors() {
        return selectors;
    }

    public int getLimit() {
        return limit;
    }

    public Map<String, Order> getOrderBy() {
        return orderBy;
    }

    public Where getWhere() {
        return where;
    }

    public QueryResult execute() {
        return table.getDatabase().select(this);
    }
}
