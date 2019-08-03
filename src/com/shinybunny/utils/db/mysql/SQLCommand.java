package com.shinybunny.utils.db.mysql;

import com.shinybunny.utils.Array;
import com.shinybunny.utils.db.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLCommand {

    private static final Pattern ARG_PATTERN = Pattern.compile("\\{(\\w+)}");

    private String command;
    private Array<Token> tokens;

    private SQLCommand(String cmd, Array<Token> tokens) {
        this.command = cmd;
        this.tokens = tokens;
    }

    public static SQLCommand create(String cmd) {
        Array<Token> tokens = new Array<>();
        Matcher m = ARG_PATTERN.matcher(cmd);
        while (m.find()) {
            String p = m.group(1);
            int startBr = cmd.lastIndexOf('[',m.start());
            int endBr = cmd.lastIndexOf(']',m.start());
            int startBrAfter = cmd.indexOf('[',m.end());
            int endBrAfter = cmd.indexOf(']',m.end());
            if (startBr > -1 && endBrAfter > -1 && endBr < startBr && (startBrAfter > endBrAfter || startBrAfter == -1)) {
                tokens.add(new Token(p,false));
            } else {
                tokens.add(new Token(p,true));
            }
        }
        return new SQLCommand(cmd,tokens);
    }

    public SQLCommand.Builder build() {
        return new Builder(this);
    }

    private static class Token {

        private String name;
        private boolean required;

        public Token(String name, boolean required) {
            this.name = name;
            this.required = required;
        }

        public String applyTo(String str, String value) {
            Matcher m = Pattern.compile("\\{" + name + "}").matcher(str);
            if (!m.find()) {
                throw new IllegalStateException("Command param " + name + " already set or doesn't exist in the command string!");
            }
            if (value != null) {
                str = m.replaceFirst(value);
                if (required) return str;
            }
            int openBr = str.lastIndexOf('[',m.start());
            int closeBr = str.indexOf(']',m.end());
            if (openBr < 0 || closeBr < 0) {
                throw new IllegalStateException("Open/Close brackets not found for optional region param " + name);
            }
            StringBuilder b = new StringBuilder(str);
            if (value == null) {
                b.delete(openBr,closeBr+1);
            } else {
                b.deleteCharAt(openBr).deleteCharAt(closeBr);
            }
            return b.toString();
        }
    }

    public static class Builder {

        private final SQLCommand cmd;
        private String string;

        public Builder(SQLCommand cmd) {
            this.cmd = cmd;
            this.string = cmd.command;
        }

        public Builder set(String param, Object value) {
            Token token = cmd.tokens.find(t->t.name != null && t.name.equalsIgnoreCase(param));
            if (token == null) {
                throw new IllegalArgumentException("Unknown parameter name in SQL command: " + param);
            }
            string = token.applyTo(string, DatabaseUtils.toString(value));
            return this;
        }

        @Override
        public String toString() {
            return string;
        }

        public PreparedStatement prepare(Connection conn) {
            try {
                return conn.prepareStatement(string);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        public PreparedStatement prepare(Connection conn, List<?> replaceArgs) {
            PreparedStatement p = prepare(conn);
            for (int i = 0; i < replaceArgs.size(); i++) {
                try {
                    p.setObject(i+1,replaceArgs.get(i));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return p;
        }

        public void execute(Connection connection) {
            try {
                Statement s = connection.createStatement();
                s.execute(string);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}
