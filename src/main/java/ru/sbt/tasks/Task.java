package ru.sbt.tasks;

import java.util.concurrent.Callable;

public class Task<T> {

    private volatile Object result;
    private final Object lock = new Object();
    private final Callable<? extends T> call;
    private boolean isException = false;

    public Task(Callable<? extends T> callable) {
        call = callable;
    }

    public T get() {
        if (result != null) return objectReturn();
        synchronized (lock) {
            if (result != null) return objectReturn(); // for first waiting Threads
            try {
                result = call.call();

            } catch (Exception e) {
                isException = true;
                result = e;
            }
            System.out.println(Thread.currentThread().getName());
            return objectReturn();
        }
    }

    private T objectReturn() {
        if (isException) {
            throw new RuntimeException("Error during method call", (Exception) result);
        } else {
            return (T) result;
        }
    }


}
