/*
 * The MIT License (MIT)
 * Copyright © 2013 Englishtown <opensource@englishtown.com>
 * http://englishtown.mit-license.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.englishtown.promises;

import com.englishtown.promises.monitor.MonitorApi;
import com.englishtown.promises.monitor.PromiseStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by adriangonzalez on 2/22/14.
 */
public class WhenProgress2<TResolve, TProgress> {


    /**
     * Register an observer for an immediate value.
     *
     * @param value the value to provide to returned promise
     * @return a new {@link ProgressPromise} that will complete with the return value of onFulfilled or the provided value if
     * onFulfilled is not supplied.
     */
    public ProgressPromise<TResolve, TProgress> when(TResolve value) {
        return when(value, null);
    }

    /**
     * Register an observer for an immediate value.
     *
     * @param value       the value to provide to the onFulfilled callback
     * @param onFulfilled callback to be called when value is successfully fulfilled.  It will be invoked immediately.
     * @return a new {@link ProgressPromise} that will complete with the return value of onFulfilled or the provided value if
     * onFulfilled is not supplied.
     */
    public ProgressPromise<TResolve, TProgress> when(
            TResolve value,
            Runnable<ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
        // Get a trusted promise for the input value, and then
        // register promise handlers
        return when(resolve(value), onFulfilled);
    }

