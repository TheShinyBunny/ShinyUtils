package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public abstract class Database<P extends DatabaseProvider> {

    private P provider;
    public final String name;
    private Array<Table> tables;
    private Map<Class<?>, Model> models;

    public Database(P provider, String name) {
        this.provider = provider;
        this.name = name;
        this.tables = new Array<>();
        this.models = new HashMap<>();
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

    public abstract void addColumn(Table table, Column column);

    public abstract void removeColumn(Table table, Column column);

    public Model registerModel(Class<?> modelClass) {
        Model m = new Model(modelClass,this);
        models.put(modelClass,m);
        return m;
    }

    public <D> D createDal(Class<D> dalClass) {
        return (D) Proxy.newProxyInstance(dalClass.getClassLoader(),new Class[]{dalClass},new Dal(this,dalClass));
    }

    public Model getModel(Class<?> cls) {
        return models.get(cls);
    }


}
