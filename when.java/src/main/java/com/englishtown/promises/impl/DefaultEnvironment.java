package com.englishtown.promises.impl;

import com.englishtown.promises.Environment;

import javax.inject.Inject;

/**
 * Default implementation of {@link com.englishtown.promises.Environment}
 */
public class DefaultEnvironment implements Environment {

    private final com.englishtown.promises.Scheduler scheduler;

    @Inject
    public DefaultEnvironment(com.englishtown.promises.Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public com.englishtown.promises.Scheduler getScheduler() {
        return scheduler;
    }

}
