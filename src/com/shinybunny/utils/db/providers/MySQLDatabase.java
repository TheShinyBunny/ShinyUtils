package com.shinybunny.utils.db.providers;

import com.shinybunny.utils.ListUtils;
import com.shinybunny.utils.db.*;
import com.shinybunny.utils.db.SelectStatement;

import java.util.Map;

public class MySQLDatabase extends Database<MySQLProvider> {
    public MySQLDatabase(MySQLProvider provider, String name) {
        super(provider, name);
    }

    @Override
    public Table createTable(String name, SelectStatement select) {
        return null;
    }

    @Override
    public void insert(Table table, Map<String, Object> data) {
        String statement = "INSERT INTO " + this + " (" + String.join(", ",data.keySet()) + ") VALUES (" + String.join(", ", ListUtils.convertAll(data.values(), DatabaseUtils::toString)) + ")";
    }

    @Override
    public QueryResult select(SelectStatement select) {
        return null;
    }

    @Override
    public void removeTable(Table table) {

    }

    @Override
    public void update(Table table, Map<String, Object> data, Where where) {

    }

    @Override
    public void createTable(Table table) {

    }
}
