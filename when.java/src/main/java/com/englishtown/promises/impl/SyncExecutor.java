package com.englishtown.promises.impl;

import java.util.concurrent.Executor;

/**
 * Synchronous executor
 */
public class SyncExecutor implements Executor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Runnable command) {
        command.run();
    }

}
