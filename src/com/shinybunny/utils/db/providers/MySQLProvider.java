package com.shinybunny.utils.db.providers;

import com.mysql.cj.jdbc.Driver;
import com.shinybunny.utils.Array;
import com.shinybunny.utils.db.Database;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLProvider extends DatabaseProvider {

    private String host;
    private String username;
    private String password;
    private int port;

    private Array<Database> databases;

    public MySQLProvider(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.databases = new Array<>();
    }

    @Override
    public void connect() {
        try {
            DriverManager.registerDriver(new Driver());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MySQLDatabase getDatabase(String name) {
        return new MySQLDatabase(this,name);
    }

}
