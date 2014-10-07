package com.englishtown.promises.internal;

import com.englishtown.promises.*;
import com.englishtown.promises.internal.handlers.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Injected promise helper methods
 */
public class PromiseHelper {

    private final Scheduler scheduler;
    private final Context context;
    private final Reporter reporter;

    private final Promise<Object> foreverPendingPromise;

    @Inject
    public PromiseHelper(Environment environment, Context context, Reporter reporter) {
        this.scheduler = environment.getScheduler();
        this.context = context;
        this.reporter = reporter;

        Handler<Object> foreverPendingHandler = new Handler<Object>(this) {
        };
        foreverPendingPromise = new TrustedPromise<>(foreverPendingHandler, this);
    }

    /**
     * Returns a trusted promise.
     *
     * @param x   a value to be wrapped in a promise
     * @param <T> type of resolved value
     * @return {Promise} promise
     */
    public <T> TrustedPromise<T> resolve(T x) {
        return resolve0(x);
    }

    /**
     * Returns a trusted promise. If x is already a trusted promise, it is
     * returned, otherwise returns a new trusted Promise which follows x.
     *
     * @param x   a thenable to be wrapped in a trusted promise
     * @param <T> type of thenable to resolve with
     * @return {Promise} promise
     */
    public <T> TrustedPromise<T> resolve(Thenable<T> x) {
        return resolve0(x);
    }

    @SuppressWarnings("unchecked")
    private <T> TrustedPromise<T> resolve0(Object x) {
        return isPromise(x) ? (TrustedPromise<T>) x
                : new TrustedPromise<>(new AsyncHandler<>(this.<T>getHandler(x), this), this);
    }

    <T> TrustedPromise<T> toPromise(T x) {
        return resolve0(x);
    }

    <T> TrustedPromise<T> toPromise(Thenable<T> x) {
        return resolve0(x);
    }
    // TODO: need both resolve and toPromise?

    /**
     * Return a rejected promise with x as its reason (x is used verbatim)
     *
     * @param x   a throwable to reject with
     * @param <T> type of rejected promise
     * @return {Promise} rejected promise
     */
    public <T> TrustedPromise<T> reject(Throwable x) {
        return new TrustedPromise<>(new AsyncHandler<>(new RejectedHandler<T>(x, this), this), this);
    }

    @SuppressWarnings("unchecked")
    public <T> Promise<T> never() {
        return (Promise<T>) foreverPendingPromise;
    }

    /**
     * Creates an internal {promise, resolver} pair
     *
     * @param <T> type of deferred
     * @return {Promise}
     */
    public <T> TrustedPromise<T> defer() {
        return new TrustedPromise<>(new DeferredHandler<>(this, null), this);
    }

    /**
     * Get an appropriate handler for x, without checking for cycles
     *
     * @param x   promise, thenable, or fulfillment value
     * @param <T> type of handler
     * @return {object} handler
     */
    @SuppressWarnings("unchecked")
    public <T> Handler<T> getHandler(Object x) {
        if (isPromise(x)) {
            return ((TrustedPromise<T>) x)._handler.join();
        }
        return maybeThenable(x) ? getHandlerUntrusted((Thenable<T>) x) : new FulfilledHandler<>((T) x, this);
    }

    public boolean isPromise(Object x) {
        return x instanceof TrustedPromise;
    }

    /**
     * @param x object to check if thenable
     * @return {boolean} false iff x is guaranteed not to be a thenable
     */
    public boolean maybeThenable(Object x) {
        return x instanceof Thenable;
    }

    /**
     * Get a handler for potentially untrusted thenable x
     *
     * @param x a thenable
     * @return {object} handler
     */
    private <T> Handler<T> getHandlerUntrusted(Thenable<T> x) {
        try {
            return (x != null) ? new ThenableHandler<>(x, this) : new FulfilledHandler<>(null, this);
//            var untrustedThen = x.then;
//            return typeof untrustedThen === 'function'
//                    ? new ThenableHandler(untrustedThen, x)
//                    : new FulfilledHandler(x);
        } catch (Throwable e) {
            return new RejectedHandler<>(e, this);
        }
    }

    /**
     * Return f.call(thisArg, x), or if it throws return a rejected promise for
     * the thrown exception
     *
     * @param f   function to apply in try/catch
     * @param x   parameter passed to f
     * @param <T> type of parameter
     * @param <U> type of thenable returned
     * @return result of f or rejected promise if exception thrown
     */
    public <T, U> Thenable<U> tryCatchReject(Function<T, ? extends Thenable<U>> f, T x) {
        try {
            return f.apply(x);
        } catch (Throwable e) {
            return reject(e);
        }
    }

