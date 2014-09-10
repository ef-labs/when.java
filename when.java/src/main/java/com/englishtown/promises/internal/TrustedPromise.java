package com.englishtown.promises.internal;

import com.englishtown.promises.Promise;
import com.englishtown.promises.PromiseResolver;
import com.englishtown.promises.State;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.exceptions.RejectException;
import com.englishtown.promises.internal.handlers.DeferredHandler;
import com.englishtown.promises.internal.handlers.Handler;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

import static com.englishtown.promises.HandlerState.FULFILLED;

/**
 * A trusted {@link com.englishtown.promises.Promise}
 */
public class TrustedPromise<T> implements Promise<T> {

    public final Handler<T> _handler;
    private final PromiseHelper helper;

    /**
     * Create a promise whose fate is determined by handler
     *
     * @param handler handler used to fulfill/reject
     * @param helper  promise helper methods
     */
    public TrustedPromise(Handler<T> handler, PromiseHelper helper) {
        this._handler = handler;
        this.helper = helper;
    }

    /**
     * Create a promise whose fate is determined by the resolver
     *
     * @param resolver a promise resolver to fulfill/reject
     * @param helper   promise helper methods
     */
    public TrustedPromise(PromiseResolver<T> resolver, PromiseHelper helper) {
        this.helper = helper;
        this._handler = init(resolver);
    }

    /**
     * Run the supplied resolver
     *
     * @param resolver a promise resolver to fulfill/reject
     * @return {makePromise.DeferredHandler}
     */
    private DeferredHandler<T> init(PromiseResolver<T> resolver) {
        DeferredHandler<T> handler = new DeferredHandler<>(helper, null);

//        /**
//         * Issue a progress event, notifying all progress listeners
//         * @param {*} x progress event payload to pass to all listeners
//         */
//        function promiseNotify(x) {
//                handler.notify(x);
//        }

        try {
            resolver.resolve(
                    /**
                     * Transition from pre-resolution state to post-resolution state, notifying
                     * all listeners of the ultimate fulfillment or rejection
                     * @param {*} x resolution value
                     */
                    handler::resolve,
                    /**
                     * Reject this promise with reason, which will be used verbatim
                     * @param {Error|*} reason rejection reason, strongly suggested
                     *   to be an Error type
                     */
                    handler::reject
            );

        } catch (Throwable e) {
            handler.reject(e);
        }

        return handler;

    }

    // Creation

//    Promise.resolve = resolve;
//    Promise.reject = reject;
//    Promise.never = never;
//
//    Promise._defer = defer;

    @Override
    public <U> Promise<U> then(Function<T, ? extends Thenable<U>> onFulfilled) {
        return then(onFulfilled, null);
    }

    /**
     * Transform this promise's fulfillment value, returning a new Promise
     * for the transformed result.  If the promise cannot be fulfilled, onRejected
     * is called with the reason.  onProgress *may* be called with updates toward
     * this promise's fulfillment.
     *
     * @param onFulfilled fulfillment handler
     * @param onRejected  rejection handler
     * @return new promise
     */
    @Override
    public <U> Promise<U> then(Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
        Handler<T> parent = this._handler;

        if ((onFulfilled == null) && (parent.join().state() == FULFILLED)) {
            // Short circuit: value will not change, simply share handler
            //noinspection unchecked
            return new TrustedPromise<>((Handler<U>) parent, helper); // TODO: Check this cast
        }

        TrustedPromise<U> p = this._beget();
        Handler<U> child = p._handler;

        Continuation<T, U> cont = new Continuation<>();
        cont.resolve = child::resolve;
//        cont.notify = child::notify;
        cont.context = child;
//        cont.receiver = parent.receiver;
        cont.fulfilled = onFulfilled;
        cont.rejected = onRejected;
//        cont.progress = arguments.length > 2 ? arguments[2] : null;

        parent.when(cont);

        return p;
    }