    /**
     * Register an observer for a promise.
     *
     * @param promise a promise whose value is provided to the callbacks.
     * @return a new {@link ProgressPromise} that will complete with the completion value of promise.
     */
    public ProgressPromise<TResolve, TProgress> when(
            Thenable<TResolve, TProgress> promise) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(promise, null, null, null);
    }

    /**
     * Register an observer for a promise.
     *
     * @param promise     a promise whose value is provided to the callbacks.
     * @param onFulfilled callback to be called when the promise is successfully fulfilled.  If the promise is an
     *                    immediate value, the callback will be invoked immediately.
     * @return a new {@link ProgressPromise} that will complete with the return value of onFulfilled or the
     * completion value of promise if onFulfilled is not supplied.
     */
    public ProgressPromise<TResolve, TProgress> when(
            Thenable<TResolve, TProgress> promise,
            Runnable<ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
        return when(promise, onFulfilled, null, null);
    }

    /**
     * Register an observer for a promise.
     *
     * @param promise     a promise whose value is provided to the callbacks.
     * @param onFulfilled callback to be called when the promise is successfully fulfilled.  If the promise is an
     *                    immediate value, the callback will be invoked immediately.
     * @param onRejected  callback to be called when the promise is rejected.
     * @return a new {@link ProgressPromise} that will complete with the return value of onFulfilled or onRejected or the
     * completion value of promise if onFulfilled and/or onRejected are not supplied.
     */
    public ProgressPromise<TResolve, TProgress> when(
            Thenable<TResolve, TProgress> promise,
            Runnable<ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
        return when(promise, onFulfilled, onRejected, null);
    }

    /**
     * Register an observer for a promise or immediate value.
     *
     * @param {*}         promiseOrValue
     * @param {function?} [onFulfilled] callback to be called when promiseOrValue is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @param {function?} [onRejected] callback to be called when promiseOrValue is
     *                    rejected.
     * @param {function?} [onProgress] callback to be called when progress updates
     *                    are issued for promiseOrValue.
     * @returns {Promise} a new {@link Promise} that will complete with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public ProgressPromise<TResolve, TProgress> when(
            Thenable<TResolve, TProgress> promise,
            Runnable<ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return cast(promise).then(onFulfilled, onRejected, onProgress);
    }

    /**
     * Creates a new promise whose fate is determined by resolver.
     *
     * @param {function} resolver function(resolve, reject, notify)
     * @returns {Promise} promise whose fate is determine by resolver
     */
    private Promise0 promise(ResolveCallback<TResolve, TProgress> resolver) {
        return new Promise0(resolver, monitorApi.promiseStatus());
    }

    protected class Promise0 extends AbstractPromise {

        private ValueHolder<AbstractPromise> value = new ValueHolder<>();
        private ValueHolder<List<Runnable<Void, AbstractPromise>>> consumers = new ValueHolder<>();

        /**
         * Trusted Promise constructor.  A Promise created from this constructor is
         * a trusted when.js promise.  Any other duck-typed promise is considered
         * untrusted.
         *
         * @constructor
         * @returns {Promise} promise whose fate is determine by resolver
         * @name Promise
         */
        private Promise0(ResolveCallback<TResolve, TProgress> resolver, final PromiseStatus status) {

            final Promise0 self = this;
            this._status = status;
//            this.inspect = inspect;
//            this._when = _when;

            /**
             * Transition from pre-resolution state to post-resolution state, notifying
             * all listeners of the ultimate fulfillment or rejection
             * @param {*} val resolution value
             */
            final Runnable<Void, Thenable<TResolve, TProgress>> promiseResolve = new Runnable<Void, Thenable<TResolve, TProgress>>() {
                @Override
                public Void run(Thenable<TResolve, TProgress> val) {
                    if (consumers.value == null) {
                        return null;
                    }

                    // TODO: Make thread safe
                    final List<Runnable<Void, AbstractPromise>> queue = consumers.value;
                    consumers.value = null;

                    value.value = coerce(self, val);
                    enqueue(new Runnable<Void, Void>() {
                        @Override
                        public Void run(Void aVoid) {
                            if (status != null) {
                                updateStatus(value.value, status);
                            }
                            runHandlers(queue, value.value);
                            return null;
                        }
                    });

                    return null;
                }
            };

            /**
             * Reject this promise with the supplied reason, which will be used verbatim.
             * @param {*} reason reason for the rejection
             */
            Runnable<Void, Value<TResolve>> promiseReject = new Runnable<Void, Value<TResolve>>() {
                @Override
                public Void run(Value<TResolve> reason) {
                    promiseResolve.run(new RejectedPromise(reason));
                    return null;
                }
            };

            /**
             * Issue a progress event, notifying all progress listeners
             * @param {*} update progress event payload to pass to all listeners
             */
            Runnable<Void, Value<TProgress>> promiseNotify = new Runnable<Void, Value<TProgress>>() {
                @Override
                public Void run(final Value<TProgress> update) {
                    final List<Runnable<Void, AbstractPromise>> queue = consumers.value;
                    if (queue != null) {
                        enqueue(new Runnable<Void, Void>() {
                            @Override
                            public Void run(Void aVoid) {
                                runHandlers(queue, new ProgressingPromise(update));
                                return null;
                            }
                        });
                    }
                    return null;
                }
            };

            // Call the provider resolver to seal the promise's fate
            try {
                resolver.run(promiseResolve, promiseReject, promiseNotify);
            } catch (Throwable e) {
                promiseReject.run(new Value<TResolve>(e));
            }

        }

        /**
         * Returns a snapshot of this promise's current status at the instant of call
         *
         * @returns {{state:String}}
         */
        public PromiseState<TResolve> inspect() {
            return (value.value != null) ? value.value.inspect() : toPendingState();
        }

        /**
         * Private message delivery. Queues and delivers messages to
         * the promise's ultimate fulfillment value or rejection reason.
         *
         * @param resolve
         * @param notify
         * @param onFulfilled
         * @param onRejected
         * @param onProgress
         * @private
         */
        @Override
        protected void _when(
                final Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                final Runnable<Void, Value<TProgress>> notify,
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                final Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            final Runnable<Void, AbstractPromise> deliver = new Runnable<Void, AbstractPromise>() {
                @Override
                public Void run(AbstractPromise p) {
                    p._when(resolve, notify, onFulfilled, onRejected, onProgress);
                    return null;
                }
            };

            if (consumers.value != null) {
                consumers.value.add(deliver);
            } else {
                enqueue(new Runnable<Void, Void>() {
                    @Override
                    public Void run(Void aVoid) {
                        deliver.run(value.value);
                        return null;
                    }
                });
            }
        }

    }

    protected abstract class AbstractPromise implements PromiseExt<TResolve, TProgress> {

        protected PromiseStatus _status;

        /**
         * Register handlers for this promise.
         *
         * @param onFulfilled {Function} fulfillment handler
         * @param onRejected  {Function} rejection handler
         * @param onProgress  {Function} progress handler
         * @return {Promise} new Promise
         */
        @Override
        public AbstractPromise then(
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                final Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            final AbstractPromise self = this;

            return new Promise0(new ResolveCallback<TResolve, TProgress>() {
                @Override
                public void run(
                        Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                        Runnable<Void, Value<TResolve>> reject,
                        Runnable<Void, Value<TProgress>> notify) {
                    self._when(resolve, notify, onFulfilled, onRejected, onProgress);
                }
            }, this._status == null ? null : this._status.observed());
        }

        /**
         * Register handlers for this promise.
         *
         * @param onFulfilled {Function} fulfillment handler
         * @param onRejected  {Function} rejection handler
         * @return {Promise} new Promise
         */
        @Override
        public AbstractPromise then(
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        ;

        /**
         * Register handlers for this promise.
         *
         * @param onFulfilled {Function} fulfillment handler
         * @return {Promise} new Promise
         */
        @Override
        public AbstractPromise then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        ;

        /**
         * Private message delivery. Queues and delivers messages to
         * the promise's ultimate fulfillment value or rejection reason.
         *
         * @private
         */
        protected abstract void _when(
                Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                Runnable<Void, Value<TProgress>> notify,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress);

        public abstract PromiseState<TResolve> inspect();

        // TODO: What is catch/finally doing here?

        /**
         * Register a rejection handler.  Shortcut for .then(undefined, onRejected)
         *
         * @param {function?} onRejected
         * @return {Promise}
         */
        @Override
        public AbstractPromise otherwise(Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return this.then(null, onRejected, null);
        }

        ;

        private AbstractPromise catch0(Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return otherwise(onRejected);
        }

        ;

        /**
         * Ensures that onFulfilledOrRejected will be called regardless of whether
         * this promise is fulfilled or rejected.  onFulfilledOrRejected WILL NOT
         * receive the promises' value or reason.  Any returned value will be disregarded.
         * onFulfilledOrRejected may throw or return a rejected promise to signal
         * an additional error.
         *
         * @param {function} onFulfilledOrRejected handler to be called regardless of
         *                   fulfillment or rejection
         * @returns {Promise}
         */
        // TODO: promisePrototype['finally'] = promisePrototype.ensure = function(onFulfilledOrRejected) {
        public AbstractPromise ensure(final Runnable<? extends ProgressPromise<TResolve, TProgress>, Void> onFulfilledOrRejected) {

            if (onFulfilledOrRejected != null) {
                return this.then(
                        new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                            @Override
                            public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                                return resolve(onFulfilledOrRejected.run(null));
                            }
                        },
                        new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                            @Override
                            public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                                return resolve(onFulfilledOrRejected.run(null));
                            }
                        }
                ).yield(this);
            } else {
                return this;
            }

        }

        /**
         * Terminate a promise chain by handling the ultimate fulfillment value or
         * rejection reason, and assuming responsibility for all errors.  if an
         * error propagates out of handleResult or handleFatalError, it will be
         * rethrown to the host, resulting in a loud stack track on most platforms
         * and a crash on some.
         *
         * @param {function?} handleResult
         * @param {function?} handleError
         * @returns {undefined}
         */
        public AbstractPromise done(
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> handleResult,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> handleError) {

            return this.then(handleResult, handleError)
                    .catch0(
                            new RejectedCallback<TResolve, TProgress>() {
                                @Override
                                public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                                    crash(value.getCause());
                                    return null;
                                }
                            });

        }

        /**
         * Shortcut for .then(function() { return resolve(value); })
         *
         * @param value the value to be returned
         * @return an already-fulfilled {@link com.englishtown.promises.ProgressPromise}
         */
        @Override
        public ProgressPromise<TResolve, TProgress> yield(final TResolve value) {
            return this.then(new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                @Override
                public ProgressPromise<TResolve, TProgress> run(TResolve _) {
                    return resolve(value);
                }
            });
        }

        /**
         * Shortcut for .then(function() { return value; })
         *
         * @param {*} value
         * @return {Promise} a promise that:
         * - is fulfilled if value is not a promise, or
         * - if value is a promise, will fulfill with its value, or reject
         * with its reason.
         */
        @Override
        public AbstractPromise yield(final Thenable<TResolve, TProgress> value) {
            return this.then(new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                @Override
                public ProgressPromise<TResolve, TProgress> run(TResolve _) {
                    return resolve(value);
                }
            });
        }

        /**
         * Runs a side effect when this promise fulfills, without changing the
         * fulfillment value.
         *
         * @param {function} onFulfilledSideEffect
         * @returns {Promise}
         */
        public AbstractPromise tap(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilledSideEffect) {
            return this.then(onFulfilledSideEffect).yield(this);
        }

        ;

