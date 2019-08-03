package com.shinybunny.utils.db;

import com.shinybunny.utils.ListUtils;
import com.shinybunny.utils.MapUtils;
import com.shinybunny.utils.Name;
import com.shinybunny.utils.StringUtils;
import com.shinybunny.utils.db.annotations.DataModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {

    private Class<?> modelClass;
    private Table table;
    private Map<String, String> fieldMap;
    private String primaryKey;
    private Field primaryKeyField;
    private ModelConstructor constructor;

    public Model(Class<?> modelClass, Database db) {
        this.modelClass = modelClass;
        this.fieldMap = new HashMap<>();
        List<Column> cols = new ArrayList<>();
        for (Field f : modelClass.getDeclaredFields()) {
            fieldMap.put(f.getName(),Name.Helper.getName(f));
            cols.add(Column.fromField(db,f));
        }
        DataModel dm = modelClass.getAnnotation(DataModel.class);
        String name = "";
        if (dm == null) {
            name = modelClass.getSimpleName();
        } else {
            name = dm.value().equalsIgnoreCase("classname") ? modelClass.getSimpleName() : dm.value();
        }
        if (dm == null || dm.pluralize()) {
            name = StringUtils.pluralize(name);
        }
        this.table = db.createTable().columns(cols).create(name);
        this.constructor = ModelConstructor.createFor(modelClass);
    }

    public Table getTable() {
        return table;
    }

    public String getColumn(String alias) {
        return fieldMap.get(alias);
    }

    public Class<?> getModelClass() {
        return modelClass;
    }

    @Override
    public String toString() {
        return modelClass.toString();
    }

    /**
     * Updates the given model object. This is useful for adding a simple update() method to a model, to update it in the database.
     */
    public static void updateModel(Object model) {
        Model m = DatabaseManager.getGlobalModel(model.getClass());
        if (m != null) {
            m.update(model);
        }
    }

    public void update(Object model) {

    }

    public Object deserialize(QueryResult result, DataSetType resultType) {
        switch (resultType) {
            case SINGLE:
                ResultRow row = result.first();
                return deserialize(row);
            case COLLECTION:
                List<Object> list = new ArrayList<>();
                for (ResultRow r : result.getRows()) {
                    list.add(deserialize(r));
                }
                return ListUtils.cast(list,modelClass);
            case ARRAY:
                return ListUtils.map(result.getRows(),this::deserialize).toArray();
        }
        return null;
    }

    public Object deserialize(ResultRow row) {
        return constructor.newInstance(this,row);
    }

    public Field getField(String column) {
        String field = MapUtils.getKey(fieldMap,column);
        if (field == null) return null;
        try {
            return modelClass.getDeclaredField(field);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
