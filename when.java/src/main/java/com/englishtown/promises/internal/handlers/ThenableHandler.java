package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.Thenable;
import com.englishtown.promises.internal.AssimilateTask;
import com.englishtown.promises.internal.PromiseHelper;

/**
 * Handler that wraps an untrusted thenable and assimilates it in a future stack
 */
public class ThenableHandler<T> extends DeferredHandler<T> {

    public ThenableHandler(Thenable<T> thenable, PromiseHelper helper) {
        super(helper, null);
        helper.getScheduler().enqueue(new AssimilateTask<>(thenable, this, helper));
    }

}
