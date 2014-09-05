package com.englishtown.promises.internal;

/**
 * Wrapper for values
 */
public class ValueHolder<T> {

    public T value;

    public ValueHolder(T value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return value == null ? "null" : value.toString();
    }
}
