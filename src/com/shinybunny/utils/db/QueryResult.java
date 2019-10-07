package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryResult {

    private Array<ResultRow> rows;
    private int index;

    public QueryResult(Iterable<ResultRow> rows) {
        this.rows = new Array<>(rows);
    }

    public Array<ResultRow> getRows() {
        return rows;
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public ResultRow next() {
        return rows.get(index++);
    }

    public ResultRow first() {
        return isEmpty() ? null : rows.get(0);
    }
}
