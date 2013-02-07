package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Value<T> {

    public Value(T value) {
        this.value = value;
    }

    public Value(T value, RuntimeException error) {
        this.value = value;
        this.error = error;
    }

    public T value;
    public RuntimeException error;

}