//        /**
//         * Assumes that this promise will fulfill with an array, and arranges
//         * for the onFulfilled to be called with the array as its argument list
//         * i.e. onFulfilled.apply(undefined, array).
//         * @param {function} onFulfilled function to receive spread arguments
//         * @return {Promise}
//         */
//        public AbstractPromise spread(Runnable<? extends ProgressPromise<List<TResolve>, TProgress> onFulfilled) {
//            return this.then(function(array) {
//                // array may contain promises, so resolve its contents.
//                return all(array, function(array) {
//                    return onFulfilled.apply(undef, array);
//                });
//            });
//        };
        // TODO: spread

        /**
         * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected)
         *
         * @deprecated
         */
        @Override
        @Deprecated
        public ProgressPromise<TResolve, TProgress> always(
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilledOrRejected) {
            return always(onFulfilledOrRejected, null);
        }

        /**
         * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected)
         *
         * @deprecated
         */
        @Override
        @Deprecated
        public AbstractPromise always(
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilledOrRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
            return this.then(
                    onFulfilledOrRejected,
                    new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                        @Override
                        public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                            return onFulfilledOrRejected.run(reason.getValue());
                        }
                    },
                    onProgress);
        }

        ;

    }

    /**
     * Casts x to a trusted promise. If x is already a trusted promise, it is
     * returned, otherwise a new trusted Promise which follows x is returned.
     *
     * @param {*} x
     * @returns {Promise}
     */
    public AbstractPromise cast(Thenable<TResolve, TProgress> x) {
        return (AbstractPromise.class.isInstance(x)) ? (AbstractPromise) x : resolve(x);
    }

    /**
     * Returns a resolved promise. The returned promise will be
     * - fulfilled with promiseOrValue if it is a value, or
     * - if promiseOrValue is a promise
     * - fulfilled with promiseOrValue's value after it is fulfilled
     * - rejected with promiseOrValue's reason after it is rejected
     * In contract to cast(x), this always creates a new Promise
     *
     * @param {*} x
     * @return {Promise}
     */
    public AbstractPromise resolve(final Thenable<TResolve, TProgress> x) {
        return promise(new ResolveCallback<TResolve, TProgress>() {
            @Override
            public void run(
                    Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                    Runnable<Void, Value<TResolve>> reject,
                    Runnable<Void, Value<TProgress>> notify) {
                resolve.run(x);
            }
        });
    }

    /**
     * Returns a resolved promise. The returned promise will be
     * - fulfilled with promiseOrValue if it is a value, or
     * - if promiseOrValue is a promise
     * - fulfilled with promiseOrValue's value after it is fulfilled
     * - rejected with promiseOrValue's reason after it is rejected
     * In contract to cast(x), this always creates a new Promise
     *
     * @param {*} x
     * @return {Promise}
     */
    public AbstractPromise resolve(TResolve x) {
        return new FulfilledPromise(x);
    }

    /**
     * Returns a rejected promise for the supplied promiseOrValue.  The returned
     * promise will be rejected with:
     * - promiseOrValue, if it is a value, or
     * - if promiseOrValue is a promise
     * - promiseOrValue's value after it is fulfilled
     * - promiseOrValue's reason after it is rejected
     *
     * @param {*} x the rejected value of the returned promise
     * @return {Promise} rejected promise
     * @deprecated The behavior of when.reject in 3.0 will be to reject
     * with x VERBATIM
     */
    public ProgressPromise<TResolve, TProgress> reject(Thenable<TResolve, TProgress> x) {
        return when(x, new FulfilledCallback<TResolve, TProgress>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(TResolve e) {
                return new RejectedPromise(e);
            }
        });
    }

    /**
     * Returns a rejected promise for the supplied promiseOrValue.  The returned
     * promise will be rejected with:
     * - promiseOrValue, if it is a value, or
     * - if promiseOrValue is a promise
     * - promiseOrValue's value after it is fulfilled
     * - promiseOrValue's reason after it is rejected
     *
     * @param {*} x the rejected value of the returned promise
     * @return {Promise} rejected promise
     * @deprecated The behavior of when.reject in 3.0 will be to reject
     * with x VERBATIM
     */
    public ProgressPromise<TResolve, TProgress> reject(Throwable x) {
        return new RejectedPromise(x);
    }

    /**
     * Returns a rejected promise for the supplied promiseOrValue.  The returned
     * promise will be rejected with:
     * - promiseOrValue, if it is a value, or
     * - if promiseOrValue is a promise
     * - promiseOrValue's value after it is fulfilled
     * - promiseOrValue's reason after it is rejected
     *
     * @param {*} x the rejected value of the returned promise
     * @return {Promise} rejected promise
     * @deprecated The behavior of when.reject in 3.0 will be to reject
     * with x VERBATIM
     */
    public ProgressPromise<TResolve, TProgress> reject(TResolve x) {
        return new RejectedPromise(x);
    }

    /**
     * Creates a {promise, resolver} pair, either or both of which
     * may be given out safely to consumers.
     * The resolver has resolve, reject, and progress.  The promise
     * has then plus extended promise API.
     *
     * @return {{
     * promise: Promise,
     * resolve: function:Promise,
     * reject: function:Promise,
     * notify: function:Promise
     * resolver: {
     * resolve: function:Promise,
     * reject: function:Promise,
     * notify: function:Promise
     * }}}
     */
    public DeferredProgress<TResolve, TProgress> defer() {

        final InternalDeferred deferred = new InternalDeferred();
        final ValueHolder<AbstractPromise> pending = new ValueHolder<>();
        final ValueHolder<Boolean> resolved = new ValueHolder<>();

        ResolveCallback<TResolve, TProgress> makeDeferred = new ResolveCallback<TResolve, TProgress>() {
            @Override
            public void run(
                    final Runnable<Void, Thenable<TResolve, TProgress>> resolvePending,
                    final Runnable<Void, Value<TResolve>> rejectPending,
                    final Runnable<Void, Value<TProgress>> notifyPending) {

                deferred.resolver.resolve = new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                        if (resolved.value) {
                            return resolve(value);
                        }
                        resolved.value = true;
                        resolvePending.run(resolve(value));
                        return pending.value;
                    }
                };

                deferred.resolver.reject = new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                        if (resolved.value) {
                            return resolve(new RejectedPromise(reason));
                        }
                        resolved.value = true;
                        rejectPending.run(reason);
                        return pending.value;
                    }
                };

                deferred.resolver.notify = new Runnable<Value<TProgress>, Value<TProgress>>() {
                    @Override
                    public Value<TProgress> run(Value<TProgress> update) {
                        notifyPending.run(update);
                        return update;
                    }
                };

            }
        };

        deferred.promise = pending.value = promise(makeDeferred);
        return deferred;

    }

    private class InternalDeferred implements DeferredProgress<TResolve, TProgress> {

        InternalDeferred() {
            this.resolver = new InternalResolver();
        }

        private final InternalResolver resolver;
        public AbstractPromise promise;

        @Override
        public Resolver<TResolve, TProgress> getResolver() {
            return resolver;
        }

        @Override
        public ProgressPromise<TResolve, TProgress> getPromise() {
            return promise;
        }

    }

    private class InternalResolver implements Resolver<TResolve, TProgress> {

        public Runnable<ProgressPromise<TResolve, TProgress>, TResolve> resolve;
        public Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>> resolvePromise;
        public Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> reject;
        public Runnable<Value<TProgress>, Value<TProgress>> notify;

        @Override
        public ProgressPromise<TResolve, TProgress> resolve(TResolve value) {
            return resolve.run(value);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> resolve(ProgressPromise<TResolve, TProgress> value) {
            return resolvePromise.run(value);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> reject(TResolve reason) {
            return reject(new Value<>(reason));
        }

        @Override
        public ProgressPromise<TResolve, TProgress> reject(Throwable reason) {
            return reject(new Value<TResolve>(reason));
        }

        @Override
        public ProgressPromise<TResolve, TProgress> reject(Value<TResolve> reason) {
            return reject.run(reason);
        }

        @Override
        public Value<TProgress> notify(TProgress update) {
            return notify(new Value<>(update));
        }

        @Override
        public Value<TProgress> notify(Value<TProgress> update) {
            return notify.run(update);
        }
    }

    /**
     * Run a queue of functions as quickly as possible, passing
     * value to each.
     */
    private <T> void runHandlers(List<Runnable<Void, T>> queue, T value) {
        for (int i = 0; i < queue.size(); i++) {
            queue.get(i).run(value);
        }
    }

    /**
     * Coerces x to a trusted Promise
     *
     * @param {*} x thing to coerce
     * @returns {*} Guaranteed to return a trusted Promise.  If x
     * is trusted, returns x, otherwise, returns a new, trusted, already-resolved
     * Promise whose resolution value is:
     * * the resolution value of x if it's a foreign promise, or
     * * x if it's a value
     */
    private AbstractPromise coerce(Promise0 self, Thenable<TResolve, TProgress> x) {
        if (x == self) {
            return new RejectedPromise(new IllegalArgumentException());
        }

        if (AbstractPromise.class.isInstance(x)) {
            return (AbstractPromise) x;
        }

        try {
            Thenable<TResolve, TProgress> untrustedThen = x;
            return (untrustedThen != null ? assimilate(untrustedThen) : new FulfilledPromise(null));

        } catch (Throwable e) {
            return new RejectedPromise(e);
        }
    }

    /**
     * Safely assimilates a foreign thenable by wrapping it in a trusted promise
     *
     * @param {function}        untrustedThen x's then() method
     * @param {object|function} x thenable
     * @returns {Promise}
     */
    private AbstractPromise assimilate(final Thenable<TResolve, TProgress> untrustedThen) {

        return promise(new ResolveCallback<TResolve, TProgress>() {
            @Override
            public void run(
                    final Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                    final Runnable<Void, Value<TResolve>> reject,
                    Runnable<Void, Value<TProgress>> notify) {

                enqueue(new Runnable<Void, Void>() {
                    @Override
                    public Void run(Void aVoid) {
                        try {
                            untrustedThen.then(
                                    new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                                        @Override
                                        public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                                            resolve.run(resolve(value));
                                            return null;
                                        }
                                    },
                                    new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                                        @Override
                                        public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                                            reject.run(value);
                                            return null;
                                        }
                                    }, null
                            );
                        } catch (Throwable e) {
                            reject(e);
                        }
                        return null;
                    }
                });
            }
        });
    }

    /**
     * Creates a fulfilled, local promise as a proxy for a value
     * NOTE: must never be exposed
     *
     * @param {*} value fulfillment value
     * @private
     * @returns {Promise}
     */
    private class FulfilledPromise extends AbstractPromise {

        private TResolve value;

        public FulfilledPromise(TResolve value) {
            this.value = value;
        }

        @Override
        public PromiseState<TResolve> inspect() {
            return toFulfilledState(this.value);
        }

        ;

        @Override
        protected void _when(
                Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                Runnable<Void, Value<TProgress>> notify,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            try {
                resolve.run(onFulfilled != null ? onFulfilled.run(this.value) : resolve(this.value));
            } catch (Throwable e) {
                resolve.run(new RejectedPromise(e));
            }

        }

    }

    /**
     * Creates a rejected, local promise as a proxy for a value
     * NOTE: must never be exposed
     *
     * @param {*} reason rejection reason
     * @private
     * @returns {Promise}
     */
    private class RejectedPromise extends AbstractPromise {

        private Value<TResolve> value;

        public RejectedPromise(Value<TResolve> reason) {
            this.value = reason;
        }

        public RejectedPromise(TResolve reason) {
            this.value = new Value<>(reason);
        }

        public RejectedPromise(Throwable reason) {
            this.value = new Value<>(reason);
        }

//        RejectedPromise.prototype = makePromisePrototype(promisePrototype);

        /**
         * Private message delivery. Queues and delivers messages to
         * the promise's ultimate fulfillment value or rejection reason.
         *
         * @param resolve
         * @param notify
         * @param onFulfilled
         * @param onRejected
         * @param onProgress
         * @private
         */
        @Override
        protected void _when(
                Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                Runnable<Void, Value<TProgress>> notify,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            try {
                resolve.run(onRejected != null ? onRejected.run(this.value) : this);
            } catch (Throwable e) {
                resolve.run(new RejectedPromise(e));
            }

        }

        @Override
        public PromiseState<TResolve> inspect() {
            return toRejectedState(this.value);
        }

        ;

    }

    /**
     * Create a progress promise with the supplied update.
     *
     * @param {*} value progress update value
     * @private
     * @return {Promise} progress promise
     */
    private class ProgressingPromise extends AbstractPromise {

        private Value<TProgress> value;

        public ProgressingPromise(Value<TProgress> value) {
            this.value = value;
        }

        /**
         * Private message delivery. Queues and delivers messages to
         * the promise's ultimate fulfillment value or rejection reason.
         *
         * @param resolve
         * @param notify
         * @param onFulfilled
         * @param onRejected
         * @param onProgress
         * @private
         */
        @Override
        protected void _when(
                Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                Runnable<Void, Value<TProgress>> notify,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            try {
                notify.run(onProgress != null ? onProgress.run(this.value) : this.value);
            } catch (Throwable e) {
                notify.run(new Value<TProgress>(e));
            }

        }

        @Override
        public PromiseState<TResolve> inspect() {
            return toPendingState();
        }

    }

    /**
     * Update a PromiseStatus monitor object with the outcome
     * of the supplied value promise.
     *
     * @param {Promise}       value
     * @param {PromiseStatus} status
     */
    private void updateStatus(AbstractPromise value, final PromiseStatus status) {

        value.then(
                new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                        status.fulfilled();
                        return null;
                    }
                },
                new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(Value<TResolve> r) {
                        status.rejected(r.getCause());
                        return null;
                    }
                }
        );

    }

