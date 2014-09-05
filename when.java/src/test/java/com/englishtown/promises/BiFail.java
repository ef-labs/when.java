package com.englishtown.promises;

import java.util.function.Function;

import static org.junit.Assert.fail;

/**
 * Unit test fail helper
 */
public class BiFail<T, U> {

    public final Function<T, Promise<U>> onFulfilled = x -> {
        fail("Promise should not have fulfilled");
        return null;
    };

    public final Function<Throwable, Promise<U>> onRejected = x -> {
        fail("Promise should not have rejected");
        return null;
    };

}
