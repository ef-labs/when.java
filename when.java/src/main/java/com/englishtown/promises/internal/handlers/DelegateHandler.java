package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.State;
import com.englishtown.promises.internal.PromiseHelper;

/**
 * Abstract base for handler that delegates to another handler
 */
public abstract class DelegateHandler<T> extends Handler<T> {

    protected DelegateHandler(Handler<T> handler, PromiseHelper helper) {
        super(helper);
        this.handler = handler;
    }

    @Override
    public State<T> inspect() {
        return this.join().inspect();
    }

    @Override
    protected void _report(Object context) {
        this.join()._report(context);
    }

    @Override
    protected void _unreport() {
        this.join()._unreport();
    }

}
