package com.shinybunny.utils;

import java.util.Iterator;
import java.util.function.Consumer;

public class Promise<T> {

    private final Task<T, Exception> task;
    private Consumer<T> success;
    private Consumer<Exception> fail;
    private boolean started;

    public Promise(Task<T,Exception> task) {
        this.task = task;
    }

    public Promise(T literal) {
        this((s,f)->s.accept(literal));
    }

    public static Promise<Array<?>> all(Promise<?>... promises) {
        return new Promise<>((success, fail) -> {
            Iterator<Promise<?>> iterator = ListUtils.iterator(promises);
            Array<Object> arr = new Array<>();
            if (iterator.hasNext()) {
                Promise<?> p = iterator.next();
                p.then(nextPromise(iterator,arr, success));
            }
        });
    }

    private static <T> Consumer<T> nextPromise(Iterator<Promise<?>> iterator, Array<Object> arr, Consumer<Array<?>> resolve) {
        return (obj)->{
            arr.add(obj);
            if (iterator.hasNext()) {
                Promise<?> p = iterator.next();
                p.then(nextPromise(iterator,arr,resolve));
            } else {
                resolve.accept(arr);
            }
        };
    }

    public Promise<T> then(Consumer<T> res) {
        success = res;
        start();
        return this;
    }

    private void start() {
        if (started) return;
        started = true;
        new Thread(()->{
            try {
                task.run((res) -> {
                    if (success != null) {
                        success.accept(res);
                    }
                }, (err) -> {
                    if (fail != null) {
                        fail.accept(err);
                    }
                });
            } catch (Exception e) {
                if (fail != null) {
                    fail.accept(e);
                }
            }
        },"PromiseThread")
                .run();
    }

    public void then(Consumer<T> res, Consumer<Exception> err) {
        success = res;
        fail = err;
        start();
    }

    public Promise<T> fail(Consumer<Exception> err) {
        fail = err;
        start();
        return this;
    }

}
