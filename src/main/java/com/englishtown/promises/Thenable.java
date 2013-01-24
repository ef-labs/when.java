package com.englishtown.promises;

import java.lang.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Thenable<T> {

    public Promise<T> then(
            Runnable<Promise<T>, T> onFulfilled,
            Runnable<Promise<T>, Reason<T>> onRejected,
            Runnable<Void, Promise<T>> onProgress);
}