    /**
     * Creates a new, pending promise of the same type as this promise
     *
     * @return {Promise}
     */
    private <U> TrustedPromise<U> _beget() {
        Handler<T> parent = this._handler;
        DeferredHandler<U> child = new DeferredHandler<>(helper, parent.join().context);
        return new TrustedPromise<>(child, helper);
    }

    /**
     * Check if x is a rejected promise, and if so, delegate to handler._fatal
     *
     * @param x a thenable
     */
    private void _maybeFatal(Thenable<?> x) {
        if (!helper.maybeThenable(x)) {
            return;
        }

        Handler<T> handler = helper.getHandler(x);
        Object context = this._handler.context;
        handler.catchError(t -> {
            handler._fatal(context);
        }, handler);
    }

    @Override
    public State<T> inspect() {
        return _handler.inspect();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> void done(Function<T, ? extends Thenable<U>> onResult, Function<Throwable, ? extends Thenable<U>> onError) {
        Handler<T> h = this._handler;

        Continuation<T, U> cont = new Continuation<>();
        cont.resolve = this::_maybeFatal;
//        cont.notify = noop;
        cont.context = this;
        cont.fulfilled = onResult;
        cont.rejected = onError;
//        cont.progress = null;

//        h.when({ resolve: this._maybeFatal, notify: noop, context: this,
//                receiver: h.receiver, fulfilled: onResult, rejected: onError,
//                progress: void 0 });

        h.when(cont);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> otherwise(Function<Throwable, ? extends Thenable<U>> onRejected) {
        return this.then(null, onRejected);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> otherwise(Predicate<Throwable> predicate, Function<Throwable, ? extends Thenable<U>> onRejected) {
        if (predicate == null) {
            return otherwise(onRejected);
        } else {
            if (onRejected == null) {
                //noinspection unchecked
                return (Promise<U>) this.ensure(this::rejectInvalidPredicate);
            }

            return this.otherwise(this.createCatchFilter(onRejected, predicate));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> otherwise(Class<? extends Throwable> type, Function<Throwable, ? extends Thenable<U>> onRejected) {
        return otherwise(type::isInstance, onRejected);
    }

    /**
     * Wraps the provided catch handler, so that it will only be called
     * if the predicate evaluates truthy
     *
     * @param handler   catch function
     * @param predicate predicate to check if handler should be called
     * @return conditional catch handler
     */
    private <U> Function<Throwable, ? extends Thenable<U>> createCatchFilter(Function<Throwable, ? extends Thenable<U>> handler, Predicate<Throwable> predicate) {
        return (e) -> {
            return predicate.test(e)
                    ? handler.apply(e)
                    : helper.reject(e);
        };
    }

    /**
     * Ensures that onFulfilledOrRejected will be called regardless of whether
     * this promise is fulfilled or rejected.  onFulfilledOrRejected WILL NOT
     * receive the promises' value or reason.  Any returned value will be disregarded.
     * onFulfilledOrRejected may throw or return a rejected promise to signal
     * an additional error.
     *
     * @param handler handler to be called regardless of
     *                fulfillment or rejection
     * @return promise
     */
    @Override
    public Promise<T> ensure(Runnable handler) {
        if (handler == null) {
            // Optimization: result will not change, return same promise
            return this;
        }

        return this.then(
                x -> {
                    handler.run();
                    return this;
                },
                t -> {
                    handler.run();
                    return this;
                });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> orElse(Thenable<U> defaultValue) {
        return this.then(null, (t) -> {
            return defaultValue;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <U> Promise<U> yield(Thenable<U> value) {
        return this.then((x) -> {
            return value;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Promise<T> tap(Function<T, Thenable<T>> onFulfilledSideEffect) {
        return this.then(onFulfilledSideEffect).yield(this);
    }

    private void rejectInvalidPredicate() {
        throw new RejectException("catch predicate must be a function");
    }

    @Override
    public <U, V> Promise<V> fold(BiFunction<U, T, ? extends Thenable<V>> fn, Thenable<U> arg) {
        TrustedPromise<V> promise = this._beget();
        this._handler.fold(promise._handler, fn, arg);
        return promise;
    }

}
