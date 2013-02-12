package com.englishtown.promises;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/5/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Reducer<T> {

    T run(T previousValue, T currentValue, int currentIndex, int total);

}
