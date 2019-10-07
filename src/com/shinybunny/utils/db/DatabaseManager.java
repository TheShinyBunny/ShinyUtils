package com.shinybunny.utils.db;

import com.mongodb.MongoClientURI;
import com.shinybunny.utils.Array;
import com.shinybunny.utils.db.mongo.MongoProvider;

public class DatabaseManager {


    private static Array<DatabaseProvider> providers = new Array<>();

    public static Model getGlobalModel(Class<?> modelClass) {
        for (DatabaseProvider p : providers) {
            Model m = p.getModel(modelClass);
            if (m != null) return m;
        }
        return null;
    }

    public static void addProvider(DatabaseProvider provider) {
        providers.add(provider);
    }

    public static DatabaseProvider mongoDB(String uri) {
        return new MongoProvider(new MongoClientURI(uri));
    }
}
