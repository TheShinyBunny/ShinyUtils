package com.shinybunny.utils.db;

import java.util.Map;
import java.util.Set;

public class ResultRow {

    private Map<String,Object> data;

    public ResultRow(Map<String,Object> data) {
        this.data = data;
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
