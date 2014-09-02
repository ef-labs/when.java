package com.englishtown.promises;

import com.englishtown.promises.internal.ArrayHelper;
import com.englishtown.promises.internal.PromiseHelper;
import com.englishtown.promises.internal.TrustedPromise;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Promises/A+ and when() implementation
 * when is part of the cujoJS family of libraries (http://cujojs.com/)
 *
 * @author Brian Cavalier
 * @author John Hann
 * @version 3.2.3
 */
public class When {

    private final PromiseHelper helper;
    private final ArrayHelper arrayHelper;

    @Inject
    public When(PromiseHelper helper, ArrayHelper arrayHelper) {
        this.helper = helper;
        this.arrayHelper = arrayHelper;
    }

//            // Public API
//
//            when.lift        = lift;                 // lift a function to return promises
//            when['try']      = attempt;              // call a function and return a promise
//            when.attempt     = attempt;              // alias for when.try
//
//            when.iterate     = Promise.iterate;      // Generate a stream of promises
//            when.unfold      = Promise.unfold;       // Generate a stream of promises
//
//

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public <T, U> Promise<U> when(T x) {
        return when(x, null, null);
    }

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public <T, U> Promise<U> when(Thenable<T> x) {
        return this.<T, U>when(x, null, null);
    }

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public <T, U> Promise<U> when(T x, Function<T, ? extends Thenable<U>> onFulfilled) {
        return when(x, onFulfilled, null);
    }

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @param onRejected  callback to be called when x is
     *                    rejected.
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public <T, U> Promise<U> when(T x, Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
        Promise<T> p = resolve(x);
        return p.then(onFulfilled, onRejected);
    }

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public <T, U> Promise<U> when(Thenable<T> x, Function<T, ? extends Thenable<U>> onFulfilled) {
        return when(x, onFulfilled, null);
    }

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @param onRejected  callback to be called when x is
     *                    rejected.
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    public <T, U> Promise<U> when(Thenable<T> x, Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
        Promise<T> p = resolve(x);
        return p.then(onFulfilled, onRejected);
    }

    /**
     * Create a resolved promise
     *
     * @param x
     * @param <T>
     * @return
     */
    public <T> Promise<T> resolve(T x) {
        return helper.resolve(x);
    }

    /**
     * Create a resolved promise
     *
     * @param x
     * @param <T>
     * @return
     */
    public <T> Promise<T> resolve(Thenable<T> x) {
        return helper.resolve(x);
    }

    /**
     * Create a rejected promise
     *
     * @param x
     * @param <T>
     * @return
     */
    public <T> Promise<T> reject(Throwable x) {
        return helper.reject(x);
    }

    /**
     * Creates a new promise whose fate is determined by resolver.
     *
     * @param resolver function(resolve, reject, notify)
     * @return {Promise} promise whose fate is determine by resolver
     */
    public <T> Promise<T> promise(PromiseResolver<T> resolver) {
        return new TrustedPromise<>(resolver, helper);
    }

//    /**
//     * Lift the supplied function, creating a version of f that returns
//     * promises, and accepts promises as arguments.
//     *
//     * @param {function} f
//     * @return {Function} version of f that returns promises
//     */
//    public <T> Promise<T> lift(Object f) {
//        return function() {
//            return _apply(f, this, slice.call(arguments));
//        }
//    }

//    /**
//     * Call f in a future turn, with the supplied args, and return a promise
//     * for the result.
//     *
//     * @param {function} f
//     * @return {Promise}
//     */
//    function attempt(f /*, args... */) {
//        /*jshint validthis:true */
//        return _apply(f, this, slice.call(arguments, 1));
//    }

    /**
     * try/lift helper that allows specifying thisArg
     */
    private <T, U> Promise<U> _apply(Function<List<T>, Thenable<U>> f, List<Thenable<T>> args) {
        return helper.all(args).then(f::apply);
//        return Promise.all(args).then(function(args) {
//            return f.apply(thisArg, args);
//        });
    }


    /**
     * Creates a {promise, resolver} pair, either or both of which
     * may be given out safely to consumers.
     *
     * @return {{promise: Promise, resolve: function, reject: function, notify: function}}
     */
    public <T> Deferred<T> defer() {
        return new DeferredImpl<>();
    }

    private class DeferredImpl<T> implements Deferred<T> {

        private final TrustedPromise<T> promise;

        public DeferredImpl() {
            promise = helper.defer();
        }

        @Override
        public Resolver<T> getResolver() {
            return this;
        }

        @Override
        public Promise<T> getPromise() {
            return promise;
        }

        @Override
        public void resolve(T x) {
            promise._handler.resolve(x);
        }

        @Override
        public void resolve(Thenable<T> x) {
            promise._handler.resolve(x);
        }

        @Override
        public void reject(Throwable x) {
            promise._handler.reject(x);
        }
    }

//
//    /**
//     * Determines if x is promise-like, i.e. a thenable object
//     * NOTE: Will return true for *any thenable object*, and isn't truly
//     * safe, since it may attempt to access the `then` property of x (i.e.
//     * clever/malicious getters may do weird things)
//     *
//     * @param {*} x anything
//     * @return {boolean} true if x is promise-like
//     */
//    function isPromiseLike(x) {
//        return x && typeof x.then == = 'function';
//    }

    /**
     * Return a promise that will resolve only once all the supplied arguments
     * have resolved. The resolution value of the returned promise will be an array
     * containing the resolution values of each of the arguments.
     *
     * @param {...*} arguments may be a mix of promises and values
     * @return {Promise}
     */
    public <T> Promise<List<T>> join(Promise<T>... promises) {
        return helper.all(Arrays.asList(promises));
    }

    /**
     * Return a promise that will fulfill once all input promises have
     * fulfilled, or reject when any one input promise rejects.
     *
     * @param {array|Promise} promises array (or promise for an array) of promises
     * @return {Promise}
     */
    public <T> Promise<List<T>> all(List<? extends Thenable<T>> promises) {
        return when(promises, helper::all);
    }

    /**
     * Return a promise that will always fulfill with an array containing
     * the outcome states of all input promises.  The returned promise
     * will only reject if `promises` itself is a rejected promise.
     *
     * @param promises array (or promise for an array) of promises
     * @return {Promise}
     */
    public <T> Promise<List<State<T>>> settle(List<? extends Thenable<T>> promises) {
        return when(promises, arrayHelper::settle);
    }

    /**
     * One-winner race
     *
     * @param promises
     * @param <T>
     * @return
     */
    public <T> Promise<T> any(List<? extends Thenable<T>> promises) {
        return when(promises, arrayHelper::any);
    }

    /**
     * Multi-winner race
     *
     * @param promises
     * @param n
     * @param <T>
     * @return
     */
    public <T> Promise<List<T>> some(List<? extends Thenable<T>> promises, int n) {
        return arrayHelper.some(promises, n);
    }

    /**
     * Promise-aware array map function, similar to `Array.prototype.map()`,
     * but input array may contain promises or values.
     *
     * @param promises array of promises
     * @param mapFunc  map function which may return a promise or value
     * @return {Promise} promise that will fulfill with an array of mapped values
     * or reject if any input promise rejects.
     */
    public <T> Promise<List<T>> map(List<? extends Thenable<T>> promises, Function<T, ? extends Thenable<T>> mapFunc) {
        return when(promises, (promises1) -> arrayHelper.map(promises1, mapFunc, null));
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may`
     * be a promise for the starting value.
     *
     * @param promises array or promise for an array of anything,
     *                 may contain a mix of promises and values.
     * @param f        reduce function reduce(currentValue, nextValue, index)
     * @return {Promise} that will resolve to the final reduced value
     */
    public <T> Promise<T> reduce(List<? extends Thenable<T>> promises, BiFunction<T, T, ? extends Thenable<T>> f) {
        return when(promises, (promises1) -> arrayHelper.reduce(promises1, f));
    }

    /**
     * Traditional reduce function, similar to `Array.prototype.reduce()`, but
     * input may contain promises and/or values, and reduceFunc
     * may return either a value or a promise, *and* initialValue may`
     * be a promise for the starting value.
     *
     * @param promises array or promise for an array of anything,
     *                 may contain a mix of promises and values.
     * @param f        reduce function reduce(currentValue, nextValue, index)
     * @return {Promise} that will resolve to the final reduced value
     */
    public <T, U> Promise<U> reduce(List<? extends Thenable<T>> promises, BiFunction<U, T, ? extends Thenable<U>> f, Thenable<U> initialValue) {
        return when(promises, (promises1) -> arrayHelper.reduce(promises1, f, initialValue));
    }

//    /**
//     * Traditional reduce function, similar to `Array.prototype.reduceRight()`, but
//     * input may contain promises and/or values, and reduceFunc
//     * may return either a value or a promise, *and* initialValue may
//     * be a promise for the starting value.
//     *
//     * @param {Array|Promise} promises array or promise for an array of anything,
//     *                        may contain a mix of promises and values.
//     * @param {function}      f reduce function reduce(currentValue, nextValue, index)
//     * @return {Promise} that will resolve to the final reduced value
//     */
//    function reduceRight(promises, f /*, initialValue */) {
//		/*jshint unused:false*/
//        var args = slice.call(arguments, 1);
//        return when(promises, function(array) {
//            args.unshift(array);
//            return Promise.reduceRight.apply(Promise, args);
//        });
//    }
//
//    return when;

    /**
     * Run array of tasks in sequence with no overlap
     *
     * @param tasks {Array|Promise} array or promiseForArray of task functions
     * @param arg   arguments to be passed to all tasks
     * @return {Promise} promise for an array containing
     * the result of each task in the array position corresponding
     * to position of the task in the tasks array
     */
    public <T, U> Promise<List<U>> sequence(List<Function<T, Thenable<U>>> tasks, Thenable<T> arg) {
        List<U> results = new ArrayList<>();

        // Create resolved promises for tasks so we can use reduce()
        List<Thenable<Function<T, Thenable<U>>>> promises = new ArrayList<>(tasks.size());
        tasks.stream().forEach((task) -> promises.add(resolve(task)));

        Function<U, Thenable<List<U>>> addResult = (result) -> {
            results.add(result);
            return resolve(results);
        };

        return resolve(arg).then(argResult -> {
            return this.<Function<T, Thenable<U>>, List<U>>reduce(promises, (results1, task) -> {
                return when(task.apply(argResult), addResult);
            }, resolve(results));
        });

        /*
        var results = [];

		return all(slice.call(arguments, 1)).then(function(args) {
			return when.reduce(tasks, function(results, task) {
				return when(task.apply(void 0, args), addResult);
			}, results);
		});

		function addResult(result) {
			results.push(result);
			return results;
		}
         */
    }

    public <T> Promise<T> race(List<? extends Thenable<T>> promises) {
        return helper.race(promises);
    }

}
