package ru.sbt.manager.executor;

import ru.sbt.manager.context.Context;
import ru.sbt.manager.context.ContextImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ExecutorManagerImpl implements ExecutorManager {

    ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {

        List<Future<Object>> list = new ArrayList<>();
        for (Runnable task : tasks) {
            Future<Object> submit = executorService.submit(Executors.callable(task));
            list.add(submit);
        }

        return new ContextImpl(executorService, callback, list);

    }
}
