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
        this.selectors.addAll(ListUtils.convertAllArray(columns, Selectors::column));
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

    public QueryResult execute() {
        return table.getDatabase().query(toString());
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("SELECT ");
        if (selectors.isEmpty()) {
            b.append("*");
        } else {
            b.append(selectors.join(", "));
        }
        b.append(" FROM " + table);
        if (where != null) {
            String w = where.toString();
            if (!w.isEmpty()) b.append(" WHERE " + w);
        }
        if (limit != 0) {
            b.append(" LIMIT " + limit);
        }
        if (!orderBy.isEmpty()) {
            b.append(" ORDER BY");
            boolean comma = true;
            for (Map.Entry<String,Order> e : orderBy.entrySet()) {
                b.append(" " + e.getKey());
                if (e.getValue() != Order.ARBITRARY) {
                    b.append(" " + e.getValue());
                }
                if (comma) {
                    b.append(',');
                    comma = false;
                }
            }
        }
        return b.toString();
    }
}