//    /**
//     * Determines if x is promise-like, i.e. a thenable object
//     * NOTE: Will return true for *any thenable object*, and isn't truly
//     * safe, since it may attempt to access the `then` property of x (i.e.
//     *  clever/malicious getters may do weird things)
//     * @param {*} x anything
//     * @returns {boolean} true if x is promise-like
//     */
//    function isPromiseLike(x) {
//        return x && typeof x.then === 'function';
//    }

//    /**
//     * Initiates a competitive race, returning a promise that will resolve when
//     * howMany of the supplied promisesOrValues have resolved, or will reject when
//     * it becomes impossible for howMany to resolve, for example, when
//     * (promisesOrValues.length - howMany) + 1 input promises reject.
//     *
//     * @param {Array} promisesOrValues array of anything, may contain a mix
//     *      of promises and values
//     * @param howMany {number} number of promisesOrValues to resolve
//     * @param {function?} [onFulfilled] DEPRECATED, use returnedPromise.then()
//     * @param {function?} [onRejected] DEPRECATED, use returnedPromise.then()
//     * @param {function?} [onProgress] DEPRECATED, use returnedPromise.then()
//     * @returns {Promise} promise that will resolve to an array of howMany values that
//     *  resolved first, or will reject with an array of
//     *  (promisesOrValues.length - howMany) + 1 rejection reasons.
//     */
//    function some(promisesOrValues, howMany, onFulfilled, onRejected, onProgress) {
//
//        return when(promisesOrValues, function(promisesOrValues) {
//
//            return promise(resolveSome).then(onFulfilled, onRejected, onProgress);
//
//            function resolveSome(resolve, reject, notify) {
//                var toResolve, toReject, values, reasons, fulfillOne, rejectOne, len, i;
//
//                len = promisesOrValues.length >>> 0;
//
//                toResolve = Math.max(0, Math.min(howMany, len));
//                values = [];
//
//                toReject = (len - toResolve) + 1;
//                reasons = [];
//
//                // No items in the input, resolve immediately
//                if (!toResolve) {
//                    resolve(values);
//
//                } else {
//                    rejectOne = function(reason) {
//                        reasons.push(reason);
//                        if(!--toReject) {
//                            fulfillOne = rejectOne = identity;
//                            reject(reasons);
//                        }
//                    };
//
//                    fulfillOne = function(val) {
//                        // This orders the values based on promise resolution order
//                        values.push(val);
//                        if (!--toResolve) {
//                            fulfillOne = rejectOne = identity;
//                            resolve(values);
//                        }
//                    };
//
//                    for(i = 0; i < len; ++i) {
//                        if(i in promisesOrValues) {
//                            when(promisesOrValues[i], fulfiller, rejecter, notify);
//                        }
//                    }
//                }
//
//                function rejecter(reason) {
//                        rejectOne(reason);
//                }
//
//                function fulfiller(val) {
//                        fulfillOne(val);
//                }
//            }
//        });
//    }
//
//    /**
//     * Initiates a competitive race, returning a promise that will resolve when
//     * any one of the supplied promisesOrValues has resolved or will reject when
//     * *all* promisesOrValues have rejected.
//     *
//     * @param {Array|Promise} promisesOrValues array of anything, may contain a mix
//     *      of {@link Promise}s and values
//     * @param {function?} [onFulfilled] DEPRECATED, use returnedPromise.then()
//     * @param {function?} [onRejected] DEPRECATED, use returnedPromise.then()
//     * @param {function?} [onProgress] DEPRECATED, use returnedPromise.then()
//     * @returns {Promise} promise that will resolve to the value that resolved first, or
//     * will reject with an array of all rejected inputs.
//     */
//    function any(promisesOrValues, onFulfilled, onRejected, onProgress) {
//
//        function unwrapSingleResult(val) {
//        return onFulfilled ? onFulfilled(val[0]) : val[0];
//        }
//
//        return some(promisesOrValues, 1, unwrapSingleResult, onRejected, onProgress);
//    }
//
//    /**
//     * Return a promise that will resolve only once all the supplied promisesOrValues
//     * have resolved. The resolution value of the returned promise will be an array
//     * containing the resolution values of each of the promisesOrValues.
//     * @memberOf when
//     *
//     * @param {Array|Promise} promisesOrValues array of anything, may contain a mix
//     *      of {@link Promise}s and values
//     * @param {function?} [onFulfilled] DEPRECATED, use returnedPromise.then()
//     * @param {function?} [onRejected] DEPRECATED, use returnedPromise.then()
//     * @param {function?} [onProgress] DEPRECATED, use returnedPromise.then()
//     * @returns {Promise}
//     */
//    function all(promisesOrValues, onFulfilled, onRejected, onProgress) {
//        return _map(promisesOrValues, identity).then(onFulfilled, onRejected, onProgress);
//    }
//
//    /**
//     * Joins multiple promises into a single returned promise.
//     * @return {Promise} a promise that will fulfill when *all* the input promises
//     * have fulfilled, or will reject when *any one* of the input promises rejects.
//     */
//    function join(/* ...promises */) {
//        return _map(arguments, identity);
//    }
//
//    /**
//     * Settles all input promises such that they are guaranteed not to
//     * be pending once the returned promise fulfills. The returned promise
//     * will always fulfill, except in the case where `array` is a promise
//     * that rejects.
//     * @param {Array|Promise} array or promise for array of promises to settle
//     * @returns {Promise} promise that always fulfills with an array of
//     *  outcome snapshots for each input promise.
//     */
//    function settle(array) {
//        return _map(array, toFulfilledState, toRejectedState);
//    }
//
//    /**
//     * Promise-aware array map function, similar to `Array.prototype.map()`,
//     * but input array may contain promises or values.
//     * @param {Array|Promise} array array of anything, may contain promises and values
//     * @param {function} mapFunc map function which may return a promise or value
//     * @returns {Promise} promise that will fulfill with an array of mapped values
//     *  or reject if any input promise rejects.
//     */
//    function map(array, mapFunc) {
//        return _map(array, mapFunc);
//    }
//
//    /**
//     * Internal map that allows a fallback to handle rejections
//     * @param {Array|Promise} array array of anything, may contain promises and values
//     * @param {function} mapFunc map function which may return a promise or value
//     * @param {function?} fallback function to handle rejected promises
//     * @returns {Promise} promise that will fulfill with an array of mapped values
//     *  or reject if any input promise rejects.
//     */
//    function _map(array, mapFunc, fallback) {
//        return when(array, function(array) {
//
//            return new Promise(resolveMap);
//
//            function resolveMap(resolve, reject, notify) {
//                var results, len, toResolve, i;
//
//                // Since we know the resulting length, we can preallocate the results
//                // array to avoid array expansions.
//                toResolve = len = array.length >>> 0;
//                results = [];
//
//                if(!toResolve) {
//                    resolve(results);
//                    return;
//                }
//
//                // Since mapFunc may be async, get all invocations of it into flight
//                for(i = 0; i < len; i++) {
//                    if(i in array) {
//                        resolveOne(array[i], i);
//                    } else {
//                        --toResolve;
//                    }
//                }
//
//                function resolveOne(item, i) {
//                    when(item, mapFunc, fallback).then(function(mapped) {
//                        results[i] = mapped;
//
//                        if(!--toResolve) {
//                            resolve(results);
//                        }
//                    }, reject, notify);
//                }
//            }
//        });
//    }
//
//    /**
//     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
//     * input may contain promises and/or values, and reduceFunc
//     * may return either a value or a promise, *and* initialValue may
//     * be a promise for the starting value.
//     *
//     * @param {Array|Promise} promise array or promise for an array of anything,
//     *      may contain a mix of promises and values.
//     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
//     *      where total is the total number of items being reduced, and will be the same
//     *      in each call to reduceFunc.
//     * @returns {Promise} that will resolve to the final reduced value
//     */
//    function reduce(promise, reduceFunc /*, initialValue */) {
//        var args = fcall(slice, arguments, 1);
//
//        return when(promise, function(array) {
//            var total;
//
//            total = array.length;
//
//            // Wrap the supplied reduceFunc with one that handles promises and then
//            // delegates to the supplied.
//            args[0] = function (current, val, i) {
//                return when(current, function (c) {
//                    return when(val, function (value) {
//                        return reduceFunc(c, value, i, total);
//                    });
//                });
//            };
//
//            return reduceArray.apply(array, args);
//        });
//    }

    // Snapshot states

    /**
     * Creates a fulfilled state snapshot
     *
     * @param {*} x any value
     * @private
     * @returns {{state:'fulfilled',value:*}}
     */
    public PromiseState<TResolve> toFulfilledState(TResolve x) {
        return new PromiseState<TResolve>()
                .setState(PromiseState.State.FULFILLED)
                .setValue(x);
    }

    /**
     * Creates a rejected state snapshot
     *
     * @param {*} x any reason
     * @private
     * @returns {{state:'rejected',reason:*}}
     */
    public PromiseState<TResolve> toRejectedState(Value<TResolve> x) {
        return new PromiseState<TResolve>()
                .setState(PromiseState.State.REJECTED)
                .setValue(x.getValue())
                .setReason(x.getCause());
    }

    /**
     * Creates a pending state snapshot
     *
     * @private
     * @returns {{state:'pending'}}
     */
    public PromiseState<TResolve> toPendingState() {
        return new PromiseState<TResolve>()
                .setState(PromiseState.State.PENDING);
    }

    //
    // Shared handler queue processing
    //
    // Credit to Twisol (https://github.com/Twisol) for suggesting
    // this type of extensible queue + trampoline approach for
    // next-tick conflation.

    List<Runnable<Void, Void>> handlerQueue = new ArrayList<>();

    /**
     * Enqueue a task. If the queue is not currently scheduled to be
     * drained, schedule it.
     *
     * @param {function} task
     */
    public void enqueue(Runnable<Void, Void> task) {
        // TODO: Make thread safe
        handlerQueue.add(task);
        if (handlerQueue.size() == 1) {
            nextTick.execute(new java.lang.Runnable() {
                @Override
                public void run() {
                    drainQueue();
                }
            });
        }
    }

    /**
     * Drain the handler queue entirely, being careful to allow the
     * queue to be extended while it is being processed, and to continue
     * processing until it is truly empty.
     */
    public void drainQueue() {
        // TODO: Make thread safe
        List<Runnable<Void, Void>> q = handlerQueue;
        handlerQueue = new ArrayList<>();
        runHandlers(q, null);
    }

    // Allow attaching the monitor to when() if env has no console
    private static MonitorApi monitorApi;

    private static Executor nextTick = new Executor() {
        @Override
        public void execute(java.lang.Runnable command) {
            command.run();
        }
    };

    public static void setNextTick(Executor executor) {
        nextTick = executor;
    }


    //
    // Capture/polyfill function and array utils
    //

