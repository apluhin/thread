package ru.sbt.manager.executor;

import org.junit.Before;
import org.junit.Test;
import ru.sbt.manager.context.Context;

import static org.junit.Assert.*;

public class ExecutorManagerTest {
    Context context;
    ExecutorManager executorManager;
    Runnable task1;
    Runnable task2;
    Runnable task3;
    Runnable callback;

    @Before
    public void setUp() throws Exception {

        task1 = () -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {

            }
            System.out.println("task1");
        };

        task2 = () -> {
            throw new RuntimeException("error");
        };

        task3 = () -> {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {

            }
            System.out.println("task3");
        };

        Runnable callback = () -> {
            System.out.println("stop");
        };
        executorManager = new ExecutorManagerImpl();
    }

    @Test
    public void testNormalWork() throws Exception {
        context = executorManager.execute(callback, task1,task1, task2, task2, task3, task3);
        assertEquals(context.isFinished(),false);
        Thread.sleep(6000);
        assertEquals(context.getCompletedTaskCount(), 4);
        assertEquals(context.getFailedTaskCount(), 2);
        assertEquals(context.getInterruptedTaskCount(), 0);
        assertEquals(context.isFinished(), true);
    }

    @Test
    public void testInterrput() throws Exception {
        context = executorManager.execute(callback, task1,task1, task2, task2, task3, task3);
        assertEquals(context.isFinished(),false);
        Thread.sleep(3000);
        context.interrupt();
        assertEquals(context.getCompletedTaskCount(), 2);
        assertEquals(context.getFailedTaskCount(), 2);
        assertEquals(context.getInterruptedTaskCount(), 2);
        assertEquals(context.isFinished(), true);
    }

    @Test
    public void testAllException() throws Exception {
        context = executorManager.execute(callback, task2, task2, task2);
        Thread.sleep(1000);
        assertEquals(context.getFailedTaskCount(), 3);
        assertEquals(context.isFinished(), true);
    }
}