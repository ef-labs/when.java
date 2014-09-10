package com.englishtown.promises;

/**
 * Scheduler to run tasks
 */
public interface Scheduler {

    void enqueue(Runnable task);

    void afterQueue(Runnable task);

}
