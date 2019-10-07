package com.shinybunny.utils.db.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.shinybunny.utils.ExceptionFactory;
import com.shinybunny.utils.db.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MongoDatabase extends Database {

    private static final ExceptionFactory UNSUPPORTED_OPERATION = ExceptionFactory.make("${op} is an unsupported operation in a Mongo Database.");
    private com.mongodb.client.MongoDatabase handle;

    public MongoDatabase(MongoProvider provider, String name) {
        super(provider, name);
        this.handle = provider.getClient().getDatabase(name);
    }

    @Override
    public Table createTable(String name, SelectStatement select) {
        return null;
    }

    @Override
    public void insert(Table table, Map<String, Object> data) {

    }

    @Override
    public QueryResult select(SelectStatement select) {
        MongoCollection<Document> collection = handle.getCollection(select.getTable().getName());
        FindIterable<Document> iter = collection.find(toBsonFilter(select.getWhere()));
        if (select.getLimit() > 0) {
            iter.limit(select.getLimit());
        }
        List<String> ascending = new ArrayList<>();
        List<String> descending = new ArrayList<>();
        for (Map.Entry<String, Order> e : select.getOrderBy().entrySet()) {
            if (e.getValue() == Order.DESCENDING) {
                descending.add(e.getKey());
            } else {
                ascending.add(e.getKey());
            }
        }
        if (!ascending.isEmpty() || !descending.isEmpty()) {
            iter.sort(Sorts.orderBy(Sorts.ascending(ascending),Sorts.descending(descending)));
        }
        return new QueryResult(iter.map(ResultRow::new));
    }

    private Bson toBsonFilter(Where where) {
        if (where instanceof Comparison) {
            Comparison c = (Comparison)where;
            Operator op = c.getOperator();
            String column = c.getColumn();
            Object value = c.getValue();
            switch (op) {
                case EQUALS:
                    return Filters.eq(column,value);
                case GREATER:
                    return Filters.gt(column,value);
                case GREATER_EQUAL:
                    return Filters.gte(column,value);
                case LESS:
                    return Filters.lt(column,value);
                case LESS_EQUAL:
                    return Filters.lte(column,value);
                case NOT_EQUALS:
                    return Filters.ne(column,value);
                case LIKE:
                case ENDS_WITH:
                case STARTS_WITH:
                case CONTAINS:
                    String regex = op.toRegex(value);
                    assert regex != null;
                    return Filters.regex(column,regex);
            }
        } else {
            switch (where.getBoolOperator()) {
                case OR:
                    return Filters.or(toBsonFilter(where.getFirst()),toBsonFilter(where.getSecond()));
                case AND:
                    return Filters.and(toBsonFilter(where.getFirst()),toBsonFilter(where.getSecond()));
            }
        }
        return null;
    }

    @Override
    public void removeTable(Table table) {
        handle.getCollection(table.getName()).deleteMany(new BsonDocument());
    }

    @Override
    public void update(Table table, Map<String, Object> data, Where where) {

    }

    @Override
    public void createTable(Table table) {
        handle.createCollection(table.getName());
    }

    @Override
    public void addColumn(Table table, Column column) {

    }

    @Override
    public void removeColumn(Table table, Column column) {

    }
}
