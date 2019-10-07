package com.shinybunny.utils.db.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.shinybunny.utils.db.Database;
import com.shinybunny.utils.db.DatabaseProvider;

public class MongoProvider extends DatabaseProvider {

    private final MongoClientURI uri;
    private MongoClient client;

    public MongoProvider(MongoClientURI uri) {
        this.uri = uri;
    }

    @Override
    public void connect() {
        this.client = new MongoClient(uri);
    }

    /**
     * Acquires a database from this provider. Can create it if it doesn't exist.
     *
     * @param name The database name
     * @return A {@link Database} instance
     */
    @Override
    public Database getNewDatabase(String name) {
        return new MongoDatabase(this,name);
    }

    public MongoClient getClient() {
        return client;
    }
}
