package com.englishtown.promises;

import java.util.ArrayList;
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
/** @license MIT License (c) copyright B Cavalier & J Hann */
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
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
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
            Promise<TResolve, TProgress> promise,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
            Runnable<TProgress, TProgress> onProgress) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return resolve(promise).then(onFulfilled, onRejected, onProgress);
    }

    public Promise<TResolve, TProgress> when(
            Promise<TResolve, TProgress> promise,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return resolve(promise).then(onFulfilled, onRejected, null);
    }

    public Promise<TResolve, TProgress> when(
            Promise<TResolve, TProgress> promise,
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled) {
        // Get a trusted promise for the input promiseOrValue, and then
        // register promise handlers
        return resolve(promise).then(onFulfilled, null, null);
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
    public PromiseImpl resolve(TResolve value) {
        // It's a value, not a promise.  Create a resolved promise for it.
        return fulfilled(value);
    }

    public PromiseImpl resolve(Promise<TResolve, TProgress> promise) {

        // Handle null promise
        if (promise == null) {
            TResolve value = null;
            return resolve(value);
        }

        if (PromiseImpl.class.isInstance(promise)) {
            // It's a when.js promise, so we trust it
            return (PromiseImpl) promise;

        } else {

            // It's not a when.js promise. See if it's a foreign promise or a value.
            // It's a thenable, but we don't know where it came from, so don't trust
            // its implementation entirely.  Introduce a trusted middleman when.js promise
            final DeferredImpl deferred = defer();

            // IMPORTANT: This is the only place when.js should ever call .then() on an
            // untrusted promise. Don't expose the return value to the untrusted promise
            promise.then(
                    new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                        @Override
                        public Promise<TResolve, TProgress> run(TResolve value) {
                            deferred.resolve.run(value);
                            return null;
                        }
                    },
                    new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                        @Override
                        public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                            deferred.reject.run(reason);
                            return null;
                        }
                    },
                    new Runnable<TProgress, TProgress>() {
                        @Override
                        public TProgress run(TProgress update) {
                            deferred.progress.run(update);
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
                        return rejected(new Reason<TResolve>(value, null));
                    }
                },
                null,
                null);
    }
    // TODO: Overload and take a value to reject?

    public class PromiseImpl implements Promise<TResolve, TProgress> {

        private Thenable<TResolve, TProgress> __then;

        /**
         * Trusted com.englishtown.promises.Promise constructor.  A com.englishtown.promises.Promise created from this constructor is
         * a trusted when.js promise.  Any other duck-typed promise is considered
         * untrusted.
         *
         * @param {com.englishtown.promises.Thenable}
         */
        protected PromiseImpl(Thenable<TResolve, TProgress> then) {
            __then = then;
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
            return this.then(null, onRejected, null); // TODO: Replace null with noop?
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
//        public com.englishtown.promises.Promise<TResolve, TProgress> yield(TResolve value) {
//            return this.then(new com.englishtown.promises.Runnable<TResolve, com.englishtown.promises.Promise<TResolve, TProgress>>() {
//                @Override
//                public com.englishtown.promises.Promise<TResolve, TProgress> run(TResolve value) {
//                    return value;
//                }
//            });
//        }
// TODO: Could not implement yield returning a value rather than a promise

        /**
         * Assumes that this promise will fulfill with an array, and arranges
         * for the onFulfilled to be called with the array as its argument list
         * i.e. onFulfilled.spread(undefined, array).
         * @param {function} onFulfilled function to receive spread arguments
         * @return {com.englishtown.promises.Promise}
         */
//        public com.englishtown.promises.Promise<TResolve, TProgress> spread(com.englishtown.promises.Runnable<TResolve, com.englishtown.promises.Promise<TResolve, TProgress>> onFulfilled) {
//            return this.then(function(array) {
//                // array may contain promises, so resolve its contents.
//                return all(array, function(array) {
//                    return onFulfilled.apply(undef, array);
//                });
//            });
//        }
        // TODO: Could not implement spread
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

    // TODO: Extract interfaces for DeferredImpl and Resolver
    public class DeferredImpl {

        protected DeferredImpl() {
            resolver = new Resolver();
        }

        //        public com.englishtown.promises.Thenable<TResolve, TProgress> then;
        public Runnable<Promise<TResolve, TProgress>, TResolve> resolve;
        public Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> reject;
        public Runnable<TProgress, TProgress> progress;

        public Resolver resolver;
        public PromiseImpl promise;

    }

    public class Resolver {
        public Runnable<Promise<TResolve, TProgress>, TResolve> resolve;
        public Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> reject;
        public Runnable<TProgress, TProgress> progress;
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
    public DeferredImpl defer() {
        DeferredImpl deferred;
        PromiseImpl promise;

//                , handlers, progressHandlers,
//                _then, _progress, _resolve;


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
        // TODO: Change com.englishtown.promises.Value<TResolve> to take value in constructor?
        final Value<Thenable<TResolve, TProgress>> _then = new Value<>();
        _then.value = new Thenable<TResolve, TProgress>() {
            @Override
            public Promise<TResolve, TProgress> then(
                    final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                    final Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                    final Runnable<TProgress, TProgress> onProgress) {

                final Value<Runnable<TProgress, TProgress>> progressHandler = new Value<>();
                final DeferredImpl deferred = defer();

                if (onProgress != null) {
                    progressHandler.value = new Runnable<TProgress, TProgress>() {
                        @Override
                        public TProgress run(TProgress update) {
                            try {
                                // Allow progress handler to transform progress event
                                deferred.progress.run(onProgress.run(update));

                            } catch (RuntimeException e) {
                                // Use caught value as progress
                                deferred.progress.run(update);
                                // TODO: Could not pass exception through to progress.run(), passing update instead

                            }

                            return null;
                        }
                    };
                } else {
                    progressHandler.value = new Runnable<TProgress, TProgress>() {
                        @Override
                        public TProgress run(TProgress update) {
                            deferred.progress.run(update);
                            return null;
                        }
                    };
                }

                handlers.add(new Runnable<Void, Promise<TResolve, TProgress>>() {
                    @Override
                    public Void run(Promise<TResolve, TProgress> promise) {
                        promise.then(onFulfilled, onRejected, null)
                                .then(deferred.resolve, deferred.reject, progressHandler.value);
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
        final Value<Runnable<Promise<TResolve, TProgress>, PromiseImpl>> _resolvePromise = new Value<>();
        _resolve.value = new Runnable<Promise<TResolve, TProgress>, TResolve>() {
            @Override
            public Promise<TResolve, TProgress> run(TResolve value) {
                PromiseImpl p = resolve(value);
                return _resolvePromise.value.run(p);
            }
        };

        _resolvePromise.value = new Runnable<Promise<TResolve, TProgress>, PromiseImpl>() {
            @Override
            public Promise<TResolve, TProgress> run(PromiseImpl p) {

                // Replace _then with one that directly notifies with the result.
                _then.value = p.__then;
                // Replace _resolve so that this Deferred can only be resolved once
                _resolve.value = new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public Promise<TResolve, TProgress> run(TResolve value) {
                        return resolve(value);
                    }
                };
                // Make _progress a noop, to disallow progress for the resolved promise.
                _progress.value = null; // TODO: Replace with noop?

                // Notify handlers
                processQueue(handlers, p);

                // Free progressHandlers array since we'll never issue progress events
                progressHandlers.clear();
                handlers.clear();
                //progressHandlers = handlers = undef;

                return p;

            }
        };

        final Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> _reject = new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
            @Override
            public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                return _resolvePromise.value.run(resolve(rejected(reason)));
            }
        };

        /**
         * The promise for the new deferred
         * @type {com.englishtown.promises.Promise}
         */
        promise = new PromiseImpl(new Thenable<TResolve, TProgress>() {
            @Override
            public Promise<TResolve, TProgress> then(
                    Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                    Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                    Runnable<TProgress, TProgress> onProgress) {
                return _then.value.then(onFulfilled, onRejected, onProgress);
            }
        });

        /**
         * The full Deferred object, with {@link Promise} and {@link Resolver} parts
         * @class Deferred
         * @name Deferred
         */
        deferred = new DeferredImpl();
        deferred.resolve = _resolve.value;
        deferred.reject = _reject;
        deferred.progress = _progress.value;
        deferred.promise = promise;
        deferred.resolver.resolve = _resolve.value;
        deferred.resolver.reject = _reject;
        deferred.resolver.progress = _progress.value;

//        {
//                then:then, // DEPRECATED: use deferred.promise.then
//                resolve:promiseResolve,
//                reject:promiseReject,
//                // TODO: Consider renaming progress() to notify()
//                progress:promiseProgress,
//
//                promise:promise,
//
//                resolver:{
//            resolve:
//            promiseResolve,
//                    reject:promiseReject,
//                    progress:promiseProgress
//        }
//        };

        return deferred;
    }

    /**
     * Wrapper to allow _then to be replaced safely
     *
     * @param {function?} [onFulfilled] resolution handler
     * @param {function?} [onRejected] rejection handler
     * @param {function?} [onProgress] progress handler
     * @return {com.englishtown.promises.Promise} new promise
     */

//    function then(onFulfilled, onRejected, onProgress) {
//        // TODO: Promises/A+ check typeof onFulfilled, onRejected, onProgress
//        return _then.run(onFulfilled, onRejected, onProgress);
//    }
//
//    /**
//     * Wrapper to allow _resolve to be replaced
//     */
//    function promiseResolve(val) {
//        return _resolve(val);
//    }
//
//    /**
//     * Wrapper to allow _reject to be replaced
//     */
//    function promiseReject(err) {
//        return _resolve(rejected(err));
//    }
//
//    /**
//     * Wrapper to allow _progress to be replaced
//     */
//    function promiseProgress(update) {
//        return _progress(update);
//    }
//}

//    /**
//     * Determines if promiseOrValue is a promise or not.  Uses the feature
//     * test from http://wiki.commonjs.org/wiki/Promises/A to determine if
//     * promiseOrValue is a promise.
//     *
//     * @param {*} promiseOrValue anything
//     * @returns {boolean} true if promiseOrValue is a {@link com.englishtown.promises.Promise}
//     */
//    function isPromise(promiseOrValue) {
//        return promiseOrValue && typeof promiseOrValue.then == = 'function';
//    }

//    /**
//     * Initiates a competitive race, returning a promise that will resolve when
//     * howMany of the supplied promisesOrValues have resolved, or will reject when
//     * it becomes impossible for howMany to resolve, for example, when
//     * (promisesOrValues.length - howMany) + 1 input promises reject.
//     *
//     * @param {Array}     promisesOrValues array of anything, may contain a mix
//     *                    of promises and values
//     * @param howMany     {number} number of promisesOrValues to resolve
//     * @param {function?} [onFulfilled] resolution handler
//     * @param {function?} [onRejected] rejection handler
//     * @param {function?} [onProgress] progress handler
//     * @returns {com.englishtown.promises.Promise} promise that will resolve to an array of howMany values that
//     * resolved first, or will reject with an array of (promisesOrValues.length - howMany) + 1
//     * rejection reasons.
//     */
//    function some(promisesOrValues, howMany, onFulfilled, onRejected, onProgress) {
//
//        checkCallbacks(2, arguments);
//
//        return when(promisesOrValues, function(promisesOrValues) {
//
//            var toResolve, toReject, values, reasons, deferred, fulfillOne, rejectOne, progress, len, i;
//
//            len = promisesOrValues.length >>> 0;
//
//            toResolve = Math.max(0, Math.min(howMany, len));
//            values =[];
//
//            toReject = (len - toResolve) + 1;
//            reasons =[];
//
//            deferred = defer();
//
//            // No items in the input, resolve immediately
//            if (!toResolve) {
//                deferred.resolve(values);
//
//            } else {
//                progress = deferred.progress;
//
//                rejectOne = function(reason) {
//                    reasons.push(reason);
//                    if (!--toReject) {
//                        fulfillOne = rejectOne = noop;
//                        deferred.reject(reasons);
//                    }
//                }
//                ;
//
//                fulfillOne = function(val) {
//                    // This orders the values based on promise resolution order
//                    // Another strategy would be to use the original position of
//                    // the corresponding promise.
//                    values.push(val);
//
//                    if (!--toResolve) {
//                        fulfillOne = rejectOne = noop;
//                        deferred.resolve(values);
//                    }
//                }
//                ;
//
//                for (i = 0; i < len; ++i) {
//                    if (i in promisesOrValues){
//                        when(promisesOrValues[i], fulfiller, rejecter, progress);
//                    }
//                }
//            }
//
//            return deferred.then(onFulfilled, onRejected, onProgress);
//
//        function rejecter (reason) {
//                rejectOne(reason);
//        }
//
//        function fulfiller (val) {
//                fulfillOne(val);
//        }
//
//        });
//    }
//
//    /**
//     * Initiates a competitive race, returning a promise that will resolve when
//     * any one of the supplied promisesOrValues has resolved or will reject when
//     * *all* promisesOrValues have rejected.
//     *
//     * @param {Array|com.englishtown.promises.Promise} promisesOrValues array of anything, may contain a mix
//     *                        of {@link com.englishtown.promises.Promise}s and values
//     * @param {function?}     [onFulfilled] resolution handler
//     * @param {function?}     [onRejected] rejection handler
//     * @param {function?}     [onProgress] progress handler
//     * @returns {com.englishtown.promises.Promise} promise that will resolve to the value that resolved first, or
//     * will reject with an array of all rejected inputs.
//     */
//    function any(promisesOrValues, onFulfilled, onRejected, onProgress) {
//
//        function unwrapSingleResult (val) {
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
//     *
//     * @param {Array|com.englishtown.promises.Promise} promisesOrValues array of anything, may contain a mix
//     *                        of {@link com.englishtown.promises.Promise}s and values
//     * @param {function?}     [onFulfilled] resolution handler
//     * @param {function?}     [onRejected] rejection handler
//     * @param {function?}     [onProgress] progress handler
//     * @memberOf when
//     * @returns {com.englishtown.promises.Promise}
//     */
//    function all(promisesOrValues, onFulfilled, onRejected, onProgress) {
//        checkCallbacks(1, arguments);
//        return map(promisesOrValues, identity).then(onFulfilled, onRejected, onProgress);
//    }
//
//    /**
//     * Joins multiple promises into a single returned promise.
//     *
//     * @return {com.englishtown.promises.Promise} a promise that will fulfill when *all* the input promises
//     *         have fulfilled, or will reject when *any one* of the input promises rejects.
//     */
//    function join(/* ...promises */) {
//        return map(arguments, identity);
//    }
//
//    /**
//     * Traditional map function, similar to `Array.prototype.map()`, but allows
//     * input to contain {@link com.englishtown.promises.Promise}s and/or values, and mapFunc may return
//     * either a value or a {@link com.englishtown.promises.Promise}
//     *
//     * @param {Array|com.englishtown.promises.Promise} promise array of anything, may contain a mix
//     *                        of {@link com.englishtown.promises.Promise}s and values
//     * @param {function}      mapFunc mapping function mapFunc(value) which may return
//     *                        either a {@link com.englishtown.promises.Promise} or value
//     * @returns {com.englishtown.promises.Promise} a {@link com.englishtown.promises.Promise} that will resolve to an array containing
//     * the mapped output values.
//     */
//    function map(promise, mapFunc) {
//        return when(promise, function(array) {
//            var results, len, toResolve, resolve, i, d;
//
//            // Since we know the resulting length, we can preallocate the results
//            // array to avoid array expansions.
//            toResolve = len = array.length >>> 0;
//            results =[];
//            d = defer();
//
//            if (!toResolve) {
//                d.resolve(results);
//            } else {
//
//                resolve = function resolveOne(item, i) {
//                    when(item, mapFunc).then(function(mapped) {
//                        results[i] = mapped;
//
//                        if (!--toResolve) {
//                            d.resolve(results);
//                        }
//                    },d.reject);
//                } ;
//
//                // Since mapFunc may be async, get all invocations of it into flight
//                for (i = 0; i < len; i++) {
//                    if (i in array){
//                        resolve(array[i], i);
//                    }else{
//                        --toResolve;
//                    }
//                }
//
//            }
//
//            return d.promise;
//
//        });
//    }
//
//    /**
//     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
//     * input may contain promises and/or values, and reduceFunc
//     * may return either a value or a promise, *and* initialValue may
//     * be a promise for the starting value.
//     *
//     * @param {Array|com.englishtown.promises.Promise} promise array or promise for an array of anything,
//     *                        may contain a mix of promises and values.
//     * @param {function}      reduceFunc reduce function reduce(currentValue, nextValue, index, total),
//     *                        where total is the total number of items being reduced, and will be the same
//     *                        in each call to reduceFunc.
//     * @returns {com.englishtown.promises.Promise} that will resolve to the final reduced value
//     */
//    function reduce(promise, reduceFunc /*, initialValue */) {
//        var args = slice.call(arguments, 1);
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
     * Ensure that resolution of promiseOrValue will trigger resolver with the
     * value or reason of promiseOrValue, or instead with resolveValue if it is provided.
     *
     * @param {com.englishtown.promises.Promise} promise
     * @param {Object}   resolver
     * @param {function} resolver.resolve
     * @param {function} resolver.reject
     * @param {*}        [resolveValue]
     * @return {com.englishtown.promises.Promise}
     */
    public Promise<TResolve, TProgress> chain(Promise<TResolve, TProgress> promise, final Resolver resolver, final TResolve resolveValue) {

        return when(
                promise,
                new Runnable<Promise<TResolve, TProgress>, TResolve>() {
                    @Override
                    public Promise<TResolve, TProgress> run(TResolve val) {
                        val = resolveValue != null ? resolveValue : val;
                        resolver.resolve.run(val);
                        return resolve(val);
                    }
                },
                new Runnable<Promise<TResolve, TProgress>, Reason<TResolve>>() {
                    @Override
                    public Promise<TResolve, TProgress> run(Reason<TResolve> reason) {
                        resolver.reject.run(reason);
                        return rejected(reason);
                    }
                },
                resolver.progress
        );

    }

    //
    // Utility functions
    //

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
}

//    /**
//     * Helper that checks arrayOfCallbacks to ensure that each element is either
//     * a function, or null or undefined.
//     *
//     * @param {number} start index at which to start checking items in arrayOfCallbacks
//     * @param {Array}  arrayOfCallbacks array to check
//     * @throws {Error} if any element of arrayOfCallbacks is something other than
//     *                 a functions, null, or undefined.
//     * @private
//     */
//    function checkCallbacks(start, arrayOfCallbacks) {
//        // TODO: Promises/A+ update type checking and docs
//        var arg, i = arrayOfCallbacks.length;
//
//        while (i > start) {
//            arg = arrayOfCallbacks[--i];
//
//            if (arg != null && typeof arg != 'function'){
//                throw new Error('arg ' + i + ' must be a function');
//            }
//        }
//    }

//    /**
//     * No-Op function used in method replacement
//     *
//     * @private
//     */
//    function noop() {
//    }

//slice=[].slice;
//
//// ES5 reduce implementation if native not available
//// See: http://es5.github.com/#x15.4.4.21 as there are many
//// specifics and edge cases.
//reduceArray=[].reduce||
//        function(reduceFunc /*, initialValue */){
//        /*jshint maxcomplexity: 7*/
//
//        // ES5 dictates that reduce.length === 1
//
//        // This implementation deviates from ES5 spec in the following ways:
//        // 1. It does not check if reduceFunc is a Callable
//
//        var arr,args,reduced,len,i;
//
//i=0;
//// This generates a jshint warning, despite being valid
//// "Missing 'new' prefix when invoking a constructor."
//// See https://github.com/jshint/jshint/issues/392
//arr=Object(this);
//len=arr.length>>>0;
//args=arguments;
//
//// If no initialValue, use first item of array (we know length !== 0 here)
//// and adjust i to start at second item
//if(args.length<=1){
//        // Skip to the first real element in the array
//        for(;;){
//        if(i in arr){
//        reduced=arr[i++];
//break;
//}
//
//        // If we reached the end of the array without finding any real
//        // elements, it's a TypeError
//        if(++i>=len){
//        throw new TypeError();
//}
//        }
//        }else{
//        // If initialValue provided, use it
//        reduced=args[1];
//}
//
//        // Do the actual reduce
//        for(;i<len;++i){
//        // Skip holes
//        if(i in arr){
//        reduced=reduceFunc(reduced,arr[i],i,arr);
//}
//        }
//
//        return reduced;
//};
//
//function identity(x){
//        return x;
//}
//
//        return when;
//});
//})(typeof define=='function'&&define.amd
//        ?define
//        :function(factory){typeof exports==='object'
//        ?(module.exports=factory())
//        :(this.when=factory());
//}
//        // Boilerplate for AMD, Node, and browser global
//        );
//
//}
