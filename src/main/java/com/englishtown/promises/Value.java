package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
class Value<T> {

    public Value() {
    }

    public Value(T value) {
        this.value = value;
    }

    public T value;
}
