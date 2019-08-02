package com.shinybunny.utils.linq;

import com.shinybunny.utils.Array;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Query<T,Self extends Query> {

    Self filter(Predicate<T> filter);

    Array<T> select();

    Array<T> top(int count);

    T first();

    T last();

    Array<T> topPercent(int percent);

    double max(Function<T,Double> numProperty);
    double min(Function<T,Double> numProperty);

    double average(Function<T,Double> avgProperty);
    int count();
    double sum(Function<T,Double> numProperty);

    <R> Array<R> select(Function<T,R> mapper);

    <R> Query<R,Query> map(Function<T,R> mapper);

    Self limit(int limit);

    static <T> Query<T,Query> on(Iterable<T> elements) {
        return new SimpleQuery<>(elements);
    }

}
