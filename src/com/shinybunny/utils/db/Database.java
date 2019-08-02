package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.db.providers.DatabaseProvider;

import java.util.Map;

public abstract class Database<P extends DatabaseProvider> {

    private P provider;
    public final String name;
    private Array<Table> tables;

    public Database(P provider, String name) {
        this.provider = provider;
        this.name = name;
        this.tables = new Array<>();
    }

    public P getProvider() {
        return provider;
    }

    public String getName() {
        return name;
    }

    public Array<Table> getTables() {
        return tables;
    }

    public Table.Builder createTable() {
        return new Table.Builder(this);
    }

    public abstract Table createTable(String name, SelectStatement select);

    public abstract void insert(Table table, Map<String, Object> data);

    public abstract QueryResult select(SelectStatement select);

    public abstract void removeTable(Table table);

    public abstract void update(Table table, Map<String, Object> data, Where where);

    public abstract void createTable(Table table);
}
