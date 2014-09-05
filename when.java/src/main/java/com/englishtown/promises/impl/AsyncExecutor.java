package com.englishtown.promises.impl;

import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Asynchronous executor that uses a fixed thread pool
 */
public class AsyncExecutor implements Executor {

    private ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Inject
    public AsyncExecutor() {

    }

    /**
     * Executes the given command at some time in the future.  The command
     * may execute in a new thread, in a pooled thread, or in the calling
     * thread, at the discretion of the {@code Executor} implementation.
     *
     * @param command the runnable task
     * @throws java.util.concurrent.RejectedExecutionException if this task cannot be
     *                                                         accepted for execution
     * @throws NullPointerException                            if command is null
     */
    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }
}
