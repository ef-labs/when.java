package com.englishtown.promises;

import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test done helper
 */
public class BiDone<T, U> {

    private boolean fulfilled;
    private T value;
    private boolean rejected;
    private Throwable cause;

    public final Function<T, Promise<U>> onFulfilled = x -> {
        fulfill(x);
        return null;
    };

    public final Function<Throwable, Promise<U>> onRejected = x -> {
        reject(x);
        return null;
    };

    public void assertFulfilled() {
        if (cause != null) {
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
        assertTrue(fulfilled);
        assertFalse(rejected);
    }

    public void assertRejected() {
        assertTrue(rejected);
        assertFalse(fulfilled);
    }

    public boolean fulfilled() {
        return fulfilled;
    }

    public boolean rejected() {
        return rejected;
    }

    public void fulfill(T x) {
        value = x;
        fulfilled = true;
    }

    public void fulfill() {
        fulfill(null);
    }

    public void reject(Throwable x) {
        cause = x;
        rejected = true;
    }

    public T getValue() {
        return value;
    }

    public Throwable getCause() {
        return cause;
    }

    public boolean getFulfilled() {
        return fulfilled;
    }

    public boolean getRejected() {
        return fulfilled;
    }

}
