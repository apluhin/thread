package ru.sbt.manager.context;

import java.util.List;
import java.util.concurrent.*;

public class ContextImpl implements Context {

    private int countFailed = 0;
    private int countInterrupt = 0;
    private int countComplete = 0;
    private boolean finish = false;

    private volatile boolean completeCalculate = false;


    private final List<Future<Object>> futureList;
    private final ExecutorService executorService;
    private Runnable callback;

    public ContextImpl(ExecutorService executorService, Runnable callback, List<Future<Object>> list) {
        this.executorService = executorService;
        this.futureList = list;
        this.callback = callback;
        new Thread(this::checkTerminated).start();
    }

    @Override
    public int getCompletedTaskCount() {
        return countComplete;
    }

    @Override
    public int getFailedTaskCount() {
        return countFailed;
    }

    @Override
    public int getInterruptedTaskCount() {
        return countInterrupt;
    }

    @Override
    public void interrupt() {
        finishExecute();
    }

    private void doFinalWork() {
        completeCalculate = true;
        executorService.shutdownNow();
        executorService.shutdown();
        new Thread(callback).start();
    }

    private void calculate() {
        for (Future<Object> objectFuture : futureList) {
            try {
                objectFuture.get(1, TimeUnit.NANOSECONDS);
                countComplete++;
            } catch (InterruptedException | TimeoutException e) {
                countInterrupt++;
            } catch (ExecutionException e) {
                countFailed++;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finish;
    }

    private void checkTerminated() {
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("Too long work executor");
        }
        finishExecute();
    }

    private void finishExecute() {
        synchronized (executorService) {
            if (!completeCalculate) {
                this.finish = true;
                calculate();
                doFinalWork();
            }
        }
    }
}



