package com.englishtown.promises.hk2;

import com.englishtown.promises.*;
import com.englishtown.promises.impl.*;
import com.englishtown.promises.internal.ArrayHelper;
import com.englishtown.promises.internal.PromiseHelper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import javax.inject.Singleton;
import java.util.concurrent.Executor;

/**
 * HK2 when.java binder
 */
public class WhenBinder extends AbstractBinder {
    /**
     * Implement to provide binding definitions using the exposed binding
     * methods.
     */
    @Override
    protected void configure() {

        bind(AsyncExecutor.class).to(Executor.class).in(Singleton.class);
        bind(DefaultScheduler.class).to(Scheduler.class).in(Singleton.class);
        bind(DefaultEnvironment.class).to(Environment.class).in(Singleton.class);
        bind(NOPContext.class).to(Context.class).in(Singleton.class);
        bind(NOPReporter.class).to(Reporter.class).in(Singleton.class);
        bind(PromiseHelper.class).to(PromiseHelper.class).in(Singleton.class);
        bind(ArrayHelper.class).to(ArrayHelper.class).in(Singleton.class);
        bind(When.class).to(When.class);

    }
}
