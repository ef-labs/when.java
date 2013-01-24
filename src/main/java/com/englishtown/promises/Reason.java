package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/23/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class Reason<T> {

    public Reason(T data, RuntimeException error) {
        this.data = data;
        this.error = error;
    }

    public T data;
    public RuntimeException error;

}
