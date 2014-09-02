package com.englishtown.promises.internal;

import com.englishtown.promises.Promise;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.internal.handlers.Handler;

import java.util.function.Function;

/**
 * Assimilate a thenable, sending it's value to resolver
 */
public class AssimilateTask<T, U> implements Runnable {

    private final Thenable<T> thenable;
    private final Handler<T> resolver;
    private final PromiseHelper helper;

    public AssimilateTask(Thenable<T> thenable, Handler<T> resolver, PromiseHelper helper) {
        this.thenable = thenable;
        this.resolver = resolver;
        this.helper = helper;
    }

    @Override
    public void run() {
        Handler<T> h = this.resolver;

        Function<T, Promise<U>> _resolve = (x) -> {
            h.resolve(x);
            return null;
        };

        Function<Throwable, Promise<U>> _reject = (x) -> {
            h.reject(x);
            return null;
        };

        tryAssimilate(this.thenable, _resolve, _reject);

    }

    private void tryAssimilate(
            Thenable<T> thenable,
            Function<T, Promise<U>> resolve,
            Function<Throwable, Promise<U>> reject) {

        try {
            thenable.then(resolve, reject);
        } catch (Throwable e) {
            reject.apply(e);
        }

    }

}
