package ru.sbt.tasks;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;

import static org.junit.Assert.*;

public class TaskTest {

    private Callable<String> call1;
    private Callable<String> call2;
    private Task<String> task;

    @Before
    public void setUp() throws Exception {
        call1 = () -> {
            Thread.sleep(3000);
            return Thread.currentThread().getName();
        };
        call2 = () -> {
            Thread.sleep(3000);
            throw new IllegalArgumentException("Some exception");
        };



    }

    @Test
    public void testNormalWork() throws Exception {
        task = new Task<>(call1);
        Thread th = new Thread(task::get);
        th.setName("test");
        th.start();
        th.join();
        System.out.println(task.get());
        System.out.println(task.get());
    }

    @Test(expected = RuntimeException.class)
    public void testNormalException() throws Exception {
        task = new Task<>(call2);
        new Thread(task::get).start();
        Thread.sleep(1000);
        System.out.println(task.get());
    }

    @Test
    public void runThread() throws Exception {
        task = new Task<>(call1);
        Thread thread1 = new Thread(task::get);
        thread1.setName("1");
        String s = task.get();
        Thread.sleep(200);
        thread1.start();
        assertEquals("main", s);

    }
}