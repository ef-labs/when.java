package com.englishtown.promises.impl;

import com.englishtown.promises.*;
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
 * Default implementation of {@link com.englishtown.promises.When}
 */
public class DefaultWhen implements When {

    private final PromiseHelper helper;
    private final ArrayHelper arrayHelper;

    @Inject
    public DefaultWhen(PromiseHelper helper, ArrayHelper arrayHelper) {
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

    @Override
    public <T> Promise<T> when(T x) {
        return when(x, null, null);
    }

    @Override
    public <T> Promise<T> when(Thenable<T> x) {
        return this.<T, T>when(x, null, null);
    }

    @Override
    public <T, U> Promise<U> when(T x, Function<T, ? extends Thenable<U>> onFulfilled) {
        return when(x, onFulfilled, null);
    }

    @Override
    public <T, U> Promise<U> when(T x, Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
        Promise<T> p = resolve(x);
        return p.then(onFulfilled, onRejected);
    }

    @Override
    public <T, U> Promise<U> when(Thenable<T> x, Function<T, ? extends Thenable<U>> onFulfilled) {
        return when(x, onFulfilled, null);
    }

    @Override
    public <T, U> Promise<U> when(Thenable<T> x, Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
        Promise<T> p = resolve(x);
        return p.then(onFulfilled, onRejected);
    }

    @Override
    public <T> Promise<T> resolve(T x) {
        return helper.resolve(x);
    }

    @Override
    public <T> Promise<T> resolve(Thenable<T> x) {
        return helper.resolve(x);
    }

    @Override
    public <T> Promise<T> reject(Throwable x) {
        return helper.reject(x);
    }

    @Override
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


    @Override
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

    @Override
    public <T> Promise<List<T>> join(Promise<T>... promises) {
        return helper.all(Arrays.asList(promises));
    }

    @Override
    public <T> Promise<List<T>> all(List<? extends Thenable<T>> promises) {
        return when(promises, helper::all);
    }

    @Override
    public <T> Promise<List<State<T>>> settle(List<? extends Thenable<T>> promises) {
        return when(promises, arrayHelper::settle);
    }

    @Override
    public <T> Promise<T> any(List<? extends Thenable<T>> promises) {
        return when(promises, arrayHelper::any);
    }

    @Override
    public <T> Promise<List<T>> some(List<? extends Thenable<T>> promises, int n) {
        return arrayHelper.some(promises, n);
    }

    @Override
    public <T> Promise<List<T>> map(List<? extends Thenable<T>> promises, Function<T, ? extends Thenable<T>> mapFunc) {
        return when(promises, (promises1) -> arrayHelper.map(promises1, mapFunc, null));
    }

    @Override
    public <T> Promise<T> reduce(List<? extends Thenable<T>> promises, BiFunction<T, T, ? extends Thenable<T>> f) {
        return when(promises, (promises1) -> arrayHelper.reduce(promises1, f));
    }

    @Override
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

    @Override
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

    }

    @Override
    public <T> Promise<T> race(List<? extends Thenable<T>> promises) {
        return helper.race(promises);
    }

}
