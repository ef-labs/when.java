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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Java implementation of the CommonJS Promises/A specification.
 * http://wiki.commonjs.org/wiki/Promises/A
 * <p/>
 * Based on the when.js 1.8.1 library (c) copyright B Cavalier & J Hann (https://github.com/cujojs/when)
 *
 * @param <TResolve>  the type passed to fulfillment or rejection handlers
 * @param <TProgress> the type passed to progress handlers
 */
public class OldWhenProgress<TResolve, TProgress> {

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
     * Register an observer for a promise.
     *
     * @param promise     a promise whose value is provided to the callbacks.
     * @param onFulfilled callback to be called when the promise is successfully fulfilled.  If the promise is an
     *                    immediate value, the callback will be invoked immediately.
     * @param onRejected  callback to be called when the promise is rejected.
     * @param onProgress  callback to be called when progress updates are issued for the promise.
     * @return a new {@link ProgressPromise} that will complete with the return value of onFulfilled or onRejected or the
     * completion value of promise if onFulfilled and/or onRejected are not supplied.
     */
    public ProgressPromise<TResolve, TProgress> when(
            Thenable<TResolve, TProgress> promise,
            Runnable<ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
        // Get a trusted promise for the input promise, and then register promise handlers
        return resolve(promise).then(onFulfilled, onRejected, onProgress);
    }

    /**
     * Returns promiseOrValue if promiseOrValue is a {@link ProgressPromise}, a new com.englishtown.promises.ProgressPromise if
     * promiseOrValue is a foreign promise, or a new, already-fulfilled {@link ProgressPromise}
     * whose value is promiseOrValue if promiseOrValue is an immediate value.
     *
     * @param value a value to wrap in a resolved promise
     * @return a new already-fulfilled trusted {@link ProgressPromise} for the provided value
     */
    public ProgressPromise<TResolve, TProgress> resolve(TResolve value) {
        // It's a value, not a promise.  Create a resolved promise for it.
        return fulfilled(value);
    }

    /**
     * Returns the promise if it is a PromiseImpl, a new PromiseImpl if it is a foreign
     * promise, or a new, already-fulfilled {@link ProgressPromise} if it is null.
     *
     * @param promise the promise to resolve
     * @return Guarantee to return a trusted PromiseImpl
     */
    public ProgressPromise<TResolve, TProgress> resolvePromise(Thenable<TResolve, TProgress> promise) {
        return resolve(promise);
    }

    /**
     * Returns the promise if it is a PromiseImpl, a new PromiseImpl if it is a "foreign"
     * promise, or a new, already-fulfilled {@link ProgressPromise} if it is null.
     *
     * @param promise the promise to resolve
     * @return Guarantee to return a trusted PromiseImpl
     */
    private PromiseImpl resolve(Thenable<TResolve, TProgress> promise) {

        // Handle null promise
        if (promise == null) {
            return fulfilled(null);
        }

        if (PromiseImpl.class.isInstance(promise)) {
            // It's a when.js promise, so we trust it
            return (PromiseImpl) promise;
        } else {
            // Assimilate foreign promises
            return assimilate(promise);
        }

    }

    /**
     * Assimilate an untrusted thenable by introducing a trusted middle man.
     * Not a perfect strategy, but possibly the best we can do.
     * IMPORTANT: This is the only place when.js should ever call an untrusted
     * thenable's then(). Don't expose the return value to the untrusted thenable
     *
     * @param thenable An untrusted Thenable instance
     * @return trusted PromiseImpl
     */
    private PromiseImpl assimilate(Thenable<TResolve, TProgress> thenable) {
        final DeferredImpl d = deferInner();

        try {
            thenable.then(
                    new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                        @Override
                        public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                            d.resolver.resolve(value);
                            return null;
                        }
                    },
                    new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                        @Override
                        public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                            d.resolver.reject(reason);
                            return null;
                        }
                    },
                    new Runnable<Value<TProgress>, Value<TProgress>>() {
                        @Override
                        public Value<TProgress> run(Value<TProgress> update) {
                            d.resolver.notify(update);
                            return null;
                        }
                    }
            );
        } catch (RuntimeException e) {
            d.resolver.reject(e);
        }

        return d.promise;

    }

    /**
     * Returns a rejected promise for the supplied promise.  The returned promise will be rejected with the
     * value of the promises after it is fulfilled or the reason after it is rejected.
     *
     * @param promise the rejected value of the returned {@link ProgressPromise}
     * @return a rejected {@link ProgressPromise}
     */
    public ProgressPromise<TResolve, TProgress> reject(Thenable<TResolve, TProgress> promise) {
        return when(
                promise,
                new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                        return rejected(new Value<>(value));
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
     * @param value the rejected value of the returned {@link ProgressPromise}
     * @return {com.englishtown.promises.ProgressPromise} rejected {@link ProgressPromise}
     */
    /**
     * Returns a rejected promise for the supplied value.  The returned promise will be rejected with the
     * supplied value.
     *
     * @param value the rejected value of the returned {@link ProgressPromise}
     * @return a rejected {@link ProgressPromise}
     */
    public ProgressPromise<TResolve, TProgress> reject(TResolve value) {
        return reject(resolve(value));
    }

    /**
     * Returns a rejected promise for the supplied value.  The returned promise will be rejected with the
     * supplied value.
     *
     * @param value the rejected value of the returned {@link ProgressPromise}
     * @return a rejected {@link ProgressPromise}
     */
    public ProgressPromise<TResolve, TProgress> reject(Value<TResolve> value) {
        return rejected(value);
    }

    /**
     * Returns a rejected promise for the supplied value.  The returned promise will be rejected with the
     * supplied error for the value.
     *
     * @param error the rejected error value of the returned {@link ProgressPromise}
     * @return a rejected {@link ProgressPromise}
     */
    public ProgressPromise<TResolve, TProgress> reject(Throwable error) {
        return rejected(new Value<TResolve>(error));
    }

    /**
     * Trusted implementation of {@link PromiseExt}.  Any other {@link ProgressPromise} implementation is considered untrusted.
     */
    protected class PromiseImpl implements PromiseExt<TResolve, TProgress> {

        private final Thenable<TResolve, TProgress> then;

        PromiseImpl(Thenable<TResolve, TProgress> then) {
            this.then = then;
        }

        @Override
        public ProgressPromise<TResolve, TProgress> then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> then(
                Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
            return this.then.then(onFulfilled, onRejected, onProgress);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> always(
                final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilledOrRejected) {
            return always(onFulfilledOrRejected, null);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> always(
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

        @Override
        public ProgressPromise<TResolve, TProgress> otherwise(Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return this.then(null, onRejected);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> yield(final TResolve value) {
            return this.then(
                    new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                        @Override
                        public ProgressPromise<TResolve, TProgress> run(TResolve ignore) {
                            return resolve(value);
                        }
                    });
        }

        @Override
        public ProgressPromise<TResolve, TProgress> yield(final Thenable<TResolve, TProgress> promise) {
            return this.then(
                    new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                        @Override
                        public ProgressPromise<TResolve, TProgress> run(TResolve ignore) {
                            return resolve(promise);
                        }
                    });
        }

        /**
         * Not implemented
         *
         * @return
         */
        @Override
        public PromiseState<TResolve> inspect() {
            return null;
        }

// TODO: Does spread make sense in java?
//        /**
//         * Assumes that this promise will fulfill with an array, and arranges
//         * for the onFulfilled to be called with the array as its argument list
//         * i.e. onFulfilled.spread(undefined, array).
//         * @param {function} onFulfilled function to receive spread arguments
//         * @return {ProgressPromise}
//         */
//        public ProgressPromise<TResolve, TProgress> spread(final Runnable<ProgressPromise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled) {
//            return this.then(function(array) {
//                // array may contain promises, so resolve its contents.
//                return all(array, function(array) {
//                    return onFulfilled.apply(undef, array);
//                });
//            });
//        }

    }

    /**
     * Create an already-resolved promise for the supplied value
     *
     * @param value the value for the promise
     * @return new fulfilled {@link ProgressPromise}.
     */
    private PromiseImpl fulfilled(final TResolve value) {
        return createPromise(new Thenable<TResolve, TProgress>() {

            @Override
            public ProgressPromise<TResolve, TProgress> then(
                    Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                    Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                    Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

                try {
                    return (onFulfilled != null ? resolve(onFulfilled.run(value)) : resolve(value));

                } catch (RuntimeException e) {
                    return rejected(new Value<TResolve>(e));

                }

            }

        });
    }

    /**
     * Create an already-rejected {@link ProgressPromise} with the supplied rejection reason.
     *
     * @param reason the reason for the rejection
     * @return a new rejected {@link ProgressPromise}.
     */
    private PromiseImpl rejected(final Value<TResolve> reason) {
        return createPromise(new Thenable<TResolve, TProgress>() {

            @Override
            public ProgressPromise<TResolve, TProgress> then(
                    Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                    Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                    Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

                try {
                    return onRejected != null ? resolve(onRejected.run(reason)) : rejected(reason);

                } catch (RuntimeException e) {
                    return rejected(new Value<TResolve>(e));

                }
            }
        });
    }

    /**
     * Private implementation of {@link DeferredProgress}
     */
    protected class DeferredImpl implements DeferredProgress<TResolve, TProgress> {

        DeferredImpl(PromiseImpl promise, ResolverImpl resolver) {
            this.promise = promise;
            this.resolver = resolver;
        }

        public final ResolverImpl resolver;
        public final PromiseImpl promise;

        @Override
        public Resolver<TResolve, TProgress> getResolver() {
            return resolver;
        }

        @Override
        public ProgressPromise<TResolve, TProgress> getPromise() {
            return promise;
        }
    }

    /**
     * Private implementation of {@link Resolver}
     */
    protected class ResolverImpl implements Resolver<TResolve, TProgress> {

        public final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> resolve;
        public final Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>> resolvePromise;
        public final Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> reject;
        public final Runnable<Value<TProgress>, Value<TProgress>> notify;

        public ResolverImpl(
                Runnable<ProgressPromise<TResolve, TProgress>, TResolve> resolve,
                Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>> resolvePromise,
                Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> reject,
                Runnable<Value<TProgress>, Value<TProgress>> notify) {
            this.resolve = resolve;
            this.resolvePromise = resolvePromise;
            this.reject = reject;
            this.notify = notify;
        }

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
     * Creates a new {@link DeferredProgress} with fully isolated resolver and promise parts,
     * either or both of which may be given out safely to consumers.
     *
     * @return new {@link DeferredProgress}
     */
    public DeferredProgress<TResolve, TProgress> defer() {
        return deferInner();
    }

    /**
     * Creates a new {@link DeferredImpl} with fully isolated resolver and promise parts,
     * either or both of which may be given out safely to consumers.
     *
     * @return new {@link DeferredImpl}
     */
    private DeferredImpl deferInner() {

        final List<Runnable<Void, ProgressPromise<TResolve, TProgress>>> handlers = new ArrayList<>();
        final List<Runnable<Value<TProgress>, Value<TProgress>>> progressHandlers = new ArrayList<>();

        // Pre-resolution then() implementation that adds the supplied onFulfilled, onRejected,
        // and onProgress callbacks to the registered listeners
        final ValueHolder<Thenable<TResolve, TProgress>> _then = new ValueHolder<>();
        _then.value = new Thenable<TResolve, TProgress>() {
            @Override
            public ProgressPromise<TResolve, TProgress> then(
                    final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
                    final Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected,
                    final Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

                final ValueHolder<Runnable<Value<TProgress>, Value<TProgress>>> progressHandler = new ValueHolder<>();
                final DeferredImpl deferred = deferInner();

                if (onProgress != null) {
                    progressHandler.value = new Runnable<Value<TProgress>, Value<TProgress>>() {
                        @Override
                        public Value<TProgress> run(Value<TProgress> update) {
                            try {
                                // Allow progress handler to transform progress event
                                deferred.resolver.notify(onProgress.run(update));

                            } catch (RuntimeException e) {
                                // Use caught value as progress
                                deferred.resolver.notify(new Value<TProgress>(e));

                            }

                            return null;
                        }
                    };
                } else {
                    progressHandler.value = new Runnable<Value<TProgress>, Value<TProgress>>() {
                        @Override
                        public Value<TProgress> run(Value<TProgress> update) {
                            deferred.resolver.notify(update);
                            return null;
                        }
                    };
                }

                handlers.add(new Runnable<Void, ProgressPromise<TResolve, TProgress>>() {
                    @Override
                    public Void run(ProgressPromise<TResolve, TProgress> promise) {
                        promise.then(onFulfilled, onRejected)
                                .then(deferred.resolver.resolve, deferred.resolver.reject, progressHandler.value);
                        return null;
                    }
                });

                progressHandlers.add(progressHandler.value);

                return deferred.promise;
            }
        };

        // Issue a progress event, notifying all progress listeners
        final ValueHolder<Runnable<Value<TProgress>, Value<TProgress>>> _notify = new ValueHolder<>();
        _notify.value = new Runnable<Value<TProgress>, Value<TProgress>>() {
            @Override
            public Value<TProgress> run(Value<TProgress> update) {
                processQueue(progressHandlers, update);
                return update;
            }
        };

        // Transition from pre-resolution state to post-resolution state, notifying all listeners of the resolution
        // or rejection
        final ValueHolder<Runnable<ProgressPromise<TResolve, TProgress>, PromiseImpl>> _resolve = new ValueHolder<>();

        _resolve.value = new Runnable<ProgressPromise<TResolve, TProgress>, PromiseImpl>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(PromiseImpl p) {

                // Replace _then with one that directly notifies with the result.
                _then.value = p.then;
                // Replace _resolve so that this DeferredProgress can only be resolved once
                _resolve.value = new Runnable<ProgressPromise<TResolve, TProgress>, PromiseImpl>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(PromiseImpl promise) {
                        return promise;
                    }
                };
                // Make _notify identity, to disallow progress for the resolved promise.
                _notify.value = new Runnable<Value<TProgress>, Value<TProgress>>() {
                    @Override
                    public Value<TProgress> run(Value<TProgress> progress) {
                        return progress;
                    }
                };

                // Notify handlers
                processQueue(handlers, p);

                // Free progressHandlers array since we'll never issue progress events
                progressHandlers.clear();
                handlers.clear();

                return p;
            }
        };

        // Wrappers to allow replacing implementations
        Thenable<TResolve, TProgress> promiseThen = new Thenable<TResolve, TProgress>() {
            @Override
            public ProgressPromise<TResolve, TProgress> then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected, Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
                return _then.value.then(onFulfilled, onRejected, onProgress);
            }
        };
        Runnable<ProgressPromise<TResolve, TProgress>, TResolve> promiseResolve = new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(TResolve value) {
                return _resolve.value.run(fulfilled(value));
            }
        };
        Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>> promiseResolvePromise = new
                Runnable<ProgressPromise<TResolve, TProgress>, ProgressPromise<TResolve, TProgress>>() {
                    @Override
                    public ProgressPromise<TResolve, TProgress> run(ProgressPromise<TResolve, TProgress> promise) {
                        return _resolve.value.run(resolve(promise));
                    }
                };
        Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> promiseReject = new Runnable<ProgressPromise<TResolve,
                TProgress>, Value<TResolve>>() {
            @Override
            public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                return _resolve.value.run(rejected(reason));
            }
        };
        Runnable<Value<TProgress>, Value<TProgress>> promiseNotify = new Runnable<Value<TProgress>, Value<TProgress>>() {
            @Override
            public Value<TProgress> run(Value<TProgress> value) {
                return _notify.value.run(value);
            }
        };

        // The promise for the new deferred
        PromiseImpl promise = createPromise(promiseThen);

        // The resolver for the new deferred
        ResolverImpl resolver = new ResolverImpl(
                promiseResolve,
                promiseResolvePromise,
                promiseReject,
                promiseNotify);

        // The full DeferredProgress object, with PromiseImpl and ResolverImpl
        return new DeferredImpl(promise, resolver);
    }

    protected PromiseImpl createPromise(Thenable<TResolve, TProgress> then) {
        return new PromiseImpl(then);
    }

    /**
     * Initiates a competitive race, returning a {@link ProgressPromise} that will resolve when
     * howMany of the supplied promises have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promises.size() - howMany) + 1 input promises reject.
     *
     * @param promises    a list of promises
     * @param howMany     number of promises to resolve
     * @param onFulfilled resolution handler
     * @return a {@link ProgressPromise} that will resolve to a list of howMany values that resolved first,
     * or will reject with an array of (promises.size() - howMany + 1) rejection reasons
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> some(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final int howMany,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled) {
        return some(promises, howMany, onFulfilled, null, null);
    }

    /**
     * Initiates a competitive race, returning a {@link ProgressPromise} that will resolve when
     * howMany of the supplied promises have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promises.size() - howMany) + 1 input promises reject.
     *
     * @param promises    a list of promises
     * @param howMany     number of promises to resolve
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @return a {@link ProgressPromise} that will resolve to a list of howMany values that resolved first,
     * or will reject with an array of (promises.size() - howMany + 1) rejection reasons
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> some(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final int howMany,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>> onRejected) {
        return some(promises, howMany, onFulfilled, onRejected, null);
    }

    /**
     * Initiates a competitive race, returning a {@link ProgressPromise} that will resolve when
     * howMany of the supplied promises have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promises.size() - howMany) + 1 input promises reject.
     *
     * @param promises    a list of promises
     * @param howMany     number of promises to resolve
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @param onProgress  progress handler
     * @return a {@link ProgressPromise} that will resolve to a list of howMany values that resolved first,
     * or will reject with an array of (promises.size() - howMany + 1) rejection reasons
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> some(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final int howMany,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>> onRejected,
            final Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

        final OldWhenProgress<List<? extends TResolve>, TProgress> w1 = new OldWhenProgress<>();
        final OldWhenProgress<List<? extends TResolve>, TProgress>.DeferredImpl d1 = w1.deferInner();
        OldWhenProgress<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> w2 = new OldWhenProgress<>();

        w2.when(promises, new Runnable<ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress>, List<? extends ProgressPromise<TResolve, TProgress>>>() {
            @Override
            public ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> run(List<? extends ProgressPromise<TResolve, TProgress>> value) {

                int len = promises.size();

                final ValueHolder<Integer> toResolve = new ValueHolder<>(Math.max(0, Math.min(howMany, len)));
                final List<TResolve> values = new ArrayList<>();

                final ValueHolder<Integer> toReject = new ValueHolder<>((len - toResolve.value) + 1);
                final List<TResolve> reasons = new ArrayList<>();

                // No items in the input, resolve immediately
                if (toResolve.value == 0) {
                    d1.getResolver().resolve(values);

                } else {
                    Runnable<Value<TProgress>, Value<TProgress>> notify = d1.resolver.notify;

                    final ValueHolder<Runnable<ProgressPromise<TResolve, TProgress>, TResolve>> fulfillOne = new ValueHolder<>();
                    final ValueHolder<Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>> rejectOne = new ValueHolder<>();

                    rejectOne.value = new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                        @Override
                        public ProgressPromise<TResolve, TProgress> run(Value<TResolve> reason) {
                            reasons.add(reason.getValue());

                            if (--toReject.value == 0) {
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

                            if (--toResolve.value == 0) {
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
                }

                d1.getPromise().then(onFulfilled, onRejected, onProgress);
                return null;
            }
        }).then(null, new Runnable<ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress>, Value<List<? extends ProgressPromise<TResolve, TProgress>>>>() {
            @Override
            public ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> run(Value<List<? extends ProgressPromise<TResolve, TProgress>>> value) {
                // Need to reject the deferred if an exception is thrown above
                d1.getResolver().reject(value.getCause());
                return null;
            }
        });

        return d1.getPromise();
    }

    /**
     * Resolves immediately returning a resolved {@link ProgressPromise} with the specified number of values.
     *
     * @param values      a list of resolved values
     * @param howMany     number of promises to resolve
     * @param onFulfilled resolution handler
     * @return a resolved {@link ProgressPromise} with howMany values.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> someValues(
            final List<TResolve> values,
            final int howMany,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled) {
        return some(resolveValues(values), howMany, onFulfilled, null, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promises has resolved or will reject when
     * *all* promises have rejected.
     *
     * @param promises    list of promises to resolve
     * @param onFulfilled resolution handler
     * @return a {@link ProgressPromise} that will resolve to the value that resolved first,
     * or will reject with an array of all rejected inputs.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> any(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
        return any(promises, onFulfilled, null, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promises has resolved or will reject when
     * *all* promises have rejected.
     *
     * @param promises    list of promises to resolve
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @return a {@link ProgressPromise} that will resolve to the value that resolved first,
     * or will reject with an array of all rejected inputs.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> any(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>> onRejected) {
        return any(promises, onFulfilled, onRejected, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promises has resolved or will reject when
     * *all* promises have rejected.
     *
     * @param promises    list of promises to resolve
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @param onProgress  progress handler
     * @return a {@link ProgressPromise} that will resolve to the value that resolved first,
     * or will reject with an array of all rejected inputs.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> any(
            final List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled,
            final Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>> onRejected,
            final Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

        Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> unwrapSingleResult = new Runnable<ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>>() {
            @Override
            public ProgressPromise<List<? extends TResolve>, TProgress> run(List<? extends TResolve> value) {
                if (onFulfilled != null) {
                    TResolve val = null;

                    if (value.size() > 0) {
                        val = value.get(0);
                    }

                    onFulfilled.run(val);
                }
                return null;
            }
        };

        return some(promises, 1, unwrapSingleResult, onRejected, onProgress);
    }

    /**
     * Resolves immediately returning a resolved {@link ProgressPromise} with the first value from the input list.
     *
     * @param values      the values
     * @param onFulfilled resolution handler
     * @return an already-fulfilled {@link ProgressPromise} with the first value from the input list.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> anyValues(
            final List<TResolve> values,
            final Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
        return any(resolveValues(values), onFulfilled, null, null);
    }

    /**
     * Return a promise that will resolve only once all the supplied promises
     * have resolved. The resolution value of the returned promise will be a list
     * containing the resolution values of each of the promises.
     *
     * @param promises    input promises to resolve
     * @param onFulfilled resolution handler
     * @return a {@link ProgressPromise} that resolves when all the input promises have resolved
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> all(
            List<? extends ProgressPromise<TResolve, TProgress>> promises,
            Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled) {
        return all(promises, onFulfilled, null, null);
    }

    /**
     * Return a promise that will resolve only once all the supplied promises
     * have resolved. The resolution value of the returned promise will be a list
     * containing the resolution values of each of the promises.
     *
     * @param promises    input promises to resolve
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @return a {@link ProgressPromise} that resolves when all the input promises have resolved
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> all(
            List<? extends ProgressPromise<TResolve, TProgress>> promises,
            Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled,
            Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>> onRejected) {
        return all(promises, onFulfilled, onRejected, null);
    }

    /**
     * Return a promise that will resolve only once all the supplied promises
     * have resolved. The resolution value of the returned promise will be a list
     * containing the resolution values of each of the promises.
     *
     * @param promises    input promises to resolve
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @param onProgress  progress handler
     * @return a {@link ProgressPromise} that resolves when all the input promises have resolved
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> all(
            List<? extends ProgressPromise<TResolve, TProgress>> promises,
            Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled,
            Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, Value<List<? extends TResolve>>> onRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
        // TODO: onProgress does not work...(same problem in when.js)
        // TODO: change onRejected to take single value rather than list and provide wrapper to resolve?
        return map(promises, identity).then(onFulfilled, onRejected, onProgress);
    }

    /**
     * Return a resolved promise for the list of input values.
     *
     * @param values      input values
     * @param onFulfilled resolution handler
     * @return a resolved {@link ProgressPromise}
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> allValues(
            List<TResolve> values,
            Runnable<? extends ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>> onFulfilled) {
        return all(resolveValues(values), onFulfilled, null, null);
    }

    /**
     * Joins multiple promises into a single returned promise.
     *
     * @param promises the promises to join
     * @return a {@link ProgressPromise} that will fulfill when *all* the input promises have fulfilled,
     * or will reject when *any one* of the input promises rejects.
     */
    @SafeVarargs
    public final ProgressPromise<List<? extends TResolve>, TProgress> join(ProgressPromise<TResolve, TProgress>... promises) {
        List<ProgressPromise<TResolve, TProgress>> input = (promises == null ? new ArrayList<ProgressPromise<TResolve, TProgress>>()
                : Arrays.asList(promises));
        return map(input, identity);
    }

    /**
     * Joins multiple values into a single returned promise.
     *
     * @param values input values to join
     * @return a resolved {@link ProgressPromise} for the joined input values
     */
    @SafeVarargs
    public final ProgressPromise<List<? extends TResolve>, TProgress> join(TResolve... values) {
        List<TResolve> input = (values == null ? new ArrayList<TResolve>() : Arrays.asList(values));
        return mapValues(input, identity);
    }

    /**
     * Traditional map function, but the input is a list of {@link ProgressPromise}s for values to be mapped.
     *
     * @param promises a list of {@link ProgressPromise}s
     * @param mapFunc  a mapping function that returns a promise for a value
     * @return a {@link ProgressPromise} that will resolve to a list containing the mapped output values.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> map(
            List<? extends ProgressPromise<TResolve, TProgress>> promises,
            final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> mapFunc) {

        final OldWhenProgress<List<? extends TResolve>, TProgress> w1 = new OldWhenProgress<>();
        final DeferredProgress<List<? extends TResolve>, TProgress> d1 = w1.defer();

        final OldWhenProgress<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> w2 = new OldWhenProgress<>();

        w2.when(promises, new Runnable<ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress>, List<? extends ProgressPromise<TResolve, TProgress>>>() {
            @Override
            public ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> run(List<? extends ProgressPromise<TResolve, TProgress>> array) {

                final ValueHolder<Integer> toResolve = new ValueHolder<>();
                int len;

                // Since we know the resulting length, we can preallocate the results array to avoid array expansions.
                toResolve.value = len = array.size();
                final List<TResolve> results = new ArrayList<>(len);

                if (toResolve.value == 0) {
                    d1.getResolver().resolve(results);

                } else {
                    // Pre-populate null values to allow us to set the value at an index
                    for (int i = 0; i < len; i++) {
                        results.add(null);
                    }

                    Runnable2<Void, ProgressPromise<TResolve, TProgress>, Integer> resolveOne;
                    resolveOne = new Runnable2<Void,
                            ProgressPromise<TResolve, TProgress>, Integer>() {
                        @Override
                        public Void run(ProgressPromise<TResolve, TProgress> item, final Integer index) {
                            when(item, mapFunc).then(
                                    new Runnable<ProgressPromise<TResolve, TProgress>, TResolve>() {
                                        @Override
                                        public ProgressPromise<TResolve, TProgress> run(TResolve mapped) {
                                            results.set(index, mapped);

                                            if (--toResolve.value == 0) {
                                                d1.getResolver().resolve(results);
                                            }
                                            return null;
                                        }
                                    },
                                    new Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>>() {
                                        @Override
                                        public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
                                            d1.getResolver().reject(new Value<List<? extends TResolve>>(Arrays.asList(value.getValue()), value.getCause()));
                                            return null;
                                        }
                                    }
                            );
                            return null;
                        }
                    };

                    // Since mapFunc may be async, get all invocations of it into flight
                    for (int i = 0; i < len; i++) {
                        resolveOne.run(array.get(i), i);
                    }
                }

                return null;
            }
        }).then(null, new Runnable<ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress>, Value<List<? extends ProgressPromise<TResolve, TProgress>>>>() {
            @Override
            public ProgressPromise<List<? extends ProgressPromise<TResolve, TProgress>>, TProgress> run(Value<List<? extends ProgressPromise<TResolve, TProgress>>> value) {
                // Need to reject the deferred if an exception is thrown above
                d1.getResolver().reject(value.getCause());
                return null;
            }
        });

        return d1.getPromise();
    }

    /**
     * Traditional map function, but the input is a {@link ProgressPromise} for values to be mapped.
     *
     * @param promise a {@link ProgressPromise} for a list of values to be mapped
     * @param mapFunc a mapping function that returns a promise for a value
     * @return a {@link ProgressPromise} that will resolve to a list containing the mapped output values.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> mapPromise(
            ProgressPromise<List<? extends TResolve>, TProgress> promise,
            final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> mapFunc) {

        OldWhenProgress<List<? extends TResolve>, TProgress> when = new OldWhenProgress<>();

        return when.when(promise, new Runnable<ProgressPromise<List<? extends TResolve>, TProgress>, List<? extends TResolve>>() {
            @Override
            public ProgressPromise<List<? extends TResolve>, TProgress> run(List<? extends TResolve> value) {
                return mapValues(value, mapFunc);
            }
        });

    }

    /**
     * Traditional map function, the input is a list of values to be mapped.
     *
     * @param values  a list of values to be mapped
     * @param mapFunc a mapping function that returns a promise for a value
     * @return a resolved {@link ProgressPromise} containing the mapped output values.
     */
    public ProgressPromise<List<? extends TResolve>, TProgress> mapValues(
            List<? extends TResolve> values,
            Runnable<ProgressPromise<TResolve, TProgress>, TResolve> mapFunc) {
        return map(resolveValues(values), mapFunc);
    }

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
        return reduceInner(promises, reduceFunc, null);
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
        OldWhenProgress<T, TProgress> when = new OldWhenProgress<>();
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
        return reduceInner(promises, reduceFunc, new Value<>(initialValue));
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
        return reduceInner(resolveValues(values), reduceFunc, null);
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
        OldWhenProgress<T, TProgress> when = new OldWhenProgress<>();
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
        return reduceInner(resolveValues(values), reduceFunc, new Value<>(initialValue));
    }

    /**
     * Traditional reduce function, but the input is a {@link ProgressPromise} for a list of values.
     *
     * @param promise      a {@link ProgressPromise} for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reducePromise(
            ProgressPromise<List<TResolve>, TProgress> promise,
            final Reducer<T, TResolve> reduceFunc,
            final T initialValue) {
        OldWhenProgress<T, TProgress> when = new OldWhenProgress<>();
        return reducePromise(promise, reduceFunc, when.resolve(initialValue));
    }

    /**
     * Traditional reduce function, but the input is a {@link ProgressPromise} for a list of values.
     *
     * @param promise      a {@link ProgressPromise} for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the initial value. If null is provided, it will be used as the initial value.
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    public <T> ProgressPromise<T, TProgress> reducePromise(
            ProgressPromise<List<TResolve>, TProgress> promise,
            final Reducer<T, TResolve> reduceFunc,
            final ProgressPromise<T, TProgress> initialValue) {

        final DeferredProgress<T, TProgress> d1 = new OldWhenProgress<T, TProgress>().defer();
        final OldWhenProgress<List<TResolve>, TProgress> w2 = new OldWhenProgress<>();

        w2.when(promise,
                new Runnable<ProgressPromise<List<TResolve>, TProgress>, List<TResolve>>() {
                    @Override
                    public ProgressPromise<List<TResolve>, TProgress> run(List<TResolve> values) {
                        d1.getResolver().resolve(reduceValues(values, reduceFunc, initialValue));
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
     * Private reduce implementation, initialValue is now a Value&lt;TResolve&gt; to differentiate between no initial
     * value and a null initial value.
     *
     * @param promises     list of {@link ProgressPromise}s for values to reduce
     * @param reduceFunc   the reduce function
     * @param initialValue the optional initial value
     * @return a {@link ProgressPromise} that will resolve to the final reduced values.
     */
    private <T> ProgressPromise<T, TProgress> reduceInner(
            List<ProgressPromise<TResolve, TProgress>> promises,
            final Reducer<T, TResolve> reduceFunc,
            final Value<ProgressPromise<T, TProgress>> initialValue) {

        final OldWhenProgress<List<ProgressPromise<TResolve, TProgress>>, TProgress> w1 = new OldWhenProgress<>();
        final OldWhenProgress<T, TProgress> w2 = new OldWhenProgress<>();
        final DeferredProgress<T, TProgress> d2 = w2.defer();

        w1.when(promises, new Runnable<ProgressPromise<List<ProgressPromise<TResolve, TProgress>>, TProgress>, List<ProgressPromise<TResolve, TProgress>>>() {
            @Override
            public ProgressPromise<List<ProgressPromise<TResolve, TProgress>>, TProgress> run(List<ProgressPromise<TResolve, TProgress>> array) {

                // Wrap the supplied reduceFunc with one that handles promises and then
                // delegates to the supplied.
                Reducer<ProgressPromise<T, TProgress>, ProgressPromise<TResolve, TProgress>> reducerWrapper = new Reducer<ProgressPromise<T, TProgress>, ProgressPromise<TResolve, TProgress>>() {
                    @Override
                    public ProgressPromise<T, TProgress> run(
                            ProgressPromise<T, TProgress> current,
                            final ProgressPromise<TResolve, TProgress> val,
                            final int i,
                            final int total) {

                        final DeferredProgress<T, TProgress> d3 = w2.defer();

                        return w2.when(current,
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
                                                            OldWhenProgress<Object, Object> w = new OldWhenProgress<>();
                                                            w.when((ProgressPromise<Object, Object>) value, new Runnable<ProgressPromise<Object, Object>, Object>() {
                                                                @Override
                                                                public ProgressPromise<Object, Object> run(Object obj) {
                                                                    d3.getResolver().resolve(value);
                                                                    return null;
                                                                }
                                                            });
                                                        } else {
                                                            d3.getResolver().resolve(value);
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
                                                            OldWhenProgress<Object, Object> w = new OldWhenProgress<>();
                                                            w.when((ProgressPromise<Object, Object>) value, new Runnable<ProgressPromise<Object, Object>, Object>() {
                                                                @Override
                                                                public ProgressPromise<Object, Object> run(Object obj) {
                                                                    d3.getResolver().reject(value);
                                                                    return null;
                                                                }
                                                            });
                                                        } else {
                                                            d3.getResolver().reject(value);
                                                        }
                                                        return null;
                                                    }
                                                }
                                        );
                                        return d3.getPromise();
                                    }
                                });

                    }
                };

                reduceList(array, reducerWrapper, initialValue).then(
                        new Runnable<ProgressPromise<T, TProgress>, T>() {
                            @Override
                            public ProgressPromise<T, TProgress> run(T value) {
                                d2.getResolver().resolve(value);
                                return null;
                            }
                        },
                        new Runnable<ProgressPromise<T, TProgress>, Value<T>>() {
                            @Override
                            public ProgressPromise<T, TProgress> run(Value<T> value) {
                                d2.getResolver().reject(value);
                                return null;
                            }
                        }
                );
                return null;
            }
        }).then(null, new Runnable<ProgressPromise<List<ProgressPromise<TResolve, TProgress>>, TProgress>,
                Value<List<ProgressPromise<TResolve, TProgress>>>>() {
            @Override
            public ProgressPromise<List<ProgressPromise<TResolve, TProgress>>, TProgress> run(Value<List<ProgressPromise<TResolve, TProgress>>> value) {
                d2.getResolver().reject(value.getCause());
                return null;
            }
        });

        return d2.getPromise();
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
        return chainInner(promise, resolver, null);
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
        return chainInner(promise, resolver, new Value<>(resolveValue));
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
    private ProgressPromise<TResolve, TProgress> chainInner(
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
                        return rejected(reason);
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

        OldWhenProgress<Runnable<ProgressPromise<TResolve, TProgress>, TArg>, TProgress> w1 = new OldWhenProgress<>();
        final OldWhenProgress<List<TResolve>, TProgress> w2 = new OldWhenProgress<>();
        final DeferredProgress<List<TResolve>, TProgress> d1 = w2.defer();
        ProgressPromise<List<TResolve>, TProgress> list = w2.resolve(new ArrayList<TResolve>());

        w1.reduceValues(tasks, new Reducer<ProgressPromise<List<TResolve>, TProgress>, Runnable<ProgressPromise<TResolve, TProgress>,
                TArg>>() {
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
                    taskResult = rejected(new Value<TResolve>(ex));
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
        }, list
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

    /**
     * Execute all callbacks in the queue passing the provided value
     *
     * @param queue list of callbacks
     * @param value argument passed to each callback
     * @param <T1>  the callback return type
     * @param <T2>  the callback input type
     */
    private <T1, T2> void processQueue(List<Runnable<T1, T2>> queue, T2 value) {
        for (Runnable<T1, T2> handler : queue) {
            handler.run(value);
        }
    }

    /**
     * Converts a list of values to a list of resolved promises.  If a null input list is provided,
     * a null list is returned.
     *
     * @param values the values to resolve
     * @return a list of already-resolved promises for the input values
     */
    private List<ProgressPromise<TResolve, TProgress>> resolveValues(List<? extends TResolve> values) {
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

}
