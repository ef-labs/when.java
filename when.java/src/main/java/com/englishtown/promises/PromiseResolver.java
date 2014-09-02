package com.englishtown.promises;

import java.util.function.Consumer;

/**
 * Created by adriangonzalez on 8/13/14.
 */
public interface PromiseResolver<T> {

    void resolve(Consumer<T> resolve, Consumer<Throwable> reject);

}
