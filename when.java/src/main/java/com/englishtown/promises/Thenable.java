package com.englishtown.promises;

import java.util.function.Function;

/**
 * Promises/A+ implementation
 */
public interface Thenable<T> {

    default <U> Thenable<U> then(Function<T, ? extends Thenable<U>> onFulfilled) {
        return then(onFulfilled, null);
    }

    <U> Thenable<U> then(Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected);

}
