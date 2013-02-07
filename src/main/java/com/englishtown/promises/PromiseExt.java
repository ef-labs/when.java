package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/7/13
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
public interface PromiseExt<TResolve, TProgress> extends Promise<TResolve, TProgress> {

    Promise<TResolve, TProgress> yield(TResolve value);

    Promise<TResolve, TProgress> yield(Promise<TResolve, TProgress> promise);

    Promise<TResolve, TProgress> always(
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected);

    Promise<TResolve, TProgress> always(
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress);

    Promise<TResolve, TProgress> otherwise(Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected);

}
