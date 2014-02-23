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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Java implementation of the CommonJS Promises/A specification.
 * http://wiki.commonjs.org/wiki/Promises/A
 * <p/>
 * Based on the when.js 2.8.0 library (c) copyright B Cavalier & J Hann (https://github.com/cujojs/when)
 *
 * @param <TResolve>  the type passed to fulfillment or rejection handlers
 * @param <TProgress> the type passed to progress handlers
 */
public class WhenProgress<TResolve, TProgress> {


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
    protected Promise0 promise(ResolveCallback<TResolve, TProgress> resolver) {
        return new Promise0(resolver, monitorApi.promiseStatus());
    }

    protected class Promise0 extends TrustedPromise {

        private ValueHolder<TrustedPromise> value = new ValueHolder<>();
        private ValueHolder<List<Runnable<Void, TrustedPromise>>> consumers = new ValueHolder<>();

        /**
         * Trusted Promise constructor.  A Promise created from this constructor is
         * a trusted when.js promise.  Any other duck-typed promise is considered
         * untrusted.
         *
         * @constructor
         * @returns {Promise} promise whose fate is determine by resolver
         * @name Promise
         */
        protected Promise0(ResolveCallback<TResolve, TProgress> resolver, final PromiseStatus status) {

            final Promise0 self = this;
            this._status = status;
//            this.inspect = inspect;
//            this._when = _when;
            consumers.value = new ArrayList<>();

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
                    final List<Runnable<Void, TrustedPromise>> queue = consumers.value;
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
                    final List<Runnable<Void, TrustedPromise>> queue = consumers.value;
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

            final Runnable<Void, TrustedPromise> deliver = new Runnable<Void, TrustedPromise>() {
                @Override
                public Void run(TrustedPromise p) {
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

    protected abstract class TrustedPromise implements PromiseExt<TResolve, TProgress> {

        protected PromiseStatus _status;

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

        @Override
        public abstract PromiseState<TResolve> inspect();

        /**
         * Register handlers for this promise.
         *
         * @param onFulfilled {Function} fulfillment handler
         * @param onRejected  {Function} rejection handler
         * @param onProgress  {Function} progress handler
         * @return {Promise} new Promise
         */
        @Override
        public TrustedPromise then(
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                final Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            final TrustedPromise self = this;

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
        public TrustedPromise then(
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        /**
         * Register handlers for this promise.
         *
         * @param onFulfilled {Function} fulfillment handler
         * @return {Promise} new Promise
         */
        @Override
        public TrustedPromise then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        /**
         * Register a rejection handler.  Shortcut for .then(undefined, onRejected)
         *
         * @param {function?} onRejected
         * @return {Promise}
         */
        @Override
        public TrustedPromise otherwise(Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return this.then(null, onRejected, null);
        }

        private TrustedPromise catch0(Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return otherwise(onRejected);
        }

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
        public TrustedPromise ensure(final Runnable<? extends ProgressPromise<TResolve, TProgress>, Void> onFulfilledOrRejected) {

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
        public TrustedPromise done(
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> handleResult,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> handleError) {

            return this.then(handleResult, handleError)
                    .catch0(
                            new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
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
        public TrustedPromise yield(final Thenable<TResolve, TProgress> value) {
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
        public TrustedPromise tap(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilledSideEffect) {
            return this.then(onFulfilledSideEffect).yield(this);
        }

// TODO: Does spread make sense in java?
//        /**
//         * Assumes that this promise will fulfill with an array, and arranges
//         * for the onFulfilled to be called with the array as its argument list
//         * i.e. onFulfilled.apply(undefined, array).
//         * @param {function} onFulfilled function to receive spread arguments
//         * @return {Promise}
//         */
//        promisePrototype.spread = function(onFulfilled) {
//            return this.then(function(array) {
//                // array may contain promises, so resolve its contents.
//                return all(array, function(array) {
//                    return onFulfilled.apply(undef, array);
//                });
//            });
//        };

        /**
         * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected)
         */
        @Override
        public ProgressPromise<TResolve, TProgress> always(
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilledOrRejected) {
            return always(onFulfilledOrRejected, null);
        }

        /**
         * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected)
         */
        @Override
        public TrustedPromise always(
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

    }

    /**
     * Casts x to a trusted promise. If x is already a trusted promise, it is
     * returned, otherwise a new trusted Promise which follows x is returned.
     *
     * @param {*} x
     * @returns {Promise}
     */
    private TrustedPromise cast(Thenable<TResolve, TProgress> x) {
        return (TrustedPromise.class.isInstance(x)) ? (TrustedPromise) x : resolve0(x);
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
    public ProgressPromise<TResolve, TProgress> resolve(final Thenable<TResolve, TProgress> x) {
        return resolve0(x);
    }

    private TrustedPromise resolve0(final Thenable<TResolve, TProgress> x) {
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
    public ProgressPromise<TResolve, TProgress> resolve(TResolve x) {
        return resolve0(x);
    }

    private TrustedPromise resolve0(final TResolve x) {
        return promise(new ResolveCallback<TResolve, TProgress>() {
            @Override
            public void run(
                    Runnable<Void, Thenable<TResolve, TProgress>> resolve,
                    Runnable<Void, Value<TResolve>> reject,
                    Runnable<Void, Value<TProgress>> notify) {
                resolve.run(new FulfilledPromise(x));
                // TODO: Revisit this, does not match when.js, should not create a FulfilledPromise
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
     */
    public ProgressPromise<TResolve, TProgress> reject(Thenable<TResolve, TProgress> x) {
        return when(x, new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
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
     */
    public ProgressPromise<TResolve, TProgress> reject(Value<TResolve> x) {
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
     */
    public ProgressPromise<TResolve, TProgress> reject(TResolve x) {
        return new RejectedPromise(x);
    }

    /**
     * Creates a {promise, resolver} pair, either or both of which
     * may be given out safely to consumers.
     * The resolver has resolve, reject, and progress.  The promise
     * has the extended promise API.
     */
    public DeferredProgress<TResolve, TProgress> defer() {
        return defer0();
    }

    private InternalDeferred defer0() {

        final InternalDeferred deferred = new InternalDeferred();
        final ValueHolder<TrustedPromise> pending = new ValueHolder<>();
        final ValueHolder<Boolean> resolved = new ValueHolder<>(false);

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

                deferred.resolver.resolvePromise = new Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(ProgressPromise<TResolve, TProgress> value) {
                        if (resolved.value) {
                            return resolve(value);
                        }
                        resolved.value = true;
                        resolvePending.run(value);
                        return pending.value;
                    }
                };

                deferred.resolver.reject = new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                        if (resolved.value) {
                            return resolve(reject(reason));
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
        private TrustedPromise promise;

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

        private Runnable<ProgressPromise<TResolve, TProgress>, TResolve> resolve;
        private Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>> resolvePromise;
        private Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> reject;
        private Runnable<Value<TProgress>, Value<TProgress>> notify;

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
    private TrustedPromise coerce(Promise0 self, Thenable<TResolve, TProgress> x) {
        if (x == self) {
            return new RejectedPromise(new IllegalArgumentException());
        }

        if (TrustedPromise.class.isInstance(x)) {
            return (TrustedPromise) x;
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
    private TrustedPromise assimilate(final Thenable<TResolve, TProgress> untrustedThen) {

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
                            reject.run(new Value<TResolve>(e));
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
    private class FulfilledPromise extends TrustedPromise {

        private TResolve value;

        public FulfilledPromise(TResolve value) {
            this.value = value;
        }

        @Override
        public PromiseState<TResolve> inspect() {
            return toFulfilledState(this.value);
        }

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
    private class RejectedPromise extends TrustedPromise {

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

    }

    /**
     * Create a progress promise with the supplied update.
     *
     * @param {*} value progress update value
     * @private
     * @return {Promise} progress promise
     */
    private class ProgressingPromise extends TrustedPromise {

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
    private void updateStatus(TrustedPromise value, final PromiseStatus status) {

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

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array} promisesOrValues array of anything, may contain a mix
     *                of promises and values
     * @param howMany {number} number of promisesOrValues to resolve
     * @returns {Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of
     * (promisesOrValues.length - howMany) + 1 rejection reasons.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> some(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final int howMany) {

        final WhenProgress<List<? extends TResolve>, TProgress> w1 = new WhenProgress<>();
        final List<TResolve> values = new ArrayList<>();

        // No items in the input, resolve immediately
        if (promises == null || promises.isEmpty()) {
            return w1.resolve(values);
        }

        final WhenProgress<List<? extends TResolve>, TProgress>.InternalDeferred d1 = w1.defer0();

        int len = promises.size();
        final ValueHolder<AtomicInteger> toResolve = new ValueHolder<>(new AtomicInteger(Math.max(0, Math.min(howMany, len))));

        final ValueHolder<AtomicInteger> toReject = new ValueHolder<>(new AtomicInteger(len - toResolve.value.get() + 1));
        final List<TResolve> reasons = new ArrayList<>();

        Runnable<Value<TProgress>, Value<TProgress>> notify = d1.resolver.notify;

        final ValueHolder<Runnable<ProgressPromise<TResolve, TProgress>, TResolve>> fulfillOne = new ValueHolder<>();
        final ValueHolder<Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>> rejectOne = new ValueHolder<>();

        rejectOne.value = new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                reasons.add(reason.getValue());

                if (toReject.value.decrementAndGet() == 0) {
                    rejectOne.value = null;
                    fulfillOne.value = null;
                    d1.getResolver().reject(new Value<List<? extends TResolve>>(reasons, reason.getCause()));
                }

                return null;
            }
        };

        fulfillOne.value = new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                // This orders the values based on promise resolution order
                // Another strategy would be to use the original position of
                // the corresponding promise.
                values.add(value);

                if (toResolve.value.decrementAndGet() == 0) {
                    fulfillOne.value = null;
                    rejectOne.value = null;
                    d1.getResolver().resolve(values);
                }

                return null;
            }
        };

        Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> rejecter = new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                return (rejectOne.value != null ? rejectOne.value.run(value) : null);
            }
        };
        Runnable<ProgressPromise<TResolve, TProgress>, TResolve> fulfiller = new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                return (fulfillOne.value != null ? fulfillOne.value.run(value) : null);
            }
        };

        for (int i = 0; i < len; ++i) {
            if (i < promises.size()) {
                when(promises.get(i), fulfiller, rejecter, notify);
            }
        }


//                return null;
//            }
//        }).then(null, new Runnable<ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress>, Value<List<? extends ProgressPromise<TResolve, TProgress>>>>() {
//            @Override
//            public ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> run(Value<List<? extends ProgressPromise<TResolve, TProgress>>> value) {
//                // Need to reject the deferred if an exception is thrown above
//                d1.getResolver().reject(value.getCause());
//                return null;
//            }
//        });

        return d1.getPromise();
    }

    /**
     * Resolves immediately returning a resolved {@link ProgressPromise} with the specified number of values.
     *
     * @param values  a list of resolved values
     * @param howMany number of promises to resolve
     * @return a resolved {@link ProgressPromise} with howMany values.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> someValues(
            final List<TResolve> values,
            final int howMany) {
        return some(resolveValues(values), howMany);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promises has resolved or will reject when
     * *all* promises have rejected.
     *
     * @param {Array|Promise} promises list
     * @returns {Promise} promise that will resolve to the value that resolved first, or
     * will reject with an array of all rejected inputs.
     */
    public ProgressPromise<TResolve, TProgress> any(final List<? extends ProgressPromise<TResolve, TProgress>> promises) {
        final DeferredProgress<TResolve, TProgress> d = defer();

        some(promises, 1).then(
                new Runnable<ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>>() {
                    @Override
                    public ProgressPromise<List<? extends TResolve>, TProgress> run(List<? extends TResolve> value) {
                        TResolve val = null;

                        if (value.size() > 0) {
                            val = value.get(0);
                        }

                        d.getResolver().resolve(val);
                        return null;
                    }
                },
                new Runnable<ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>>() {
                    @Override
                    public ProgressPromise<List<? extends TResolve>, TProgress> run(Value<List<? extends TResolve>> value) {
                        d.getResolver().reject(value.getCause());
                        return null;
                    }
                },
                new Runnable<Value<TProgress>, Value<TProgress>>() {
                    @Override
                    public Value<TProgress> run(Value<TProgress> value) {
                        d.getResolver().notify(value);
                        return null;
                    }
                }
        );

        return d.getPromise();
    }

    /**
     * Resolves immediately returning a resolved {@link ProgressPromise} with the first value from the input list.
     *
     * @param values the values
     * @return an already-fulfilled {@link ProgressPromise} with the first value from the input list.
     */
    public ProgressPromise<TResolve, TProgress> anyValues(final List<TResolve> values) {
        return any(resolveValues(values));
    }

    /**
     * Return a promise that will resolve only once all the supplied promises
     * have resolved. The resolution value of the returned promise will be a list
     * containing the resolution values of each of the promises.
     *
     * @param promises input promises to resolve
     * @return a {@link ProgressPromise} that resolves when all the input promises have resolved
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> all(List<? extends ProgressPromise<TResolve, TProgress>> promises) {
        return _map(promises, identity, null);
    }

    /**
     * Return a resolved promise for the list of input values.
     *
     * @param values input values
     * @return a resolved {@link ProgressPromise}
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> allValues(List<TResolve> values) {
        return all(resolveValues(values));
    }

    /**
     * Joins multiple promises into a single returned promise.
     *
     * @param promises the promises to join
     * @return {Promise} a promise that will fulfill when *all* the input promises
     * have fulfilled, or will reject when *any one* of the input promises rejects.
     */
    @SafeVarargs
    public final ProgressPromise<List<? extends TResolve>, TProgress> join(ProgressPromise<TResolve, TProgress>... promises) {
        List<ProgressPromise<TResolve, TProgress>> input = (promises == null ? new ArrayList<ProgressPromise<TResolve, TProgress>>()
                : Arrays.asList(promises));
        return _map(input, identity, null);
    }

    /**
     * Joins multiple promises into a single returned promise.
     *
     * @param values the values to join
     * @return {Promise} a promise that will fulfill when *all* the input promises
     * have fulfilled, or will reject when *any one* of the input promises rejects.
     */
    @SafeVarargs
    public final ProgressPromise<List<? extends TResolve>, TProgress> join(TResolve... values) {
        List<TResolve> input = (values == null ? new ArrayList<TResolve>() : Arrays.asList(values));
        return _map(resolveValues(input), identity, null);
    }

    // TODO: Implement settle()
//    /**
//     * Settles all input promises such that they are guaranteed not to
//     * be pending once the returned promise fulfills. The returned promise
//     * will always fulfill, except in the case where `array` is a promise
//     * that rejects.
//     * @param {Array|Promise} array or promise for array of promises to settle
//     * @returns {Promise} promise that always fulfills with an array of
//     *  outcome snapshots for each input promise.
//     */
//    public ProgressPromise<List<? extends PromiseState<TResolve>>, TProgress> settle(List<ProgressPromise<TResolve, TProgress>> array) {
//        return _map(array, toFulfilledState, toRejectedState);
//    }

    /**
     * Promise-aware array map function, similar to `Array.prototype.map()`,
     * but input array may contain promises or values.
     *
     * @param {Array|Promise} array array of anything, may contain promises and values
     * @param {function}      mapFunc map function which may return a promise or value
     * @returns {Promise} promise that will fulfill with an array of mapped values
     * or reject if any input promise rejects.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> map(
            List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> mapFunc
    ) {
        return _map(promises, mapFunc, null);
    }

    /**
     * Traditional map function, but the input is a {@link ProgressPromise} for values to be mapped.
     *
     * @param promise a {@link ProgressPromise} for a list of values to be mapped
     * @param mapFunc a mapping function that returns a promise for a value
     * @return a {@link ProgressPromise} that will resolve to a list containing the mapped output values.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> map(
            ProgressPromise<List<? extends TResolve>, TProgress> promise,
            final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> mapFunc) {

        WhenProgress<List<? extends TResolve>, TProgress> when = new WhenProgress<>();
        return when.when(promise, new Runnable<ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>>() {
            @Override
            public ProgressPromise<List<? extends TResolve>, TProgress> run(List<? extends TResolve> values) {
                return _map(resolveValues(values), mapFunc, null);
            }
        });

    }

    /**
     * Internal map that allows a fallback to handle rejections
     *
     * @param {Array|Promise} array of anything, may contain promises and values
     * @param {function}      mapFunc map function which may return a promise or value
     * @param {function?}     fallback function to handle rejected promises
     * @returns {Promise} promise that will fulfill with an array of mapped values
     * or reject if any input promise rejects.
     */
    private ProgressPromise<List<? extends TResolve>, TProgress> _map(
            List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> mapFunc,
            final Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> fallback) {

        final WhenProgress<List<? extends TResolve>, TProgress> w = new WhenProgress<>();

        if (promises == null) {
            return w.reject(new RuntimeException("promises were null"));
        } else if (promises.isEmpty()) {
            return w.resolve(new ArrayList<TResolve>());
        }

        int len = promises.size();
        final ValueHolder<AtomicInteger> toResolve = new ValueHolder<>(new AtomicInteger(len));
        final DeferredProgress<List<? extends TResolve>, TProgress> d = w.defer();

        // Since we know the resulting length, we can preallocate the results array to avoid array expansions.
        // Pre-populate null values to allow us to set the value at an index
        final List<TResolve> results = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            results.add(null);
        }

        Runnable2<Void, ProgressPromise<TResolve, TProgress>, Integer> resolveOne;
        resolveOne = new Runnable2<Void, ProgressPromise<TResolve, TProgress>, Integer>() {
            @Override
            public Void run(ProgressPromise<TResolve, TProgress> item, final Integer index) {
                when(item, mapFunc, fallback).then(
                        new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                            @Override
                            public ProgressPromise<TResolve, TProgress> run(TResolve mapped) {
                                results.set(index, mapped);

                                if (toResolve.value.decrementAndGet() == 0) {
                                    d.getResolver().resolve(results);
                                }
                                return null;
                            }
                        },
                        new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                            @Override
                            public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                                d.getResolver().reject(new Value<List<? extends TResolve>>(Arrays.asList(value.getValue()), value.getCause()));
                                return null;
                            }
                        },
                        new Runnable<Value<TProgress>, Value<TProgress>>() {
                            @Override
                            public Value<TProgress> run(Value<TProgress> update) {
                                d.getResolver().notify(update);
                                return null;
                            }
                        }
                );
                return null;
            }
        };

        // Since mapFunc may be async, get all invocations of it into flight
        for (int i = 0; i < len; i++) {
            resolveOne.run(promises.get(i), i);
        }

        return d.getPromise();
    }

    //    /**
//     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
//     * input may contain promises and/or values, and reduceFunc
//     * may return either a value or a promise, *and* initialValue may
//     * be a promise for the starting value.
//     *
//     * @param {Array|Promise} promise array or promise for an array of anything,
//     * may contain a mix of promises and values.
//     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
//     * where total is the total number of items being reduced, and will be the same
//     * in each call to reduceFunc.
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
//            args[0] = function(current, val, i) {
//                return when(current, function(c) {
//                    return when(val, function(value) {
//                        return reduceFunc(c, value, i, total);
//                    });
//                });
//            } ;
//
//            return reduceArray.apply(array, args);
//        });
//    }

    /**
     * Traditional reduce function, but the input is a list of {@link ProgressPromise}s.
     *
     * @param promises   list of {@link ProgressPromise}s for values to reduce
     * @param reduceFunc the reduce function
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduce(
            List<ProgressPromise<TResolve, TProgress>> promises,
            final Reducer<T, TResolve> reduceFunc) {
        return reduce0(promises, reduceFunc, null);
    }

    /**
     * Traditional reduce function, but the input is a list of {@link ProgressPromise}s.
     *
     * @param promises     list of {@link ProgressPromise}s for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduce(
            List<ProgressPromise<TResolve, TProgress>> promises,
            final Reducer<T, TResolve> reduceFunc,
            T initialValue) {
        WhenProgress<T, TProgress> when = new WhenProgress<>();
        return reduce(promises, reduceFunc, when.resolve(initialValue));
    }

    /**
     * Traditional reduce function, but the input is a list of {@link ProgressPromise}s.
     *
     * @param promises     list of {@link ProgressPromise}s for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduce(
            List<ProgressPromise<TResolve, TProgress>> promises,
            final Reducer<T, TResolve> reduceFunc,
            final ProgressPromise<T, TProgress> initialValue) {
        return reduce0(promises, reduceFunc, new Value<>(initialValue));
    }

    /**
     * Traditional reduce function, but the input is a {@link ProgressPromise} for a list of values.
     *
     * @param promise      a {@link ProgressPromise} for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduce(
            ProgressPromise<List<TResolve>, TProgress> promise,
            final Reducer<T, TResolve> reduceFunc,
            final T initialValue) {
        WhenProgress<T, TProgress> when = new WhenProgress<>();
        return reduce(promise, reduceFunc, when.resolve(initialValue));
    }

    /**
     * Traditional reduce function, but the input is a {@link ProgressPromise} for a list of values.
     *
     * @param promise      a {@link ProgressPromise} for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduce(
            ProgressPromise<List<TResolve>, TProgress> promise,
            final Reducer<T, TResolve> reduceFunc,
            final ProgressPromise<T, TProgress> initialValue) {

        final DeferredProgress<T, TProgress> d1 = new WhenProgress<T, TProgress>().defer();
        final WhenProgress<List<TResolve>, TProgress> w2 = new WhenProgress<>();

        w2.when(promise,
                new Runnable<ProgressPromise<List<TResolve>, TProgress>, List<TResolve>>() {
                    @Override
                    public ProgressPromise<List<TResolve>, TProgress> run(List<TResolve> values) {
                        d1.getResolver().resolve(reduce(resolveValues(values), reduceFunc, initialValue));
                        return null;
                    }
                },
                new Runnable<ProgressPromise<List<TResolve>, TProgress>, Value<List<TResolve>>>() {
                    @Override
                    public ProgressPromise<List<TResolve>, TProgress> run(Value<List<TResolve>> value) {
                        d1.getResolver().reject(value.getCause());
                        return null;
                    }
                }
        );

        return d1.getPromise();
    }

    /**
     * Traditional reduce function, where the input is a list of values to reduce.
     *
     * @param values     a list of values to reduce
     * @param reduceFunc the reduce function
     * @return a resolved {@link ProgressPromise} for the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduceValues(
            List<TResolve> values,
            final Reducer<T, TResolve> reduceFunc) {
        return reduce0(resolveValues(values), reduceFunc, null);
    }

    /**
     * Traditional reduce function, where the input is a list of values to reduce.
     *
     * @param values       a list of values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a resolved {@link ProgressPromise} for the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduceValues(
            List<TResolve> values,
            final Reducer<T, TResolve> reduceFunc,
            final T initialValue) {
        WhenProgress<T, TProgress> when = new WhenProgress<>();
        return reduceValues(values, reduceFunc, when.resolve(initialValue));
    }

    /**
     * Traditional reduce function, where the input is a list of values to reduce.
     *
     * @param values       a list of values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a resolved {@link ProgressPromise} for the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reduceValues(
            List<TResolve> values,
            final Reducer<T, TResolve> reduceFunc,
            final ProgressPromise<T, TProgress> initialValue) {
        return reduce0(resolveValues(values), reduceFunc, new Value<>(initialValue));
    }

    /**
     * Private reduce implementation, initialValue is now a Value&lt;TResolve&gt; to differentiate between no initial
     * value and a null initial value.
     *
     * @param promises     list of {@link ProgressPromise}s for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the optional initial value
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    private <T> ProgressPromise<T, TProgress> reduce0(
            List<ProgressPromise<TResolve, TProgress>> promises,
            final Reducer<T, TResolve> reduceFunc,
            final Value<ProgressPromise<T, TProgress>> initialValue) {

        final WhenProgress<T, TProgress> w = new WhenProgress<>();

        if (promises == null) {
            return w.reject((T) null);
        }

        final DeferredProgress<T, TProgress> d1 = w.defer();

        // Wrap the supplied reduceFunc with one that handles promises and then
        // delegates to the supplied.
        Reducer<ProgressPromise<T, TProgress>, ProgressPromise<TResolve, TProgress>> reducerWrapper = new Reducer<ProgressPromise<T, TProgress>, ProgressPromise<TResolve, TProgress>>() {
            @Override
            public ProgressPromise<T, TProgress> run(
                    ProgressPromise<T, TProgress> current,
                    final ProgressPromise<TResolve, TProgress> val,
                    final int i,
                    final int total) {

                final DeferredProgress<T, TProgress> d2 = w.defer();

                return w.when(current,
                        new Runnable<ProgressPromise<T, TProgress>, T>() {
                            @Override
                            public ProgressPromise<T, TProgress> run(final T c) {
                                when(val,
                                        new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                                            @SuppressWarnings("unchecked")
                                            @Override
                                            public ProgressPromise<TResolve, TProgress> run(TResolve v) {
                                                final T value = reduceFunc.run(c, v, i, total);
                                                if (value instanceof ProgressPromise) {
                                                    WhenProgress<Object, Object> w = new WhenProgress<>();
                                                    w.when((ProgressPromise<Object, Object>) value, new Runnable<ProgressPromise<Object, Object>, Object>() {
                                                        @Override
                                                        public ProgressPromise<Object, Object> run(Object obj) {
                                                            d2.getResolver().resolve(value);
                                                            return null;
                                                        }
                                                    });
                                                } else {
                                                    d2.getResolver().resolve(value);
                                                }
                                                return null;
                                            }
                                        },
                                        new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                                            @SuppressWarnings("unchecked")
                                            @Override
                                            public ProgressPromise<TResolve, TProgress> run(Value<TResolve> v) {
                                                final T value = reduceFunc.run(c, v.getValue(), i, total);
                                                if (value instanceof ProgressPromise) {
                                                    WhenProgress<Object, Object> w = new WhenProgress<>();
                                                    w.when((ProgressPromise<Object, Object>) value, new Runnable<ProgressPromise<Object, Object>, Object>() {
                                                        @Override
                                                        public ProgressPromise<Object, Object> run(Object obj) {
                                                            d2.getResolver().reject(value);
                                                            return null;
                                                        }
                                                    });
                                                } else {
                                                    d2.getResolver().reject(value);
                                                }
                                                return null;
                                            }
                                        }
                                );
                                return d2.getPromise();
                            }
                        });

            }
        };

        try {
            reduceList(promises, reducerWrapper, initialValue).then(
                    new Runnable<ProgressPromise<T, TProgress>, T>() {
                        @Override
                        public ProgressPromise<T, TProgress> run(T value) {
                            d1.getResolver().resolve(value);
                            return null;
                        }
                    },
                    new Runnable<ProgressPromise<T, TProgress>, Value<T>>() {
                        @Override
                        public ProgressPromise<T, TProgress> run(Value<T> value) {
                            d1.getResolver().reject(value);
                            return null;
                        }
                    }
            );

        } catch (Throwable t) {
            d1.getResolver().reject(t);
        }

        return d1.getPromise();
    }

    @SuppressWarnings("unchecked")
    private static <T1, T2> T1 reduceList(List<T2> list, Reducer<T1, T2> reduceFunc, Value<T1> initialValue) {
        T1 reduced;

        int i = 0;
        int len = (list == null ? 0 : list.size());

        // If no initialValue, use first item of array and adjust i to start at second item
        if (initialValue == null) {
            // If the list is empty, it's an exception
            if (i >= len) {
                throw new RuntimeException("No values to reduce.");
            }
            reduced = (T1) list.get(i++);
        } else {
            // If initialValue provided, use it
            reduced = initialValue.getValue();
        }

        // Do the actual reduce
        for (; i < len; i++) {
            reduced = reduceFunc.run(reduced, list.get(i), i, len);
        }

        return reduced;
    }

    /**
     * Ensure that resolution of a promise will trigger the resolver with the resolved value.
     *
     * @param promise  the {@link ProgressPromise} that when resolved/rejected will trigger the resolver.
     * @param resolver the resolver to be triggered.
     * @return a {@link ProgressPromise} for the input promise
     */
    public ProgressPromise<TResolve, TProgress> chain(
            ProgressPromise<TResolve, TProgress> promise,
            final Resolver<TResolve, TProgress> resolver) {
        return chain0(promise, resolver, null);
    }

    /**
     * Ensure that resolution of a promise will trigger the resolver with the provided resolveValue.
     *
     * @param promise      the {@link ProgressPromise} that when resolved/rejected will trigger the resolver.
     * @param resolver     the resolver to be triggered.
     * @param resolveValue the value to be provided to the resolver.
     * @return a {@link ProgressPromise} for the input promise
     */
    public ProgressPromise<TResolve, TProgress> chain(
            ProgressPromise<TResolve, TProgress> promise,
            final Resolver<TResolve, TProgress> resolver,
            final TResolve resolveValue) {
        return chain0(promise, resolver, new Value<>(resolveValue));
    }

    /**
     * Private implementation of chain, resolveValue is now a Value&lt;TResolve&gt; to differentiate between no resolve
     * value and a null resolve value.
     *
     * @param promise      the {@link ProgressPromise} that when resolved/rejected will trigger the resolver.
     * @param resolver     the resolver to be triggered.
     * @param resolveValue the value to be provided to the resolver.
     * @return a {@link ProgressPromise} for the input promise
     */
    private ProgressPromise<TResolve, TProgress> chain0(
            ProgressPromise<TResolve, TProgress> promise,
            final Resolver<TResolve, TProgress> resolver,
            final Value<TResolve> resolveValue) {

        return when(
                promise,
                new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(TResolve val) {
                        val = resolveValue != null ? resolveValue.getValue() : val;
                        resolver.resolve(val);
                        return resolve(val);
                    }
                },
                new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                        resolver.reject(reason);
                        return reject(reason);
                    }
                },
                new Runnable<Value<TProgress>, Value<TProgress>>() {
                    @Override
                    public Value<TProgress> run(Value<TProgress> value) {
                        resolver.notify(value);
                        return value;
                    }
                }
        );

    }


    /**
     * Run list of tasks in sequence with no overlap
     *
     * @param tasks tasks to be executed
     * @return {@link ProgressPromise} for a list containing the result of each task in the in the list position
     * corresponding to the position of the task in the task list
     */
    public ProgressPromise<List<TResolve>, TProgress> sequence(List<Runnable<ProgressPromise<TResolve, TProgress>, Void>> tasks) {
        return sequence(tasks, null);
    }

    /**
     * Run list of tasks in sequence with no overlap
     *
     * @param tasks  tasks to be executed
     * @param arg    argument to be passed to all tasks
     * @param <TArg> the argument type being passed to each task
     * @return {@link ProgressPromise} for a list containing the result of each task in the in the list position
     * corresponding to the position of the task in the task list
     */
    public <TArg> ProgressPromise<List<TResolve>, TProgress> sequence(
            List<Runnable<ProgressPromise<TResolve, TProgress>, TArg>> tasks,
            final TArg arg) {

        WhenProgress<Runnable<ProgressPromise<TResolve, TProgress>, TArg>, TProgress> w1 = new WhenProgress<>();
        final WhenProgress<List<TResolve>, TProgress> w2 = new WhenProgress<>();
        final DeferredProgress<List<TResolve>, TProgress> d1 = w2.defer();
        ProgressPromise<List<TResolve>, TProgress> list = w2.resolve(new ArrayList<TResolve>());

        w1.reduceValues(
                tasks,
                new Reducer<ProgressPromise<List<TResolve>, TProgress>, Runnable<ProgressPromise<TResolve, TProgress>, TArg>>() {
                    @Override
                    public ProgressPromise<List<TResolve>, TProgress> run(
                            final ProgressPromise<List<TResolve>, TProgress> results,
                            Runnable<ProgressPromise<TResolve, TProgress>, TArg> task,
                            int currentIndex,
                            int total) {
                        final DeferredProgress<List<TResolve>, TProgress> d2 = w2.defer();

                        ProgressPromise<TResolve, TProgress> taskResult;

                        try {
                            taskResult = task.run(arg);
                        } catch (RuntimeException ex) {
                            taskResult = reject(ex);
                        }

                        when(taskResult,
                                new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                                    @Override
                                    public ProgressPromise<TResolve, TProgress> run(final TResolve value) {
                                        w2.when(results, new Runnable<ProgressPromise<List<TResolve>, TProgress>, List<TResolve>>() {
                                            @Override
                                            public ProgressPromise<List<TResolve>, TProgress> run(List<TResolve> results) {
                                                results.add(value);
                                                d2.getResolver().resolve(results);
                                                return null;
                                            }
                                        });
                                        return null;
                                    }
                                },
                                new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                                    @Override
                                    public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                                        d1.getResolver().reject(new Value<>(Arrays.asList(value.getValue()), value.getCause()));
                                        return null;
                                    }
                                }
                        );

                        return d2.getPromise();
                    }
                },
                list
        ).then(
                new Runnable<ProgressPromise<ProgressPromise<List<TResolve>, TProgress>, TProgress>, ProgressPromise<List<TResolve>, TProgress>>() {
                    @Override
                    public ProgressPromise<ProgressPromise<List<TResolve>, TProgress>, TProgress> run(ProgressPromise<List<TResolve>, TProgress> value) {
                        w2.when(value, new Runnable<ProgressPromise<List<TResolve>, TProgress>, List<TResolve>>() {
                            @Override
                            public ProgressPromise<List<TResolve>, TProgress> run(List<TResolve> value) {
                                d1.getResolver().resolve(value);
                                return null;
                            }
                        });
                        return null;
                    }
                }
        );

        return d1.getPromise();
    }

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

    private List<Runnable<Void, Void>> handlerQueue = new ArrayList<>();

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
    private static MonitorApi monitorApi = new MonitorApi();

    protected static MonitorApi getMonitorApi() {
        return monitorApi;
    }

    // Allow running on an event loop
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

    /**
     * Converts a list of values to a list of resolved promises.  If a null input list is provided,
     * a null list is returned.
     *
     * @param values the values to resolve
     * @return a list of already-resolved promises for the input values
     */
    public List<ProgressPromise<TResolve, TProgress>> resolveValues(List<? extends TResolve> values) {
        // Short circuit if values is null
        if (values == null) {
            return null;
        }
        // Resolve non-null values
        List<ProgressPromise<TResolve, TProgress>> promises = new ArrayList<>();
        for (TResolve value : values) {
            promises.add((value != null ? resolve(value) : null));
        }
        return promises;
    }

    // Re-usable "identity" runnable that returns the input value as an already-resolved promise.
    private final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> identity =
            new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                @Override
                public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                    return resolve(value);
                }
            };

    private void crash(Throwable fatalError) {
        monitorApi.reportUnhandled();
        throw new RuntimeException(fatalError);
    }
}
