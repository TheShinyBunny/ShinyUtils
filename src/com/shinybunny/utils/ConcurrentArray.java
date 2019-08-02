package com.shinybunny.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A concurrent array will NOT be modified immediately. It saves any modification and will commit them through the {@link #update()} method.<br/>
 * To update all pending changes, call {@link #updateAll()} at the end of every tick in your program,
 * or whenever you want to apply all changes to all concurrent arrays.
 * @param <T>
 */
public class ConcurrentArray<T> extends Array<T> {

    private Array<Runnable> futureTasks = new Array<>();

    public static final List<ConcurrentArray<?>> arraysToAdd = new ArrayList<>();
    public static final List<ConcurrentArray<?>> needsUpdate = new ArrayList<>();

    public ConcurrentArray() {
    }

    public ConcurrentArray(int capacity) {
        super(capacity);
    }

    public ConcurrentArray(boolean outOfBoundsException) {
        super(outOfBoundsException);
    }

    public ConcurrentArray(int capacity, boolean outOfBoundsException) {
        super(capacity, outOfBoundsException);
    }

    public ConcurrentArray(Iterable<T> values) {
        super(values);
    }

    public ConcurrentArray(Iterable<T> values, boolean outOfBoundsException) {
        super(values, outOfBoundsException);
    }

    public ConcurrentArray(Array<T> values) {
        super(values);
    }

    public ConcurrentArray(Array<T> values, boolean outOfBoundsException) {
        super(values, outOfBoundsException);
    }

    @SafeVarargs
    public ConcurrentArray(T... values) {
        super(values);
    }

    @SafeVarargs
    public ConcurrentArray(boolean outOfBoundsException, T... values) {
        super(outOfBoundsException, values);
    }

    @Override
    protected <R> R modify(Supplier<R> modification) {
        arraysToAdd.add(this);
        futureTasks.add(modification::get);
        return null;
    }

    public static void updateAll() {
        needsUpdate.addAll(arraysToAdd);
        arraysToAdd.clear();
        needsUpdate.forEach(ConcurrentArray::update);
        needsUpdate.clear();
    }

    public void update() {
        futureTasks.forEach(Runnable::run);
        futureTasks.clear();
    }
}
