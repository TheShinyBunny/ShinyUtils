package com.shinybunny.utils.db;

public class Where {

    public static final Where ANYTHING = new Where("");
    protected String str;

    public Where(String s) {
        this.str = s;
    }

    public static Chain chain() {
        return new Chain();
    }

    @Override
    public String toString() {
        return str;
    }

    public static Where and(Where w1, Where w2) {
        return merge(w1,BoolOperator.AND,w2);
    }

    public static Where or(Where w1, Where w2) {
        return merge(w1,BoolOperator.OR,w2);
    }

    private static Where merge(Where w1, BoolOperator operator, Where w2) {
        return new Where("(" + w1 + ") " + operator + " (" + w2 + ")");
    }

    public static Where test(String column, Operator op, Object value) {
        return new Where(op.toString(column,value));
    }

    public static Where from(String s) {
        return new Where(s);
    }

    public static class Chain extends Where {

        private Chain next;
        private Chain cursor = this;
        private Where value;
        private BoolOperator bop;

        public Chain() {
            super("");
        }

        public Chain and(Where value) {
            return concat(value,BoolOperator.AND);
        }

        public Chain or(Where value) {
            return concat(value,BoolOperator.OR);
        }

        private Chain concat(Where value, BoolOperator op) {
            cursor.bop = op;
            cursor.value = value;
            Chain c = new Chain();
            cursor.next = c;
            cursor = c;
            return this;
        }

        @Override
        public String toString() {
            if (str.isEmpty()) {
                str = build().toString();
            }
            return str;
        }

        public Where build() {
            if (next == null) return value;
            return Where.merge(value,next.bop,next.build());
        }
    }


}
