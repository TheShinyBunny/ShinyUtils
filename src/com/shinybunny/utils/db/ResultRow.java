package com.shinybunny.utils.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultRow {

    private Map<String,Object> data;

    public ResultRow(ResultSet set) {
        this.data = new HashMap<>();
        try {
            for (int i = 1; i <= set.getMetaData().getColumnCount(); i++) {
                String name = set.getMetaData().getColumnName(i);
                data.put(name,set.getObject(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public <T> T get(String column, Class<T> type) {
        return type.cast(data.get(column));
    }

    public Object get(String column) {
        return data.get(column);
    }

    public boolean contains(String column) {
        return data.containsKey(column);
    }

    public Set<String> keys() {
        return data.keySet();
    }
}
