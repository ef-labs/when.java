package com.englishtown.promises.impl;

import com.englishtown.promises.Environment;
import com.englishtown.promises.Reporter;
import com.englishtown.promises.internal.handlers.RejectedHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Base {@link com.englishtown.promises.Reporter} class
 */
public abstract class AbstractReporter implements Reporter {

    private Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();
    private List<RejectedHandler<?>> reported = new ArrayList<>();
    private boolean running = false;
    private final Environment environment;

    protected AbstractReporter(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void onPotentiallyUnhandledRejection(RejectedHandler<?> rejection, Object context) {
        enqueue(() -> report(rejection));
    }

    @Override
    public void onPotentiallyUnhandledRejectionHandled(RejectedHandler<?> rejection) {
        enqueue(() -> unreport(rejection));
    }

    @Override
    public void onFatalRejection(RejectedHandler<?> rejection, Object context) {
        enqueue(() -> throwit(rejection.getValue()));
    }

    protected abstract void logError(RejectedHandler<?> rejectedHandler);

    protected abstract void logInfo(RejectedHandler<?> rejectedHandler);

    private void report(RejectedHandler<?> r) {
        if (!r.handled()) {
            reported.add(r);
            logError(r);
        }
    }

    private void unreport(RejectedHandler<?> r) {
        if (reported.remove(r)) {
            logInfo(r);
        }
    }

    private void enqueue(Runnable f) {
        tasks.add(f);
        if (!running) {
            running = true;
            environment.getScheduler().enqueue(this::flush);
        }
    }

    private void flush() {
        running = false;
        Runnable task = tasks.poll();
        while (task != null) {
            task.run();
            task = tasks.poll();
        }
    }

    private void throwit(Throwable e) {
        RuntimeException re = (e instanceof RuntimeException) ? (RuntimeException) e : new RuntimeException(e);
        throw re;
    }

}
