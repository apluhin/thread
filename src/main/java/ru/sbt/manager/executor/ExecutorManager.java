package ru.sbt.manager.executor;

import ru.sbt.manager.context.Context;

public interface ExecutorManager {



    Context execute(Runnable callback, Runnable... tasks);


}
