package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import com.englishtown.promises.exceptions.CycleException;
import com.englishtown.promises.internal.TrustedPromise;
import com.englishtown.promises.internal.ValueHolder;
import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@link com.englishtown.promises.internal.handlers.CycleHandler}
 */
public class CycleTest extends AbstractIntegrationTest {

    private final Done<Object> done = new Done<>();
    private final Fail<Object> fail = new Fail<>();

    private void assertCycle(Promise<Object> p) {

        p.then(
                fail.onFulfilled,
                e -> {
                    assertThat(e, instanceOf(CycleException.class));
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testCycle_should_detect_self_cycles_when_resolving() {

        ValueHolder<Consumer<Object>> resolveHolder = new ValueHolder<>(null);
        PromiseResolver<Object> resolver = (resolve, reject) -> resolveHolder.value = resolve;

        Promise<Object> p = new TrustedPromise<>(resolver, helper);
        resolveHolder.value.accept(p);

        assertCycle(p);

    }

    @Test
    public void testCycle_should_detect_self_cycles_when_returning_from_handler() {

        ValueHolder<Promise<Object>> holder = new ValueHolder<>(null);

        Deferred<Object> d = when.defer();
        Promise<Object> p = d.getPromise();

        holder.value = p.then(x -> holder.value);
        d.resolve((Object) null);

        assertCycle(holder.value);

    }

    @Test
    public void testCycle_should_detect_self_cycles_when_returning_resolved_from_handler() {

        ValueHolder<Promise<Object>> holder = new ValueHolder<>(null);

        Deferred<Object> d = when.defer();
        Promise<Object> p = d.getPromise();

        holder.value = p.then(x -> when.resolve(holder.value));
        d.resolve((Object) null);

        assertCycle(holder.value);

    }

    @Test
    public void testCycle_should_detect_long_cycles() {

        ValueHolder<Consumer<Object>> resolveHolder1 = new ValueHolder<>(null);
        PromiseResolver<Object> resolver1 = (resolve, reject) -> resolveHolder1.value = resolve;

        ValueHolder<Consumer<Object>> resolveHolder2 = new ValueHolder<>(null);
        PromiseResolver<Object> resolver2 = (resolve, reject) -> resolveHolder2.value = resolve;

        ValueHolder<Consumer<Object>> resolveHolder3 = new ValueHolder<>(null);
        PromiseResolver<Object> resolver3 = (resolve, reject) -> resolveHolder3.value = resolve;

        Promise<Object> p1 = new TrustedPromise<>(resolver1, helper);
        Promise<Object> p2 = new TrustedPromise<>(resolver2, helper);
        Promise<Object> p3 = new TrustedPromise<>(resolver3, helper);

        resolveHolder1.value.accept(p2);
        resolveHolder2.value.accept(p3);
        resolveHolder3.value.accept(p1);

        assertCycle(p3);

    }

}
