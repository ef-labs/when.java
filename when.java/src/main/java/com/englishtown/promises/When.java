package com.englishtown.promises;


import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Promises/A+ and when implementation
 * <p>
 * This is a port of the when.js version 3.2.3 library written by Brian Cavalier and John Hann
 * when is part of the cujoJS family of libraries (http://cujojs.com/)
 */
public interface When {

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x   value to be wrapped in a fulfilled trusted promise
     * @param <T>
     * @return a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    <T> Promise<T> when(T x);

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x thenable to be wrapped in a trusted promise
     * @return a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    <T> Promise<T> when(Thenable<T> x);

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x           value to be wrapped in a fulfilled trusted promise
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @return a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    <T, U> Promise<U> when(T x, Function<T, ? extends Thenable<U>> onFulfilled);

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x           thenable to be wrapped in a trusted promise
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @param onRejected  callback to be called when x is
     *                    rejected.
     * @return a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    <T, U> Promise<U> when(T x, Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected);

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x           value to be wrapped in a fulfilled trusted promise
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @return a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    <T, U> Promise<U> when(Thenable<T> x, Function<T, ? extends Thenable<U>> onFulfilled);

    /**
     * Get a trusted promise for x, or by transforming x with onFulfilled
     *
     * @param x           thenable to be wrapped in a trusted promise
     * @param onFulfilled callback to be called when x is
     *                    successfully fulfilled.  If promiseOrValue is an immediate value, callback
     *                    will be invoked immediately.
     * @param onRejected  callback to be called when x is
     *                    rejected.
     * @return {Promise} a new promise that will fulfill with the return
     * value of callback or errback or the completion value of promiseOrValue if
     * callback and/or errback is not supplied.
     */
    <T, U> Promise<U> when(Thenable<T> x, Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected);

    /**
     * Create a resolved promise
     *
     * @param x   value to be wrapped in a fulfilled trusted promise
     * @param <T> type of value and promise to be returned
     * @return a trusted fulfilled promise
     */
    <T> Promise<T> resolve(T x);

    /**
     * Create a resolved promise
     *
     * @param x   thenable to be wrapped in a trusted promise
     * @param <T> type of thenable and promise to be returned
     * @return a trusted promise
     */
    <T> Promise<T> resolve(Thenable<T> x);

    /**
     * Create a rejected promise
     *
     * @param x   a throwable that is the cause of a rejected promise
     * @param <T> the type of rejected promise
     * @return a rejected promise
     */
    <T> Promise<T> reject(Throwable x);

    /**
     * Creates a new promise whose fate is determined by resolver.
     *
     * @param resolver function(resolve, reject, notify)
     * @return promise whose fate is determine by resolver
     */
    <T> Promise<T> promise(PromiseResolver<T> resolver);

    /**
     * Creates a {promise, resolver} pair, either or both of which
     * may be given out safely to consumers.
     *
     * @return {{promise: Promise, resolve: function, reject: function, notify: function}}
     */
    <T> Deferred<T> defer();

    /**
     * Return a promise that will resolve only once all the supplied arguments
     * have resolved. The resolution value of the returned promise will be an array
     * containing the resolution values of each of the arguments.
     *
     * @param promises array of promises to be joined
     * @return {Promise}
     */
    <T> Promise<List<T>> join(Promise<T>... promises);

    /**
     * Return a promise that will fulfill once all input promises have
     * fulfilled, or reject when any one input promise rejects.
     *
     * @param promises list of promises
     * @return promise for list of results
     */
    <T> Promise<List<T>> all(List<? extends Thenable<T>> promises);

    /**
     * Return a promise that will always fulfill with an array containing
     * the outcome states of all input promises.  The returned promise
     * will only reject if `promises` itself is a rejected promise.
     *
     * @param promises list of promises
     * @return {Promise}
     */
    <T> Promise<List<State<T>>> settle(List<? extends Thenable<T>> promises);

    /**
     * One-winner race
     *
     * @param promises list of promises
     * @param <T>      type of promises and returned promise
     * @return a promise for the first winner
     */
    <T> Promise<T> any(List<? extends Thenable<T>> promises);

    /**
     * Multi-winner race
     *
     * @param promises list of promises
     * @param n        the number of promises to resolve
     * @param <T>      type of promises
     * @return a promise of a list of n resolved values
     */
    <T> Promise<List<T>> some(List<? extends Thenable<T>> promises, int n);

    /**
     * Promise-aware array map function, similar to `Array.prototype.map()`,
     * but input array may contain promises or values.
     *
     * @param promises array of promises
     * @param mapFunc  map function which may return a promise or value
     * @return {Promise} promise that will fulfill with an array of mapped values
     * or reject if any input promise rejects.
     */
    <T> Promise<List<T>> map(List<? extends Thenable<T>> promises, Function<T, ? extends Thenable<T>> mapFunc);

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
    <T> Promise<T> reduce(List<? extends Thenable<T>> promises, BiFunction<T, T, ? extends Thenable<T>> f);

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
    <T, U> Promise<U> reduce(List<? extends Thenable<T>> promises, BiFunction<U, T, ? extends Thenable<U>> f, Thenable<U> initialValue);

    /**
     * Run array of tasks in sequence with no overlap
     *
     * @param tasks {Array|Promise} array or promiseForArray of task functions
     * @param arg   arguments to be passed to all tasks
     * @return {Promise} promise for an array containing
     * the result of each task in the array position corresponding
     * to position of the task in the tasks array
     */
    <T, U> Promise<List<U>> sequence(List<Function<T, Thenable<U>>> tasks, Thenable<T> arg);

    <T> Promise<T> race(List<? extends Thenable<T>> promises);
}
