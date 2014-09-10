package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.internal.Continuation;
import com.englishtown.promises.internal.ContinuationTask;
import com.englishtown.promises.internal.PromiseHelper;

/**
 * Wrap another handler and force it into a future stack
 */
public class AsyncHandler<T> extends DelegateHandler<T> {

    public AsyncHandler(Handler<T> handler, PromiseHelper helper) {
        super(handler, helper);
    }

    @Override
    public void when(Continuation<T, ?> continuation) {
        helper.getScheduler().enqueue(new ContinuationTask<>(continuation, this.join()));
    }

}
