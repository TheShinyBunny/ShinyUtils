package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;

public abstract class DatabaseProvider {

    protected Array<Database> databases = new Array<>();

    public DatabaseProvider() {
        DatabaseManager.addProvider(this);
    }

    public abstract void connect();

    /**
     * Acquires a database from this provider. Can create it if it doesn't exist.
     * @param name The database name
     * @return A {@link Database} instance
     */
    public final Database getDatabase(String name) {
        if (addNewDatabases()) {
            return databases.findOrAdd(d -> d.name.equals(name), () -> getNewDatabase(name));
        }
        return databases.findOrUse(d->d.name.equals(name),()->getNewDatabase(name));
    }

    /**
     * Acquires a new database from this provider.
     * This is called when calling {@link #getDatabase(String)} and there is no database with that name in {@link #databases}.
     * Will automatically add the returned Database to {@link #databases}. To disable that, override {@link #addNewDatabases()}.
     * @param name The database name
     * @return A new {@link Database} instance.
     */
    protected abstract Database getNewDatabase(String name);

    /**
     * Whether to add new databases returned from {@link #getNewDatabase(String)} to {@link #databases}.
     * @return True by default.
     */
    protected boolean addNewDatabases() {
        return true;
    }

    public Model getModel(Class<?> modelClass) {
        for (Database db : databases) {
            Model m = db.getModel(modelClass);
            if (m != null) return m;
        }
        return null;
    }
}
