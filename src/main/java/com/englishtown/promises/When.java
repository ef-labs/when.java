package com.englishtown.promises;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A lightweight CommonJS Promises/A and when() implementation
 * when is part of the cujo.js family of libraries (http://cujojs.com/)
 * <p/>
 * Licensed under the MIT License at:
 * http://www.opensource.org/licenses/mit-license.php
 *
 * @version 1.7.1
 */

/**
 * @license MIT License (c) copyright B Cavalier & J Hann
 */
public class When<TResolve, TProgress> {


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
     * @return {com.englishtown.promises.Promise} a new {@link Promise} that will complete with the return
     *         value of callback or errback or the completion value of promiseOrValue if
     *         callback and/or errback is not supplied.
     */
    public Promise<TResolve, TProgress> when(
            TResolve value,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
            Runnable<TProgress, TProgress> onProgress) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return resolve(value).then(onFulfilled, onRejected, onProgress);
    }

    public Promise<TResolve, TProgress> when(
            Promise<TResolve, TProgress> promise,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
            Runnable<TProgress, TProgress> onProgress) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return resolve(promise).then(onFulfilled, onRejected, onProgress);
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
     * @return {com.englishtown.promises.Promise} a new {@link Promise} that will complete with the return
     *         value of callback or errback or the completion value of promiseOrValue if
     *         callback and/or errback is not supplied.
     */
    public Promise<TResolve, TProgress> when(
            TResolve value,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(value, onFulfilled, onRejected, null);
    }

    public Promise<TResolve, TProgress> when(
            TResolve value,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(value, onFulfilled, null, null);
    }

    public Promise<TResolve, TProgress> when(
            TResolve value) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(value, null, null, null);
    }

    public Promise<TResolve, TProgress> when(
            Promise<TResolve, TProgress> promise,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(promise, onFulfilled, onRejected, null);
    }

    public Promise<TResolve, TProgress> when(
            Promise<TResolve, TProgress> promise,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(promise, onFulfilled, null, null);
    }

    public Promise<TResolve, TProgress> when(
            Promise<TResolve, TProgress> promise) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return when(promise, null, null, null);
    }

    /**
     * Returns promiseOrValue if promiseOrValue is a {@link Promise}, a new com.englishtown.promises.Promise if
     * promiseOrValue is a foreign promise, or a new, already-fulfilled {@link Promise}
     * whose value is promiseOrValue if promiseOrValue is an immediate value.
     *
     * @param {*} promiseOrValue
     * @returns Guaranteed to return a trusted com.englishtown.promises.Promise.  If promiseOrValue is a when.js {@link Promise}
     * returns promiseOrValue, otherwise, returns a new, already-resolved, when.js {@link Promise}
     * whose resolution value is:
     * * the resolution value of promiseOrValue if it's a foreign promise, or
     * * promiseOrValue if it's a value
     */
    public Promise<TResolve, TProgress> resolve(TResolve value) {
        // It's a value, not a promise.  Create a resolved promise for it.
        return fulfilled(value);
    }

    /**
     * Returns promiseOrValue if promiseOrValue is a {@link Promise}, a new com.englishtown.promises.Promise if
     * promiseOrValue is a foreign promise, or a new, already-fulfilled {@link Promise}
     * whose value is promiseOrValue if promiseOrValue is an immediate value.
     *
     * @param {*} promiseOrValue
     * @returns Guaranteed to return a trusted com.englishtown.promises.Promise.  If promiseOrValue is a when.js {@link Promise}
     * returns promiseOrValue, otherwise, returns a new, already-resolved, when.js {@link Promise}
     * whose resolution value is:
     * * the resolution value of promiseOrValue if it's a foreign promise, or
     * * promiseOrValue if it's a value
     */
    public Promise<TResolve, TProgress> resolvePromise(Promise<TResolve, TProgress> promise) {
        return resolve(promise);
    }

    private PromiseImpl resolve(Promise<TResolve, TProgress> promise) {

        // Handle null promise
        if (promise == null) {
            return fulfilled(null);
        }

        if (PromiseImpl.class.isInstance(promise)) {
            // It's a when.js promise, so we trust it
            return (PromiseImpl) promise;

        } else {

            // It's not a when.js promise. See if it's a foreign promise or a value.
            // It's a thenable, but we don't know where it came from, so don't trust
            // its implementation entirely.  Introduce a trusted middleman when.js promise
            final DeferredImpl deferred = deferInner();

            // IMPORTANT: This is the only place when.js should ever call .then() on an
            // untrusted promise. Don't expose the return value to the untrusted promise
            promise.then(
                    new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                        @Override
                        public Promise<TResolve, TProgress> run(TResolve value) {
                            deferred.resolver.resolve(value);
                            return null;
                        }
                    },
                    new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                        @Override
                        public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                            deferred.resolver.reject(reason);
                            return null;
                        }
                    },
                    new Runnable<TProgress, TProgress>() {
                        @Override
                        public TProgress run(TProgress update) {
                            deferred.resolver.progress(update);
                            return null;
                        }
                    }
            );

            return deferred.promise;

        }

    }


    /**
     * Returns a rejected promise for the supplied promiseOrValue.  The returned
     * promise will be rejected with:
     * - promiseOrValue, if it is a value, or
     * - if promiseOrValue is a promise
     * - promiseOrValue's value after it is fulfilled
     * - promiseOrValue's reason after it is rejected
     *
     * @param promise the rejected value of the returned {@link Promise}
     * @return {com.englishtown.promises.Promise} rejected {@link Promise}
     */
    public Promise<TResolve, TProgress> reject(Promise<TResolve, TProgress> promise) {
        return when(
                promise,
                new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public Promise<TResolve, TProgress> run(TResolve value) {
                        return rejected(new Reason<>(value, null));
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
     * @param value the rejected value of the returned {@link Promise}
     * @return {com.englishtown.promises.Promise} rejected {@link Promise}
     */
    public Promise<TResolve, TProgress> reject(TResolve value) {
        return reject(resolve(value));
    }

    private class PromiseImpl implements Promise<TResolve, TProgress> {

        private Thenable<TResolve, TProgress> __then;

        /**
         * Trusted com.englishtown.promises.Promise constructor.  A com.englishtown.promises.Promise created from this constructor is
         * a trusted when.js promise.  Any other duck-typed promise is considered
         * untrusted.
         *
         * @param {com.englishtown.promises.Thenable}
         *
         */
        PromiseImpl(Thenable<TResolve, TProgress> then) {
            __then = then;
        }

        @Override
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        @Override
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        @Override
        public Promise<TResolve, TProgress> then(
                Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                Runnable<TProgress, TProgress> onProgress) {
            return __then.then(onFulfilled, onRejected, onProgress);
        }

        /**
         * Register a callback that will be called when a promise is
         * fulfilled or rejected.  Optionally also register a progress handler.
         * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected, onProgress)
         *
         * @param {function?} [onFulfilledOrRejected]
         * @param {function?} [onProgress]
         * @return {com.englishtown.promises.Promise}
         */
        public Promise<TResolve, TProgress> always(
                final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected,
                Runnable<TProgress, TProgress> onProgress) {

            return this.then(
                    onFulfilledOrRejected,
                    new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                        @Override
                        public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                            return onFulfilledOrRejected.run(reason.data);
                        }
                    },
                    onProgress);
        }

        /**
         * Register a rejection handler.  Shortcut for .then(undefined, onRejected)
         *
         * @param {function?} onRejected
         * @return {com.englishtown.promises.Promise}
         */
        public Promise<TResolve, TProgress> otherwise(Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected) {
            return this.then(null, onRejected);
        }

        /**
         * Shortcut for .then(function() { return value; })
         *
         * @param {*} value
         * @return {com.englishtown.promises.Promise} a promise that:
         *         - is fulfilled if value is not a promise, or
         *         - if value is a promise, will fulfill with its value, or reject
         *         with its reason.
         */
//        public Promise<TResolve, TProgress> yield(TResolve value) {
//            return this.then(
//                    new Runnable<Promise<TResolve, TProgress>, TResolve>() {
//                        @Override
//                        public Promise<TResolve, TProgress> run(TResolve value) {
//                            return value;
//                        }
//                    },
//                    null,
//                    null);
//        }
// TODO: Could not implement yield returning a value rather than a promise...
    }

    /**
     * Create an already-resolved promise for the supplied value
     *
     * @param {*} value
     * @return {com.englishtown.promises.Promise} fulfilled promise
     */
    private PromiseImpl fulfilled(final TResolve value) {

        PromiseImpl p = new PromiseImpl(new Thenable<TResolve, TProgress>() {

            @Override
            public Promise<TResolve, TProgress> then(
                    Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                    Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                    Runnable<TProgress, TProgress> onProgress) {

                try {
                    return (onFulfilled != null ? resolve(onFulfilled.run(value)) : resolve(value));

                } catch (RuntimeException e) {
                    return rejected(new Reason<TResolve>(null, e));

                }

            }

        });

        return p;
    }

    /**
     * Create an already-rejected {@link Promise} with the supplied
     * rejection reason.
     *
     * @param {*} reason
     * @return {com.englishtown.promises.Promise} rejected promise
     */
    private Promise<TResolve, TProgress> rejected(final Reason<TResolve> reason) {

        PromiseImpl p = new PromiseImpl(new Thenable<TResolve, TProgress>() {

            @Override
            public Promise<TResolve, TProgress> then(
                    Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                    Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                    Runnable<TProgress, TProgress> onProgress) {

                try {
                    return onRejected != null ? resolve(onRejected.run(reason)) : rejected(reason);

                } catch (RuntimeException e) {
                    return rejected(new Reason<TResolve>(null, e));

                }
            }
        });

        return p;
    }

    private class DeferredImpl implements Deferred<TResolve, TProgress> {

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
        public Promise<TResolve, TProgress> getPromise() {
            return promise;
        }
    }

    private class ResolverImpl implements Resolver<TResolve, TProgress> {

        public Runnable<Promise<TResolve, TProgress>, TResolve> resolve;
        public Runnable<Promise<TResolve, TProgress>, Promise<TResolve, TProgress>> resolvePromise;
        public Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> reject;
        public Runnable<TProgress, TProgress> progress;

        public ResolverImpl(
                Runnable<Promise<TResolve, TProgress>, TResolve> resolve,
                Runnable<Promise<TResolve, TProgress>, Promise<TResolve, TProgress>> resolvePromise,
                Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> reject,
                Runnable<TProgress, TProgress> progress) {
            this.resolve = resolve;
            this.resolvePromise = resolvePromise;
            this.reject = reject;
            this.progress = progress;
        }

        @Override
        public Promise<TResolve, TProgress> resolve(TResolve value) {
            return resolve.run(value);
        }

        @Override
        public Promise<TResolve, TProgress> resolve(Promise<TResolve, TProgress> value) {
            return resolvePromise.run(value);
        }

        @Override
        public Promise<TResolve, TProgress> reject(TResolve reason) {
            return reject(new Reason<>(reason, null));
        }

        @Override
        public Promise<TResolve, TProgress> reject(Reason<TResolve> reason) {
            return reject.run(reason);
        }

        @Override
        public TProgress progress(TProgress update) {
            return progress.run(update);
        }
    }

    public Deferred<TResolve, TProgress> defer() {
        When<TResolve, TProgress> when = new When<>();
        return when.deferInner();
    }

    /**
     * Creates a new, Deferred with fully isolated resolver and promise parts,
     * either or both of which may be given out safely to consumers.
     * The Deferred itself has the full API: resolve, reject, progress, and
     * then. The resolver has resolve, reject, and progress.  The promise
     * only has then.
     *
     * @return {Deferred}
     */
    private DeferredImpl deferInner() {

        final List<Runnable<Void, Promise<TResolve, TProgress>>> handlers = new ArrayList<>();
        final List<Runnable<TProgress, TProgress>> progressHandlers = new ArrayList<>();

        /**
         * Pre-resolution then() that adds the supplied callback, errback, and progback
         * functions to the registered listeners
         * @private
         *
         * @param {function?} [onFulfilled] resolution handler
         * @param {function?} [onRejected] rejection handler
         * @param {function?} [onProgress] progress handler
         */
        final Value<Thenable<TResolve, TProgress>> _then = new Value<>();
        _then.value = new Thenable<TResolve, TProgress>() {
            @Override
            public Promise<TResolve, TProgress> then(
                    final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                    final Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                    final Runnable<TProgress, TProgress> onProgress) {

                final Value<Runnable<TProgress, TProgress>> progressHandler = new Value<>();
                final DeferredImpl deferred = deferInner();

                if (onProgress != null) {
                    progressHandler.value = new Runnable<TProgress, TProgress>() {
                        @Override
                        public TProgress run(TProgress update) {
                            try {
                                // Allow progress handler to transform progress event
                                deferred.resolver.progress(onProgress.run(update));

                            } catch (RuntimeException e) {
                                // Use caught value as progress
                                deferred.resolver.progress(update);
                                // TODO: Could not pass exception through to progress.run(),
                                // passing update instead.  Consider passing Reason<TData>?

                            }

                            return null;
                        }
                    };
                } else {
                    progressHandler.value = new Runnable<TProgress, TProgress>() {
                        @Override
                        public TProgress run(TProgress update) {
                            deferred.resolver.progress(update);
                            return null;
                        }
                    };
                }

                handlers.add(new Runnable<Void, Promise<TResolve, TProgress>>() {
                    @Override
                    public Void run(Promise<TResolve, TProgress> promise) {
                        promise.then(onFulfilled, onRejected)
                                .then(deferred.resolver.resolve, deferred.resolver.reject, progressHandler.value);
                        return null;
                    }
                });

                progressHandlers.add(progressHandler.value);

                return deferred.promise;
            }
        };

        /**
         * Issue a progress event, notifying all progress listeners
         * @private
         * @param {*} update progress event payload to pass to all listeners
         */
        final Value<Runnable<TProgress, TProgress>> _progress = new Value<>();
        _progress.value = new Runnable<TProgress, TProgress>() {
            @Override
            public TProgress run(TProgress update) {
                processQueue(progressHandlers, update);
                return update;
            }
        };

        /**
         * Transition from pre-resolution state to post-resolution state, notifying
         * all listeners of the resolution or rejection
         * @private
         * @param {*} value the value of this deferred
         */
        final Value<Runnable<Promise<TResolve, TProgress>, TResolve>> _resolve = new Value<>();

        final Runnable<Promise<TResolve, TProgress>, Promise<TResolve, TProgress>> _resolvePromise =
                new Runnable<Promise<TResolve, TProgress>, Promise<TResolve, TProgress>>() {
                    @Override
                    public Promise<TResolve, TProgress> run(Promise<TResolve, TProgress> p1) {
                        PromiseImpl p2 = resolve(p1);

                        // Replace _then with one that directly notifies with the result.
                        _then.value = p2.__then;
                        // Replace _resolve so that this Deferred can only be resolved once
                        _resolve.value = new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                            @Override
                            public Promise<TResolve, TProgress> run(TResolve value) {
                                return resolve(value);
                            }
                        };
                        // Make _progress null, to disallow progress for the resolved promise.
                        _progress.value = null;

                        // Notify handlers
                        processQueue(handlers, p2);

                        // Free progressHandlers array since we'll never issue progress events
                        progressHandlers.clear();
                        handlers.clear();

                        return p2;
                    }
                };

        _resolve.value = new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return _resolvePromise.run(resolve(value));
            }
        }; // TODO: Can I get rid of _resolve or _resolvePromise?

        // Runnable wrappers
        Runnable<Promise<TResolve, TProgress>, TResolve> promiseResolve = new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return _resolve.value.run(value);
            }
        };
        Runnable<Promise<TResolve, TProgress>, Promise<TResolve, TProgress>> promiseResolvePromise = new
                Runnable<Promise<TResolve, TProgress>, Promise<TResolve, TProgress>>() {
                    @Override
                    public Promise<TResolve, TProgress> run(Promise<TResolve, TProgress> promise) {
                        return _resolvePromise.run(promise);
                    }
                };
        Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> promiseReject = new Runnable<Promise<TResolve,
                TProgress>, Reason<TResolve>>() {
            @Override
            public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                return _resolvePromise.run(resolve(rejected(reason)));
            }
        };
        Runnable<TProgress, TProgress> promiseProgress = new Runnable<TProgress, TProgress>() {
            @Override
            public TProgress run(TProgress value) {
                return (_progress.value != null ? _progress.value.run(value) : null);
            }
        };

        /**
         * The promise for the new deferred
         * @type {com.englishtown.promises.Promise}
         */
        PromiseImpl promise = new PromiseImpl(new Thenable<TResolve, TProgress>() {
            @Override
            public Promise<TResolve, TProgress> then(
                    Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                    Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                    Runnable<TProgress, TProgress> onProgress) {
                return _then.value.then(onFulfilled, onRejected, onProgress);
            }
        });

        ResolverImpl resolver = new ResolverImpl(
                promiseResolve,
                promiseResolvePromise,
                promiseReject,
                promiseProgress);

        /**
         * The full Deferred object, with {@link PromiseImpl} and {@link ResolverImpl}
         * parts
         * @class Deferred
         * @name Deferred
         */
        return new DeferredImpl(promise, resolver);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> some(
            final List<TResolve> values,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled) {
        return some(values, howMany, onFulfilled, null, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> some(
            final List<TResolve> values,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected) {
        return some(values, howMany, onFulfilled, onRejected, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> some(
            final List<TResolve> values,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            final Runnable<TProgress, TProgress> onProgress) {
        return somePromises(resolveValues(values), howMany, onFulfilled, onRejected, onProgress);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> somePromise(
            final Promise<List<TResolve>, TProgress> promise,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled) {
        return somePromise(promise, howMany, onFulfilled, null, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> somePromise(
            final Promise<List<TResolve>, TProgress> promise,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected) {
        return somePromise(promise, howMany, onFulfilled, onRejected, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> somePromise(
            final Promise<List<TResolve>, TProgress> promise,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            final Runnable<TProgress, TProgress> onProgress) {

        When<List<TResolve>, TProgress> when = new When<>();

        return when.when(promise, new Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>>() {
            @Override
            public Promise<List<TResolve>, TProgress> run(List<TResolve> value) {
                return some(value, howMany, onFulfilled, onRejected, onProgress);
            }
        });

    }


    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> somePromises(
            final List<Promise<TResolve, TProgress>> promises,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled) {
        return somePromises(promises, howMany, onFulfilled, null, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> somePromises(
            final List<Promise<TResolve, TProgress>> promises,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected) {
        return somePromises(promises, howMany, onFulfilled, onRejected, null);
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * howMany of the supplied promisesOrValues have resolved, or will reject when
     * it becomes impossible for howMany to resolve, for example, when
     * (promisesOrValues.length - howMany) + 1 input promises reject.
     *
     * @param {Array}     promisesOrValues array of anything, may contain a mix
     *                    of promises and values
     * @param howMany     {number} number of promisesOrValues to resolve
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
     * rejection reasons.
     */
    public Promise<List<TResolve>, TProgress> somePromises(
            final List<Promise<TResolve, TProgress>> promises,
            final int howMany,
            final Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            final Runnable<TProgress, TProgress> onProgress) {

        final When<List<TResolve>, TProgress> w1 = new When<>();
        final When<List<TResolve>, TProgress>.DeferredImpl d1 = w1.deferInner();
        When<List<Promise<TResolve, TProgress>>, TProgress> w2 = new When<>();
        final When<TResolve, TProgress> w3 = new When<>();

        w2.when(promises, new Runnable<Promise<List<Promise<TResolve, TProgress>>, TProgress>, List<Promise<TResolve, TProgress>>>() {
            @Override
            public Promise<List<Promise<TResolve, TProgress>>, TProgress> run(List<Promise<TResolve, TProgress>> promises) {

                int len = promises.size();

                final Value<Integer> toResolve = new Value<>(Math.max(0, Math.min(howMany, len)));
                final List<TResolve> values = new ArrayList<>();

                final Value<Integer> toReject = new Value<>((len - toResolve.value) + 1);
                final List<TResolve> reasons = new ArrayList<>();

                // No items in the input, resolve immediately
                if (toResolve.value == 0) {
                    d1.getResolver().resolve(values);

                } else {
                    Runnable<TProgress, TProgress> progress = d1.resolver.progress;

                    final Value<Runnable<Promise<TResolve, TProgress>, TResolve>> fulfillOne = new Value<>();
                    final Value<Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>> rejectOne = new Value<>();

                    rejectOne.value = new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                        @Override
                        public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                            reasons.add(reason.data);

                            if (--toReject.value == 0) {
                                rejectOne.value = null;
                                fulfillOne.value = null;
                                d1.getResolver().reject(new Reason<>(reasons, reason.error));
                            }

                            return null;
                        }
                    };

                    fulfillOne.value = new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                        @Override
                        public Promise<TResolve, TProgress> run(TResolve value) {
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

                    for (int i = 0; i < len; ++i) {
                        if (i < promises.size()) {
                            Promise<TResolve, TProgress> p3 = promises.get(i);

                            if (p3 != null) {
                                w3.when(
                                        p3,
                                        new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                                            @Override
                                            public Promise<TResolve, TProgress> run(TResolve value) {
                                                return (fulfillOne.value != null ? fulfillOne.value.run(value) : null);
                                            }
                                        },
                                        new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                                            @Override
                                            public Promise<TResolve, TProgress> run(Reason<TResolve> value) {
                                                return (rejectOne.value != null ? rejectOne.value.run(value) : null);
                                            }
                                        },
                                        progress
                                );
                            }
                        }
                    }
                }

                d1.getPromise().then(onFulfilled, onRejected, onProgress);
                return null;
            }
        }).then(null, new Runnable<Promise<List<Promise<TResolve, TProgress>>, TProgress>, Reason<List<Promise<TResolve, TProgress>>>>() {
            @Override
            public Promise<List<Promise<TResolve, TProgress>>, TProgress> run(Reason<List<Promise<TResolve, TProgress>>> value) {
                // Need to reject the deferred if an exception is thrown above
                d1.getResolver().reject(new Reason<List<TResolve>>(null, value.error));
                return null;
            }
        });

        return d1.getPromise();

    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promisesOrValues has resolved or will reject when
     * *all* promisesOrValues have rejected.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                    promisesOrValues array of anything, may contain a mix
     *                    of {@link com.englishtown.promises.Promise}s and values
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to the value that resolved first, or
     * will reject with an array of all rejected inputs.
     */
    public Promise<List<TResolve>, TProgress> any(
            final List<TResolve> values,
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            final Runnable<TProgress, TProgress> onProgress) {
        return anyPromises(resolveValues(values), onFulfilled, onRejected, onProgress);
    }
    // TODO: Overload without all callbacks

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promisesOrValues has resolved or will reject when
     * *all* promisesOrValues have rejected.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                    promisesOrValues array of anything, may contain a mix
     *                    of {@link com.englishtown.promises.Promise}s and values
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to the value that resolved first, or
     * will reject with an array of all rejected inputs.
     */
    public Promise<List<TResolve>, TProgress> anyPromise(
            final Promise<List<TResolve>, TProgress> promise,
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            final Runnable<TProgress, TProgress> onProgress) {

        When<List<TResolve>, TProgress> when = new When<>();

        return when.when(promise, new Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>>() {
            @Override
            public Promise<List<TResolve>, TProgress> run(List<TResolve> value) {
                return any(value, onFulfilled, onRejected, onProgress);
            }
        });
    }

    /**
     * Initiates a competitive race, returning a promise that will resolve when
     * any one of the supplied promisesOrValues has resolved or will reject when
     * *all* promisesOrValues have rejected.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                    promisesOrValues array of anything, may contain a mix
     *                    of {@link com.englishtown.promises.Promise}s and values
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @returns {com.englishtown.promises.Promise} promise that will resolve to the value that resolved first, or
     * will reject with an array of all rejected inputs.
     */
    public Promise<List<TResolve>, TProgress> anyPromises(
            final List<Promise<TResolve, TProgress>> promises,
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            final Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            final Runnable<TProgress, TProgress> onProgress) {

        Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> unwrapSingleResult = new
                Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>>() {
                    @Override
                    public Promise<List<TResolve>, TProgress> run(List<TResolve> value) {
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

        return somePromises(promises, 1, unwrapSingleResult, onRejected, onProgress);
    }

    /**
     * Return a promise that will resolve only once all the supplied promisesOrValues
     * have resolved. The resolution value of the returned promise will be an array
     * containing the resolution values of each of the promisesOrValues.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                    promisesOrValues array of anything, may contain a mix
     *                    of {@link com.englishtown.promises.Promise}s and values
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @memberOf when
     * @returns {com.englishtown.promises.Promise}
     */
    public Promise<List<TResolve>, TProgress> all(
            List<TResolve> values,
            Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            Runnable<TProgress, TProgress> onProgress) {

        return map(values, new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return resolve(value);
            }
        }).then(onFulfilled, onRejected, onProgress);

    }

    /**
     * Return a promise that will resolve only once all the supplied promisesOrValues
     * have resolved. The resolution value of the returned promise will be an array
     * containing the resolution values of each of the promisesOrValues.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                    promisesOrValues array of anything, may contain a mix
     *                    of {@link com.englishtown.promises.Promise}s and values
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @memberOf when
     * @returns {com.englishtown.promises.Promise}
     */
    public Promise<List<TResolve>, TProgress> allPromise(
            Promise<List<TResolve>, TProgress> promise,
            Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            Runnable<TProgress, TProgress> onProgress) {

        return mapPromise(promise, new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return resolve(value);
            }
        }).then(onFulfilled, onRejected, onProgress);

    }

    /**
     * Return a promise that will resolve only once all the supplied promisesOrValues
     * have resolved. The resolution value of the returned promise will be an array
     * containing the resolution values of each of the promisesOrValues.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                    promisesOrValues array of anything, may contain a mix
     *                    of {@link com.englishtown.promises.Promise}s and values
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @memberOf when
     * @returns {com.englishtown.promises.Promise}
     */
    public Promise<List<TResolve>, TProgress> allPromises(
            List<Promise<TResolve, TProgress>> promises,
            Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>> onFulfilled,
            Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>> onRejected,
            Runnable<TProgress, TProgress> onProgress) {

        return mapPromises(promises, new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return resolve(value);
            }
        }).then(onFulfilled, onRejected, onProgress);

    }

    /**
     * Joins multiple promises into a single returned promise.
     *
     * @return {com.englishtown.promises.Promise} a promise that will fulfill when *all* the input promises
     *         have fulfilled, or will reject when *any one* of the input promises rejects.
     */
    @SafeVarargs
    public final Promise<List<TResolve>, TProgress> join(TResolve... values) {

        return map(Arrays.asList(values), new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return resolve(value);
            }
        });

    }

    /**
     * Joins multiple promises into a single returned promise.
     *
     * @return {com.englishtown.promises.Promise} a promise that will fulfill when *all* the input promises
     *         have fulfilled, or will reject when *any one* of the input promises rejects.
     */
    @SafeVarargs
    public final Promise<List<TResolve>, TProgress> joinPromises(Promise<TResolve, TProgress>... promises) {

        return mapPromises(Arrays.asList(promises), new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                return resolve(value);
            }
        });

    }

    /**
     * Traditional map function, similar to `Array.prototype.map()`, but allows
     * input to contain {@link Promise}s and/or values, and mapFunc may return
     * either a value or a {@link Promise}
     *
     * @param {Array|Promise} promise array of anything, may contain a mix
     *                        of {@link Promise}s and values
     * @param {function}      mapFunc mapping function mapFunc(value) which may return
     *                        either a {@link Promise} or value
     * @returns {Promise} a {@link Promise} that will resolve to an array containing
     * the mapped output values.
     */
    public Promise<List<TResolve>, TProgress> map(
            List<TResolve> values,
            Runnable<Promise<TResolve, TProgress>, TResolve> mapFunc) {
        return mapPromises(resolveValues(values), mapFunc);
    }

    /**
     * Traditional map function, similar to `Array.prototype.map()`, but allows
     * input to contain {@link Promise}s and/or values, and mapFunc may return
     * either a value or a {@link Promise}
     *
     * @param {Array|Promise} promise array of anything, may contain a mix
     *                        of {@link Promise}s and values
     * @param {function}      mapFunc mapping function mapFunc(value) which may return
     *                        either a {@link Promise} or value
     * @returns {Promise} a {@link Promise} that will resolve to an array containing
     * the mapped output values.
     */
    public Promise<List<TResolve>, TProgress> mapPromise(
            Promise<List<TResolve>, TProgress> promise,
            final Runnable<Promise<TResolve, TProgress>, TResolve> mapFunc) {

        When<List<TResolve>, TProgress> when = new When<>();

        return when.when(promise, new Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>>() {
            @Override
            public Promise<List<TResolve>, TProgress> run(List<TResolve> value) {
                return map(value, mapFunc);
            }
        });

    }

    /**
     * Traditional map function, similar to `Array.prototype.map()`, but allows
     * input to contain {@link Promise}s and/or values, and mapFunc may return
     * either a value or a {@link Promise}
     *
     * @param {Array|Promise} promise array of anything, may contain a mix
     *                        of {@link Promise}s and values
     * @param {function}      mapFunc mapping function mapFunc(value) which may return
     *                        either a {@link Promise} or value
     * @returns {Promise} a {@link Promise} that will resolve to an array containing
     * the mapped output values.
     */
    public Promise<List<TResolve>, TProgress> mapPromises(
            List<Promise<TResolve, TProgress>> promises,
            final Runnable<Promise<TResolve, TProgress>, TResolve> mapFunc) {

        final When<List<TResolve>, TProgress> w1 = new When<>();
        final Deferred<List<TResolve>, TProgress> d1 = w1.deferInner();

        final When<List<Promise<TResolve, TProgress>>, TProgress> w2 = new When<>();
        final When<TResolve, TProgress> w3 = new When<>();

        w2.when(promises, new Runnable<Promise<List<Promise<TResolve, TProgress>>, TProgress>, List<Promise<TResolve, TProgress>>>() {
            @Override
            public Promise<List<Promise<TResolve, TProgress>>, TProgress> run(List<Promise<TResolve, TProgress>> array) {

                final Value<Integer> toResolve = new Value<>();
                int len;

                // Since we know the resulting length, we can preallocate the results
                // array to avoid array expansions.
                toResolve.value = len = array.size();
                final List<TResolve> results = new ArrayList<>(len);
                for (int i = 0; i < len; i++) {
                    results.add(null);
                }

                if (toResolve.value == 0) {
                    d1.getResolver().resolve(results);

                } else {
                    Runnable2<Void, Promise<TResolve, TProgress>, Integer> resolveOne = new Runnable2<Void, Promise<TResolve, TProgress>, Integer>() {
                        @Override
                        public Void run(Promise<TResolve, TProgress> item, final Integer index) {

                            w3.when(item, new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                                @Override
                                public Promise<TResolve, TProgress> run(TResolve value) {
                                    return (mapFunc != null ? w3.resolve(mapFunc.run(value)) : w3.resolve(value));
                                }
                            }).then(
                                    new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                                        @Override
                                        public Promise<TResolve, TProgress> run(TResolve mapped) {
                                            results.set(index, mapped);

                                            if (--toResolve.value == 0) {
                                                d1.getResolver().resolve(results);
                                            }

                                            return null;
                                        }
                                    },
                                    new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                                        @Override
                                        public Promise<TResolve, TProgress> run(Reason<TResolve> value) {
                                            d1.getResolver().reject(new Reason<>(Arrays.asList(value.data), value.error));
                                            return null;
                                        }
                                    },
                                    null
                            );

                            return null;
                        }
                    };

                    // Since mapFunc may be async, get all invocations of it into flight
                    for (int i = 0; i < len; i++) {
                        Promise<TResolve, TProgress> p = null;

                        if (i < array.size()) {
                            p = array.get(i);
                        }

                        if (p != null) {
                            resolveOne.run(array.get(i), i);
                        } else {
                            --toResolve.value;
                        }
                    }

                }

                return null;
            }
        }).then(null, new Runnable<Promise<List<Promise<TResolve, TProgress>>, TProgress>,
                Reason<List<Promise<TResolve, TProgress>>>>() {
            @Override
            public Promise<List<Promise<TResolve, TProgress>>, TProgress> run(Reason<List<Promise<TResolve, TProgress>>> value) {
                // Need to reject the deferred if an exception is thrown above
                d1.getResolver().reject(new Reason<List<TResolve>>(null, value.error));
                return null;
            }
        });

        return d1.getPromise();
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reduce(
            List<TResolve> values,
            final Reducer2<TResolve> reduceFunc) {
        Promise<TResolve, TProgress> initialValue = null;
        return reduce(values, reduceFunc, initialValue);
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reduce(
            List<TResolve> values,
            final Reducer2<TResolve> reduceFunc,
            final TResolve initialValue) {
        return reduce(values, reduceFunc, (initialValue != null ? resolve(initialValue) : null));
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reducePromise(
            Promise<List<TResolve>, TProgress> promise,
            final Reducer2<TResolve> reduceFunc,
            final TResolve initialValue) {


        final Deferred<TResolve, TProgress> d1 = deferInner();
        final When<List<TResolve>, TProgress> w2 = new When<>();

        w2.when(promise,
                new Runnable<Promise<List<TResolve>, TProgress>, List<TResolve>>() {
                    @Override
                    public Promise<List<TResolve>, TProgress> run(List<TResolve> values) {
                        d1.getResolver().resolve(reduce(values, reduceFunc, initialValue));
                        return null;
                    }
                },
                new Runnable<Promise<List<TResolve>, TProgress>, Reason<List<TResolve>>>() {
                    @Override
                    public Promise<List<TResolve>, TProgress> run(Reason<List<TResolve>> value) {
                        d1.getResolver().reject(new Reason<TResolve>(null, value.error));
                        return null;
                    }
                }
        );

        return d1.getPromise();
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reduce(
            List<TResolve> values,
            final Reducer2<TResolve> reduceFunc,
            final Promise<TResolve, TProgress> initialValue) {
        return reducePromises(resolveValues(values), reduceFunc, initialValue);
    }


    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reducePromises(
            List<Promise<TResolve, TProgress>> promises,
            final Reducer2<TResolve> reduceFunc) {
        Promise<TResolve, TProgress> initialValue = null;
        return reducePromises(promises, reduceFunc, initialValue);
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reducePromises(
            List<Promise<TResolve, TProgress>> promises,
            final Reducer2<TResolve> reduceFunc,
            TResolve initialValue) {
        return reducePromises(promises, reduceFunc, (initialValue != null ? resolve(initialValue) : null));
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may
     * be a promise for the starting value.
     *
     * @param {Array|com.englishtown.promises.Promise}
     *                   promise array or promise for an array of anything,
     *                   may contain a mix of promises and values.
     * @param {function} reduceFunc reduce function reduce(currentValue, nextValue, index, total),
     *                   where total is the total number of items being reduced, and will be the same
     *                   in each call to reduceFunc.
     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
     */
    public Promise<TResolve, TProgress> reducePromises(
            List<Promise<TResolve, TProgress>> promises,
            final Reducer2<TResolve> reduceFunc,
            final Promise<TResolve, TProgress> initialValue) {
        //var args = slice.call(arguments, 1);

        final When<List<Promise<TResolve, TProgress>>, TProgress> w1 = new When<>();
        final When<TResolve, TProgress> w2 = new When<>();
        final Deferred<TResolve, TProgress> d2 = w2.deferInner();

        w1.when(promises, new Runnable<Promise<List<Promise<TResolve, TProgress>>, TProgress>, List<Promise<TResolve, TProgress>>>() {
            @Override
            public Promise<List<Promise<TResolve, TProgress>>, TProgress> run(List<Promise<TResolve,
                    TProgress>> array) {

                final int total = array.size();

                // Wrap the supplied reduceFunc with one that handles promises and then
                // delegates to the supplied.
                Reducer<Promise<TResolve, TProgress>> reducerWrapper = new Reducer<Promise<TResolve, TProgress>>() {
                    @Override
                    public Promise<TResolve, TProgress> run(
                            Promise<TResolve, TProgress> current,
                            final Promise<TResolve, TProgress> val,
                            final int i,
                            List<Promise<TResolve, TProgress>> list) {

                        return w2.when(current, new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                            @Override
                            public Promise<TResolve, TProgress> run(final TResolve c) {
                                return w2.when(val, new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                                    @Override
                                    public Promise<TResolve, TProgress> run(TResolve value) {
                                        return w2.resolve(reduceFunc.run(c, value, i, total));
                                    }
                                });
                            }
                        });
                    }
                };

                Promise<TResolve, TProgress> p = reduceList(array, reducerWrapper, initialValue);
                d2.getResolver().resolve(p);
                return null;
            }
        }).then(null, new Runnable<Promise<List<Promise<TResolve, TProgress>>, TProgress>,
                Reason<List<Promise<TResolve, TProgress>>>>() {
            @Override
            public Promise<List<Promise<TResolve, TProgress>>, TProgress> run(Reason<List<Promise<TResolve, TProgress>>> value) {
                d2.getResolver().reject(new Reason<TResolve>(null, value.error));
                return null;
            }
        });

        return d2.getPromise();
    }

    private static <T> T reduceList(List<T> list, Reducer<T> reduceFunc, T initialValue) {
        /*jshint maxcomplexity: 7*/

        // ES5 dictates that reduce.length === 1

        // This implementation deviates from ES5 spec in the following ways:
        // 1. It does not check if reduceFunc is a Callable
        T reduced;
        //var arr, args, len;

        int i = 0;
        // This generates a jshint warning, despite being valid
        // "Missing 'new' prefix when invoking a constructor."
        // See https://github.com/jshint/jshint/issues/392
        //arr = Object(this);
        int len = (list == null ? 0 : list.size());
        //args = arguments;

        // If no initialValue, use first item of array (we know length !== 0 here)
        // and adjust i to start at second item
        if (initialValue == null) {
            // Skip to the first real element in the array
            for (; ; ) {
                // If we reached the end of the array without finding any real
                // elements, it's a TypeError
                if (i >= len) {
                    throw new RuntimeException("No non-null values to reduce.");
                }

                T temp = list.get(i);
                if (temp != null) {
                    reduced = temp;
                    i++;
                    break;
                }

                i++;
            }
        } else {
            // If initialValue provided, use it
            reduced = initialValue;
        }

        // Do the actual reduce
        for (; i < len; ++i) {
            // Skip holes
            T val = list.get(i);
            if (val != null) {
                reduced = reduceFunc.run(reduced, val, i, list);
            }
        }

        return reduced;
    }

    /**
     * Ensure that resolution of promiseOrValue will trigger resolver with the
     * value or reason of promiseOrValue, or instead with resolveValue if it is provided.
     *
     * @param {com.englishtown.promises.Promise}
     *                   promise
     * @param {Object}   resolver
     * @param {function} resolver.resolve
     * @param {function} resolver.reject
     * @param {*}        [resolveValue]
     * @return {com.englishtown.promises.Promise}
     */
    public Promise<TResolve, TProgress> chain(
            TResolve value,
            final Resolver<TResolve, TProgress> resolver,
            final TResolve resolveValue) {
        return chain(resolve(value), resolver, resolveValue);
    } // TODO: Overload without resolveValue

    /**
     * Ensure that resolution of promiseOrValue will trigger resolver with the
     * value or reason of promiseOrValue, or instead with resolveValue if it is provided.
     *
     * @param {com.englishtown.promises.Promise}
     *                   promise
     * @param {Object}   resolver
     * @param {function} resolver.resolve
     * @param {function} resolver.reject
     * @param {*}        [resolveValue]
     * @return {com.englishtown.promises.Promise}
     */
    public Promise<TResolve, TProgress> chain(
            Promise<TResolve, TProgress> promise,
            final Resolver<TResolve, TProgress> resolver,
            final TResolve resolveValue) { // TODO: Overload without resolveValue

        return when(
                promise,
                new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public Promise<TResolve, TProgress> run(TResolve val) {
                        val = resolveValue != null ? resolveValue : val;
                        resolver.resolve(val);
                        return resolve(val);
                    }
                },
                new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                    @Override
                    public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                        resolver.reject(reason);
                        return rejected(reason);
                    }
                },
                new Runnable<TProgress, TProgress>() {
                    @Override
                    public TProgress run(TProgress value) {
                        return resolver.progress(value);
                    }
                }
        );

    }

    /**
     * Apply all functions in queue to value
     *
     * @param {Array} queue array of functions to execute
     * @param {*}     value argument passed to each function
     */

    private <T1, T2> void processQueue(List<Runnable<T1, T2>> queue, T2 value) {
        for (Runnable<T1, T2> handler : queue) {
            handler.run(value);
        }
    }

    private List<Promise<TResolve, TProgress>> resolveValues(List<TResolve> values) {
        if (values == null) {
            return null;
        }

        List<Promise<TResolve, TProgress>> promises = new ArrayList<>();

        for (TResolve value : values) {
            promises.add((value != null ? resolve(value) : null));
        }

        return promises;
    }

}
