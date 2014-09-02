package com.englishtown.promises.internal;

import com.englishtown.promises.Thenable;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A promise continuation
 */
public class Continuation<T, U> {
    // TODO: Change to fluid setters

    public Function<T, ? extends Thenable<U>> fulfilled;

    public Function<Throwable, ? extends Thenable<U>> rejected;

    public Consumer<Thenable<U>> resolve;

    public Object context;

}
