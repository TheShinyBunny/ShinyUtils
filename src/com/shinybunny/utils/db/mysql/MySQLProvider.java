package com.shinybunny.utils.db.mysql;

import com.mysql.cj.jdbc.Driver;
import com.shinybunny.utils.db.DatabaseProvider;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLProvider extends DatabaseProvider {

    public static final String URL_FORMAT = "jdbc:mysql://%s:%d/%s";
    private String host;
    private String username;
    private String password;
    private int port;

    public MySQLProvider(String host, String username, String password, int port) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
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
        try {
            Connection conn = DriverManager.getConnection(String.format(URL_FORMAT, host, port, name),username,password);
            return new MySQLDatabase(this,name,conn);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
