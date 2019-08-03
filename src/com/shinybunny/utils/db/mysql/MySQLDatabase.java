package com.shinybunny.utils.db.mysql;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.ListUtils;
import com.shinybunny.utils.db.*;
import com.shinybunny.utils.db.SelectStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySQLDatabase extends Database<MySQLProvider> {

    private static final SQLCommand INSERT_COMMAND = SQLCommand.create("INSERT INTO {table} [({columns})] VALUES ({values})");
    private static final SQLCommand SELECT_COMMAND = SQLCommand.create("SELECT {columns} FROM {table} [WHERE {where}] [ORDER BY {order}] [LIMIT {limit}]");
    private static final SQLCommand DROP_TABLE_COMMAND = SQLCommand.create("DROP TABLE {table}");
    private static final SQLCommand CREATE_TABLE_COMMAND = SQLCommand.create("CREATE TABLE {name} ({columns}[, PRIMARY KEY({primaryKey})])");

    private final Connection connection;

    public MySQLDatabase(MySQLProvider provider, String name, Connection conn) {
        super(provider, name);
        this.connection = conn;
    }

    @Override
    public Table createTable(String name, SelectStatement select) {
        return null;
    }

    @Override
    public void insert(Table table, Map<String, Object> data) {
        List<String> columns = new ArrayList<>(data.keySet());
        PreparedStatement statement = INSERT_COMMAND.build()
                .set("table",table.getName())
                .set("columns",String.join(", ",columns))
                .set("values",String.join(", ",ListUtils.repeat("?",columns.size())))
                .prepare(connection,ListUtils.map(columns, data::get));
        try {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public QueryResult select(SelectStatement select) {
        List<Object> params = new ArrayList<>();
        String whereStr = whereString(select.getWhere(),params);
        PreparedStatement statement = SELECT_COMMAND.build()
                .set("columns",selectorString(select.getSelectors()))
                .set("table",select.getTable())
                .set("where",whereStr)
                .set("order",stringOrderBy(select.getOrderBy()))
                .set("limit",select.getLimit() == 0 ? null : select.getLimit())
                .prepare(connection,params);
        try {
            ResultSet set = statement.executeQuery();
            return new QueryResult(set);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String stringOrderBy(Map<String, Order> orderBy) {
        return "ORDER BY " + ListUtils.map(orderBy.entrySet(),MySQLDatabase::stringOrderEntry);
    }

    private static String stringOrderEntry(Map.Entry<String, Order> e) {
        return e.getKey() + (e.getValue() == Order.DEFAULT ? "" : " " + e.getValue().getLabel());
    }

    public static String selectorString(Array<ColumnSelector> selectors) {
        return String.join(", ",selectors.map(MySQLDatabase::selectorString));
    }

    public static String selectorString(ColumnSelector selector) {
        StringBuilder b = new StringBuilder();
        if (selector.getMinMax() != ColumnSelector.EnumMinMax.NONE) {
            b.append(selector.getMinMax()).append("(");
        }
        b.append(selector.getName());
        if (selector.getMinMax() != ColumnSelector.EnumMinMax.NONE) {
            b.append(')');
        }
        if (selector.getAlias() != null) {
            b.append(" AS ").append(escape(selector.getAlias()));
        }
        return b.toString();
    }

    private static String escape(String alias) {
        if (alias.contains(" ")) {
            return "[" + alias + "]";
        }
        return alias;
    }

    public static String whereString(Where where, List<Object> params) {
        if (where == null) return null;
        if (where instanceof Comparison) {
            params.add(((Comparison) where).getValue());
            return ((Comparison) where).getColumn() + " = ?";
        }
        return "(" + whereString(where.getFirst(),params) + ") " + where.getBoolOperator() + " (" + whereString(where.getSecond(),params) + ")";
    }

    @Override
    public void removeTable(Table table) {
        DROP_TABLE_COMMAND.build().set("table",table).execute(connection);
    }

    @Override
    public void update(Table table, Map<String, Object> data, Where where) {

    }

    @Override
    public void createTable(Table table) {
        CREATE_TABLE_COMMAND.build()
                .set("name",table.getName())
                .set("columns",table.getColumns().map(MySQLDatabase::columnString).join(", "))
                .set("primaryKey",table.getPrimaryKey());
    }

    public static String columnString(Column column) {
        return column.getName() + " " + column.getType() +
                (column.doesAutoIncrement() ? " AUTO_INCREMENT" : "") +
                (column.isNullable() ? "" : " NOT NULL") +
                (column.getDefaultValue() == null ? "" : " " + DatabaseUtils.toString(column.getDefaultValue())) +
                (column.isUnique() ? " UNIQUE" : "");
    }

    @Override
    public void addColumn(Table table, Column column) {

    }

    @Override
    public void removeColumn(Table table, Column column) {

    }
}
