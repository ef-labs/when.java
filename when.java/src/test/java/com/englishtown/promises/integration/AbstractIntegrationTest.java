package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import com.englishtown.promises.impl.*;
import com.englishtown.promises.internal.ArrayHelper;
import com.englishtown.promises.internal.PromiseHelper;
import com.englishtown.promises.internal.TrustedPromise;
import org.junit.Before;

import java.util.concurrent.Executor;

/**
 * Abstract base class for integration tests
 */
public abstract class AbstractIntegrationTest {

    protected Executor executor = new SyncExecutor();

    protected When when;
    protected PromiseHelper helper;

    protected Context context = new NOPContext();
    protected Reporter reporter = new NOPReporter();

    @Before
    public void setUp() throws Exception {

        Scheduler scheduler = new DefaultScheduler(() -> executor);
        Environment environment = new DefaultEnvironment(scheduler);

        helper = new PromiseHelper(environment, context, reporter);
        ArrayHelper arrayHelper = new ArrayHelper(helper);
        when = new DefaultWhen(helper, arrayHelper);
    }

    @SuppressWarnings("unchecked")
    protected <T> TrustedPromise<T> resolved(T x) {
        return (TrustedPromise<T>) when.resolve(x);
    }

    @SuppressWarnings("unchecked")
    protected <T> TrustedPromise<T> resolved(Thenable<T> x) {
        return (TrustedPromise<T>) when.resolve(x);
    }

    @SuppressWarnings("unchecked")
    protected <T> TrustedPromise<T> rejected(Throwable t) {
        return (TrustedPromise<T>) when.reject(t);
    }

    public static class Sentinel {

        public Sentinel() {
        }

    }

}
