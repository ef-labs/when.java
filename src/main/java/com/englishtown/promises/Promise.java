package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Promise<T> {

    Promise<T> then(
            Runnable<Promise<T>, T> onFulfilled,
            Runnable<Promise<T>, Reason<T>> onRejected,
            Runnable<Void, Promise<T>> onProgress);

}
