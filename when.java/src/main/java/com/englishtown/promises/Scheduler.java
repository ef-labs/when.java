package com.englishtown.promises;

/**
 * Created by adriangonzalez on 8/18/14.
 */
public interface Scheduler {

    void enqueue(Runnable task);

    void afterQueue(Runnable task);

}
