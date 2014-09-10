package com.englishtown.promises;

/**
 * A resolver for a {@link com.englishtown.promises.Deferred}
 */
public interface Resolver<T> {

    void resolve(T x);

    void resolve(Thenable<T> x);

    void reject(Throwable x);

}
