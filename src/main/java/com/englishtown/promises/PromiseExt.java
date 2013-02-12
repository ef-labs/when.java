package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/7/13
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PromiseExt<TResolve, TProgress> extends Promise<TResolve, TProgress> {

    /**
     * Register a callback that will be called when a promise is
     * fulfilled or rejected.
     * <p/>
     * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected, onProgress)
     *
     * @param onFulfilledOrRejected a callback for when a promise is fulfilled or rejected
     * @return a new {@link Promise}
     */
    Promise<TResolve, TProgress> always(
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected);

    /**
     * Register a callback that will be called when a promise is
     * fulfilled or rejected.  Also register a progress handler.
     * <p/>
     * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected, onProgress)
     *
     * @param onFulfilledOrRejected a callback for when a promise is fulfilled or rejected
     * @param onProgress a callback for progress notifications
     * @return a new {@link Promise}
     */
    Promise<TResolve, TProgress> always(
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress);

    /**
     * Register a rejection handler.  Shortcut for .then(null, onRejected)
     *
     * @param onRejected rejection handler
     * @return a new {@link Promise}
     */
    Promise<TResolve, TProgress> otherwise(Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected);

    /**
     * Shortcut for .then(function() { return resolve(value); })
     *
     * @param value the value to be returned
     * @return an already-fulfilled {@link Promise}
     */
    Promise<TResolve, TProgress> yield(TResolve value);

    /**
     * Shortcut for .then(function() { return resolve(value); })
     *
     * @param promise the promise to be returned
     * @return an {@link Promise} that fulfill with its value or reject with its reason.
     */
    Promise<TResolve, TProgress> yield(Promise<TResolve, TProgress> promise);

}
