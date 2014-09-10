package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import org.junit.Test;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests for when.resolve()
 */
public class ResolveTest extends AbstractIntegrationTest {

    private final Done<Integer> done = new Done<>();
    private final Fail<Integer> fail = new Fail<>();
    private final Sentinel sentinel = new Sentinel();

    @Test
    public void testResolve_should_resolve_an_immediate_value() throws Exception {

        int expected = 123;

        when.resolve(expected).then(
                value -> {
                    assertEquals(value.intValue(), expected);
                    return null;
                },
                fail.onRejected
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testResolve_should_resolve_a_resolved_promise() throws Exception {

        int expected = 123;
        Deferred<Integer> d = when.defer();
        d.resolve(expected);

        when.resolve(d.getPromise()).then(
                value -> {
                    assertEquals(value.intValue(), expected);
                    return null;
                },
                fail.onRejected
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testResolve_should_reject_a_rejected_promise() throws Exception {

        Throwable expected = new RuntimeException();
        Deferred<Integer> d = when.defer();
        d.reject(expected);

        when.resolve(d.getPromise()).then(
                fail.onFulfilled,
                value -> {
                    assertEquals(value, expected);
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testResolve_when_assimilating_untrusted_thenables_should_trap_exceptions_during_assimilation() throws Exception {

        RuntimeException t = new RuntimeException();

        when.resolve(new Thenable<Integer>() {
            @Override
            public <U> Thenable<U> then(Function<Integer, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                throw t;
            }
        }).then(
                fail.onFulfilled,
                val -> {
                    assertEquals(t, val);
                    return null;
                }

        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testResolve_when_assimilating_untrusted_thenables_should_ignore_exceptions_after_fulfillment() throws Exception {

        RuntimeException other = new RuntimeException();

        when.resolve(new Thenable<Sentinel>() {
            @Override
            public <U> Promise<U> then(Function<Sentinel, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                onFulfilled.apply(sentinel);
                throw other;
            }
        }).then(
                val -> {
                    assertEquals(sentinel, val);
                    return null;
                },
                fail.onRejected
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testResolve_when_assimilating_untrusted_thenables_should_ignore_exceptions_after_rejection() throws Exception {

        Throwable t = new RuntimeException();
        RuntimeException other = new RuntimeException();

        when.resolve(new Thenable<Integer>() {
            @Override
            public <U> Promise<U> then(Function<Integer, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                onRejected.apply(t);
                throw other;
            }
        }).then(
                fail.onFulfilled,
                val -> {
                    assertEquals(t, val);
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
