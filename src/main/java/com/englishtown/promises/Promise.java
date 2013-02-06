package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Promise<TResolve, TProgress> extends Thenable<TResolve, TProgress> {

    Promise<TResolve, TProgress> then(
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled);

    Promise<TResolve, TProgress> then(
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected);

}
