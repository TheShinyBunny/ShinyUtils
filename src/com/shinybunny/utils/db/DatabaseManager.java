package com.shinybunny.utils.db;

import com.shinybunny.utils.Array;

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
}
