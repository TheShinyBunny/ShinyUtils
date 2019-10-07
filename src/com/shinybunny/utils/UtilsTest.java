package com.shinybunny.utils;

import com.shinybunny.utils.db.Database;
import com.shinybunny.utils.db.DatabaseManager;
import com.shinybunny.utils.db.DatabaseProvider;
import com.shinybunny.utils.db.Table;
import com.shinybunny.utils.db.annotations.Adapter;

import java.util.UUID;

public class UtilsTest {

    public static void main(String[] args) {
        DatabaseProvider provider = DatabaseManager.mongoDB("url");
        Database db = provider.getDatabase("MyDB");
        Table table = db.createTable().create("Users");
    }

    private static class User {

        @Adapter(serializer = @MethodRef("toString"),deserializer = @MethodRef(value = "getUUID",clazz = UtilsTest.class))
        private UUID id;
        private String name;
        private String password;
        private int age;


        public User(UUID id, String name, String password, int age) {
            this.id = id;
            this.name = name;
            this.password = password;
            this.age = age;
        }

        public User() {
        }
    }

}
