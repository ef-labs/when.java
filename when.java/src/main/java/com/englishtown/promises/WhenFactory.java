package com.englishtown.promises;

import com.englishtown.promises.impl.*;
import com.englishtown.promises.internal.ArrayHelper;
import com.englishtown.promises.internal.PromiseHelper;

import javax.inject.Provider;
import java.util.concurrent.Executor;

/**
 * Factory for to use when not using dependency injection
 */
public class WhenFactory {

    public static When createAsync() {
        return createFor(AsyncExecutor::new);
    }

    public static When createSync() {
        return createFor(SyncExecutor::new);
    }

    public static When createFor(Provider<Executor> provider) {
        Scheduler scheduler = new DefaultScheduler(provider);
        Environment environment = new DefaultEnvironment(scheduler);
        PromiseHelper helper = new PromiseHelper(environment, new NOPContext(), new NOPReporter());
        return new DefaultWhen(helper, new ArrayHelper(helper));
    }

}
