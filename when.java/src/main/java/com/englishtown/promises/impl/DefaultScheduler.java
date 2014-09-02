package com.englishtown.promises.impl;

import com.englishtown.promises.Scheduler;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

/**
 * Default implementation of {@link com.englishtown.promises.Scheduler}
 */
public class DefaultScheduler implements Scheduler {

    private final Executor _enqueue;
    private final Queue<Runnable> _handlerQueue;
    private final Queue<Runnable> _afterQueue;
    private boolean _running;

    @Inject
    public DefaultScheduler(Provider<Executor> enqueue) {
        this._enqueue = enqueue.get();
        this._handlerQueue = new ConcurrentLinkedQueue<>();
        this._afterQueue = new ConcurrentLinkedQueue<>();
        this._running = false;
    }

    /**
     * Enqueue a task. If the queue is not currently scheduled to be
     * drained, schedule it.
     *
     * @param {function} task
     */
    @Override
    public void enqueue(Runnable task) {
        this._handlerQueue.add(task);
        if (!this._running) { // TODO: sync
            this._running = true;
            this._enqueue.execute(this::drain);
        }
    }

    @Override
    public void afterQueue(Runnable task) {
        this._afterQueue.add(task);
        if (!this._running) {
            this._running = true;
            this._enqueue.execute(this::drain);
        }
    }

    /**
     * Drain the handler queue entirely, being careful to allow the
     * queue to be extended while it is being processed, and to continue
     * processing until it is truly empty.
     */
    public void drain() {
        Queue<Runnable> q = this._handlerQueue;
        Runnable task = q.poll();

        while (task != null) {
            task.run();
            task = q.poll();
        }

        this._running = false;

        q = this._afterQueue;
        task = q.poll();
        while (task != null) {
            task.run();
            task = q.poll();
        }
    }

}
