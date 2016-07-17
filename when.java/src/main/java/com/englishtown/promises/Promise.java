package com.englishtown.promises;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Promises/A+ implementation
 */
public interface Promise<T> extends Thenable<T> {

    @Override
    default <U> Promise<U> then(Function<T, ? extends Thenable<U>> onFulfilled) {
        return then(onFulfilled, null);
    }

    @Override
    <U> Promise<U> then(Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected);

    State<T> inspect();

    /**
     * Handle the ultimate fulfillment value or rejection reason, and assume
     * responsibility for all errors.  If an error propagates out of result
     * or handleFatalError, it will be rethrown to the host, resulting in a
     * loud stack track on most platforms and a crash on some.
     *
     * @param onResult function called when fulfilled
     * @param <U>      onResult thenable return type
     */
    default <U> void done(Function<T, ? extends Thenable<U>> onResult) {
        done(onResult, null);
    }

    /**
     * Handle the ultimate fulfillment value or rejection reason, and assume
     * responsibility for all errors.  If an error propagates out of result
     * or handleFatalError, it will be rethrown to the host, resulting in a
     * loud stack track on most platforms and a crash on some.
     *
     * @param onResult function called when fulfilled
     * @param onError  function called when rejected
     * @param <U>      onResult thenable return type
     */
    <U> void done(Function<T, ? extends Thenable<U>> onResult, Function<Throwable, ? extends Thenable<U>> onError);

    /**
     * Add Error-type and predicate matching to catch.  Examples:
     * promise.catch(TypeError, handleTypeError)
     * .catch(predicate, handleMatchedErrors)
     * .catch(handleRemainingErrors)
     *
     * @param onRejected function called when rejected
     * @param <U>        returned promise type
     * @return a promise for a value
     */
    <U> Promise<U> otherwise(Function<Throwable, ? extends Thenable<U>> onRejected);

    /**
     * Add Error-type and predicate matching to catch.  Examples:
     * promise.catch(TypeError, handleTypeError)
     * .catch(predicate, handleMatchedErrors)
     * .catch(handleRemainingErrors)
     *
     * @param predicate  predicate to determine if throwable should be handled
     * @param onRejected function called when rejected
     * @param <U>        returned promise type
     * @return a promise for a value
     */
    <U> Promise<U> otherwise(Predicate<Throwable> predicate, Function<Throwable, ? extends Thenable<U>> onRejected);

    /**
     * Add Error-type and predicate matching to catch.  Examples:
     * promise.catch(TypeError, handleTypeError)
     * .catch(predicate, handleMatchedErrors)
     * .catch(handleRemainingErrors)
     *
     * @param type       type of throwable that should be handled
     * @param onRejected function called when rejected
     * @param <U>        returned promise type
     * @return a promise for a value
     */
    <U> Promise<U> otherwise(Class<? extends Throwable> type, Function<Throwable, ? extends Thenable<U>> onRejected);

    /**
     * Ensures that onFulfilledOrRejected will be called regardless of whether
     * this promise is fulfilled or rejected.  onFulfilledOrRejected WILL NOT
     * receive the promises' value or reason.  Any returned value will be disregarded.
     * onFulfilledOrRejected may throw or return a rejected promise to signal
     * an additional error.
     *
     * @param handler handler to be called regardless of
     *                fulfillment or rejection
     * @return a promise for a value
     */
    Promise<T> ensure(Runnable handler);

    /**
     * Recover from a failure by returning a defaultValue.  If defaultValue
     * is a promise, it's fulfillment value will be used.  If defaultValue is
     * a promise that rejects, the returned promise will reject with the
     * same reason.
     *
     * @param defaultValue a promise for a default value
     * @param <U>          returned promise type
     * @return new promise
     */
    <U> Promise<U> orElse(Thenable<U> defaultValue);

    /**
     * Shortcut for .then(function() { return value; })
     *
     * @param value promise for a value
     * @param <U>   returned promise type
     * @return a promise that:
     * - is fulfilled if value is not a promise, or
     * - if value is a promise, will fulfill with its value, or reject
     * with its reason.
     */
    <U> Promise<U> yield(Thenable<U> value);

    /**
     * Runs a side effect when this promise fulfills, without changing the
     * fulfillment value.
     *
     * @param onFulfilledSideEffect a function that is run when this promise fulfills
     * @return {Promise}
     */
    Promise<T> tap(Function<T, Thenable<T>> onFulfilledSideEffect);

    <U, V> Promise<V> fold(BiFunction<U, T, ? extends Thenable<V>> fn, Thenable<U> arg);

}
