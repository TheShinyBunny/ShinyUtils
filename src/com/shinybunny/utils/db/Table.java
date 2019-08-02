package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Table {

    private Database db;
    private String name;
    private Array<Column> columns;
    private Column primaryKey;

    public Table(Database db, String name, Builder builder) {
        this.db = db;
        this.name = name;
        columns = builder.cols;
        primaryKey = builder.primaryKey;
    }

    public Table(Database db, String name, SelectStatement as) {
        this.db = db;
        this.name = name;
        this.columns = as.getTable().columns;
        this.primaryKey = as.getTable().primaryKey;
    }

    public Array<Column> getColumns() {
        return columns;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Database getDatabase() {
        return db;
    }

    private Table create() {
        db.createTable(this);
        return this;
    }

    public void insert(Map<String,Object> data) {
        db.insert(this,data);
    }

    public void delete(Where where) {

    }

    public void addCheck(String name, Where condition) {

    }

    public void removeCheck(String name) {

    }

    public void remove() {
        db.removeTable(this);
    }

    public void addColumn(Column column) {

    }

    public void removeColumn(String name) {

    }

    public void setDataType(Column col, DataType<?> type) {
        col.setType(type);
    }

    public Column getPrimaryKey() {
        return primaryKey;
    }

    public Column getColumn(String name) {
        return columns.find(c->c.getName().equals(name));
    }

    public SelectStatement select() {
        return new SelectStatement(this);
    }

    public void update(Map<String, Object> data, Where where) {
        db.update(this,data,where);
    }

    public static class Builder {

        private final Database db;
        private Array<Column> cols = new Array<>();
        private Column primaryKey;
        private Map<String, Where> checks = new HashMap<>();

        public Builder(Database db) {
            this.db = db;
        }

        public Builder columns(Column... cols) {
            this.cols.addAll(cols);
            return this;
        }

        public Builder primaryKey(String colName) {
            this.primaryKey = cols.find(c->c.getName().equals(colName));
            return this;
        }

        public Builder check(String name, Where condition) {
            this.checks.put(name,condition);
            return this;
        }

        public Table fromSelect(String name, SelectStatement select) {
            return db.createTable(name,select);
        }

        public Table create(String name) {
            return new Table(db,name,this).create();
        }
    }


}
