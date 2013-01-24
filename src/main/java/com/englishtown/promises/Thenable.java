package com.englishtown.promises;

import java.lang.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Thenable<TResolve, TProgress> {

    public Promise<TResolve, TProgress> then(
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
            Runnable<TProgress, TProgress> onProgress);
}
