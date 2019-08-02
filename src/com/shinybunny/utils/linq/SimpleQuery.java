package com.shinybunny.utils.linq;

import com.shinybunny.utils.Array;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleQuery<T> implements Query<T, Query> {
    private Array<T> data;

    public SimpleQuery(Iterable<T> elements) {
        this.data = new Array<>(elements);
    }

    @Override
    public Query filter(Predicate<T> filter) {
        data = data.filter(filter);
        return this;
    }

    @Override
    public Array<T> select() {
        return data;
    }

    @Override
    public Array<T> top(int count) {
        return data.slice(0,count);
    }

    @Override
    public T first() {
        return data.first();
    }

    @Override
    public T last() {
        return data.last();
    }

    @Override
    public Array<T> topPercent(int percent) {
        return data.slice(0,(percent * data.length()) / 100);
    }

    @Override
    public double max(Function<T, Double> numProperty) {
        double max = Double.MIN_VALUE;
        for (T t : data) {
            double d = numProperty.apply(t);
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    @Override
    public double min(Function<T, Double> numProperty) {
        double min = Double.MAX_VALUE;
        for (T t : data) {
            double d = numProperty.apply(t);
            if (d < min) {
                min = d;
            }
        }
        return min;
    }

    @Override
    public double average(Function<T, Double> avgProperty) {
        return 0;
    }

    @Override
    public int count() {
        return data.length();
    }

    @Override
    public double sum(Function<T, Double> numProperty) {
        return data.reduce((s,o)->s + numProperty.apply(o),0.0);
    }

    @Override
    public <R> Array<R> select(Function<T, R> mapper) {
        return data.map(mapper);
    }

    @Override
    public <R> Query<R, Query> map(Function<T, R> mapper) {
        return new SimpleQuery<>(data.map(mapper));
    }

    @Override
    public Query limit(int limit) {
        data = data.slice(0,limit);
        return this;
    }
}
