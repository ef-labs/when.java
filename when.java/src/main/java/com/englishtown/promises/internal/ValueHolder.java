package com.englishtown.promises.internal;

/**
 * Created by adriangonzalez on 8/19/14.
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
