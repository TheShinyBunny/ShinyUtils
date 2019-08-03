package com.shinybunny.utils.db;

public class Where {


    private Where first;
    private BoolOperator operator;
    private Where second;

    public Where(Where first, BoolOperator operator, Where second) {
        this.first = first;
        this.operator = operator;
        this.second = second;
    }

    protected Where() {
    }

    public static Chain chain() {
        return new Chain();
    }

    public Where getFirst() {
        return first;
    }

    public Where getSecond() {
        return second;
    }

    public BoolOperator getBoolOperator() {
        return operator;
    }

    public static Where and(Where w1, Where w2) {
        return merge(w1,BoolOperator.AND,w2);
    }

    public static Where or(Where w1, Where w2) {
        return merge(w1,BoolOperator.OR,w2);
    }

    private static Where merge(Where w1, BoolOperator operator, Where w2) {
        return new Where(w1,operator,w2);
    }

    public static Where equals(String column, Object value) {
        return test(column,Operator.EQUALS,value);
    }

    public static Where test(String column, Operator op, Object value) {
        return new Comparison(column,op,value);
    }

    public static class Chain {

        private Chain next;
        private Chain cursor = this;
        private Where value;
        private BoolOperator bop;

        public Chain() {
            super();
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

        public Where build() {
            if (next == null) return value;
            return Where.merge(value,next.bop,next.build());
        }
    }


}
