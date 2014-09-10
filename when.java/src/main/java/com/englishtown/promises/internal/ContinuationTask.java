package com.englishtown.promises.internal;

import com.englishtown.promises.internal.handlers.Handler;

/**
 * Run a single consumer
 */
public class ContinuationTask<T> implements Runnable {

    private final Continuation<T, ?> continuation;
    private final Handler<T> handler;

    public ContinuationTask(Continuation<T, ?> continuation, Handler<T> handler) {
        this.continuation = continuation;
        this.handler = handler;
    }

    @Override
    public void run() {
        this.handler.join().when(this.continuation);
    }

}