//    // ES5 reduce implementation if native not available
//    // See: http://es5.github.com/#x15.4.4.21 as there are many
//    // specifics and edge cases.  ES5 dictates that reduce.length === 1
//    // This implementation deviates from ES5 spec in the following ways:
//    // 1. It does not check if reduceFunc is a Callable
//    reduceArray = arrayProto.reduce ||
//    function(reduceFunc /*, initialValue */) {
//			/*jshint maxcomplexity: 7*/
//        var arr, args, reduced, len, i;
//
//        i = 0;
//        arr = Object(this);
//        len = arr.length >>> 0;
//        args = arguments;
//
//        // If no initialValue, use first item of array (we know length !== 0 here)
//        // and adjust i to start at second item
//        if(args.length <= 1) {
//            // Skip to the first real element in the array
//            for(;;) {
//                if(i in arr) {
//                    reduced = arr[i++];
//                    break;
//                }
//
//                // If we reached the end of the array without finding any real
//                // elements, it's a TypeError
//                if(++i >= len) {
//                    throw new TypeError();
//                }
//            }
//        } else {
//            // If initialValue provided, use it
//            reduced = args[1];
//        }
//
//        // Do the actual reduce
//        for(;i < len; ++i) {
//            if(i in arr) {
//                reduced = reduceFunc(reduced, arr[i], i, arr);
//            }
//        }
//
//        return reduced;
//    };

//    function identity(x) {
//        return x;
//    }

    private void crash(Throwable fatalError) {
        monitorApi.reportUnhandled();
        throw new RuntimeException(fatalError);
    }
}
