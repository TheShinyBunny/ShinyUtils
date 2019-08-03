package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;

public abstract class DatabaseProvider {

    private Array<Database<?>> databases = new Array<>();

    public DatabaseProvider() {
        DatabaseManager.addProvider(this);
    }

    protected void addDatabase(Database<?> db) {
        databases.add(db);
    }

    public abstract void connect();

    public abstract Database getDatabase(String name);

    public Model getModel(Class<?> modelClass) {
        for (Database<?> db : databases) {
            Model m = db.getModel(modelClass);
            if (m != null) return m;
        }
        return null;
    }
}
