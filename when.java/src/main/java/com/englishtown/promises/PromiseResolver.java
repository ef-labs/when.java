package com.englishtown.promises;

import java.util.function.Consumer;

/**
 * Resolver for promises
 */
public interface PromiseResolver<T> {

    void resolve(Consumer<T> resolve, Consumer<Throwable> reject);

}