    /**
     * Same as above, but includes the extra argument parameter.
     *
     * @param f   function to apply in try/catch
     * @param x   parameter passed to f
     * @param y   second parameter passed to f
     * @param <T> type of parameter
     * @param <U> type of second parameter
     * @param <V> type of thenable returned
     * @return result of f or rejected promise if exception thrown
     */
    public <T, U, V> Thenable<V> tryCatchReject2(BiFunction<T, U, ? extends Thenable<V>> f, T x, U y) {
        try {
            return f.apply(x, y);
        } catch (Throwable e) {
            return reject(e);
        }
    }

    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public Context getContext() {
        return this.context;
    }

    public Reporter getReporter() {
        return this.reporter;
    }

    /**
     * Return a promise that will fulfill when all promises in the
     * input array have fulfilled, or will reject when one of the
     * promises rejects.
     *
     * @param promises array of promises
     * @param <T>      type of promises
     * @return {Promise} promise for array of fulfillment values
     */
    public <T> Promise<List<T>> all(List<? extends Thenable<T>> promises) {

        DeferredHandler<List<T>> resolver = new DeferredHandler<>(this, null);
        ValueHolder<Integer> pending = new ValueHolder<>(promises == null ? 0 : promises.size());
        List<T> results = new ArrayList<>(pending.value);

        BiConsumer<DeferredHandler<T>, Integer> resolveOne = (handler, i) -> {
            handler.map((x) -> {
                results.set(i, x);
                if (--(pending.value) == 0) {
                    resolver.become(new FulfilledHandler<>(results, this));
                }
            }, resolver);
        };

        if (promises == null) {
            throw new IllegalArgumentException("promises cannot be null");
        }

        for (int i = 0; i < promises.size(); ++i) {
            Thenable<T> x = promises.get(i);
            results.add(i, null);

            if (x == null) {
                --pending.value;
                continue;
            }

            Handler<T> h = isPromise(x)
                    ? ((TrustedPromise<T>) x)._handler.join()
                    : getHandlerUntrusted(x);

            HandlerState s = h.state();

            switch (s) {
                case PENDING:
                    resolveOne.accept((DeferredHandler<T>) h, i);
                    break;

                case FULFILLED:
                    // TODO: Use inspect instead?
                    results.set(i, ((FulfilledHandler<T>) h).getValue());
                    --pending.value;
                    break;

                case REJECTED:
                    // TODO: finish all() rejected.  Better approach?  inspect()?
                    resolver.reject(((RejectedHandler<T>) h).getValue());
//                resolver.become(h);
                    // Break the for loop
                    i = promises.size();
                    break;

            }

            // TODO: Support values or only promises?
//            if (helper.maybeThenable(x)) {
//
//            } else {
//                results[i] = x;
//                --pending;
//            }
        }

        if (pending.value == 0) {
            resolver.become(new FulfilledHandler<>(results, this));
        }

        return new TrustedPromise<>(resolver, this);
    }

    /**
     * Fulfill-reject competitive race. Return a promise that will settle
     * to the same state as the earliest input promise to settle.
     * <p>
     * WARNING: The ES6 Promise spec requires that race()ing an empty array
     * must return a promise that is pending forever.  This implementation
     * returns a singleton forever-pending promise, the same singleton that is
     * returned by Promise.never(), thus can be checked with ===
     *
     * @param promises array of promises to race
     * @param <T>      type of promises
     * @return {Promise} if input is non-empty, a promise that will settle
     * to the same outcome as the earliest input promise to settle. if empty
     * is empty, returns a promise that will never settle.
     */
    public <T> Promise<T> race(List<? extends Thenable<T>> promises) {
        // Sigh, race([]) is untestable unless we return *something*
        // that is recognizable without calling .then() on it.
        if (promises.size() == 0) {
            return never();
        }

        DeferredHandler<T> h = new DeferredHandler<>(this, null);

        for (int i = 0; i < promises.size(); ++i) {
            Thenable<T> x = promises.get(i);
            if (x != null) {
                this.<T>getHandler(x).chain(h::resolve, h::reject);
            }
        }

        return new TrustedPromise<>(h, this);
    }

}
