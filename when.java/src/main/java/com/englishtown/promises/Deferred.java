package com.englishtown.promises;

/**
 * A deferred wrapper around a {@link com.englishtown.promises.Promise} and {@link Resolver}
 */
public interface Deferred<T> extends Resolver<T> {

    Resolver<T> getResolver();

    Promise<T> getPromise();

}
