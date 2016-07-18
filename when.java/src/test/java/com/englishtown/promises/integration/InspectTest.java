package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import com.englishtown.promises.internal.TrustedPromise;
import org.junit.Test;

import java.util.function.Function;

import static com.englishtown.promises.HandlerState.*;
import static org.junit.Assert.assertEquals;

/**
 * Integration tests for Promise.inspect()
 */
public class InspectTest extends AbstractIntegrationTest {

    private Fail<Sentinel> fail = new Fail<>();
    private Done<Sentinel> done = new Done<>();
    private Sentinel sentinel = new Sentinel();

    private void assertPending(State<?> s) {
        assertEquals(PENDING, s.getState());
    }

    private <T> void assertFulfilled(State<T> s, T value) {
        assertEquals(FULFILLED, s.getState());
        assertEquals(value, s.getValue());
    }

    private void assertRejected(State<?> s, Throwable reason) {
        assertEquals(REJECTED, s.getState());
        assertEquals(reason, s.getReason());
    }

    @Test
    public void testInspect_when_inspecting_promises_should_return_pending_state_for_pending_promise() throws Exception {

        PromiseResolver<Object> resolver = (resolve, reject) -> {
        };

        TrustedPromise<Object> promise = new TrustedPromise<>(resolver, helper);
        assertPending(promise.inspect());

    }

    @Test
    public void testInspect_when_inspecting_promises_should_immediately_return_fulfilled_state_for_fulfilled_promise() throws Exception {

        assertFulfilled(resolved(sentinel).inspect(), sentinel);

    }

    @Test
    public void testInspect_when_inspecting_promises_should_return_fulfilled_state_for_fulfilled_promise() throws Exception {

        TrustedPromise<Sentinel> promise = resolved(sentinel);

        promise.<Sentinel>then(
                val -> {
                    assertFulfilled(promise.inspect(), sentinel);
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testInspect_when_inspecting_promises_should_immediately_return_rejected_state_for_rejected_promise() throws Exception {
        Throwable t = new RuntimeException();
        assertRejected(rejected(t).inspect(), t);
    }

    @Test
    public void testInspect_when_inspecting_promises_should_return_rejected_state_for_rejected_promise() throws Exception {

        Throwable t = new RuntimeException();
        TrustedPromise<Sentinel> promise = rejected(t);

        promise.then(
                fail.onFulfilled,
                val -> {
                    assertRejected(promise.inspect(), t);
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testInspect_when_inspecting_thenables_should_return_pending_state_for_pending_thenable() throws Exception {

        TrustedPromise<Sentinel> p = resolved(new Thenable<Sentinel>() {
            @Override
            public <U> Thenable<U> then(Function<Sentinel, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                return null;
            }
        });

        assertPending(p.inspect());
    }

    @Test
    public void testInspect_when_inspecting_thenables_should_return_fulfilled_state_for_fulfilled_thenable() throws Exception {

        TrustedPromise<Sentinel> p = resolved(new Thenable<Sentinel>() {
            @Override
            public <U> Thenable<U> then(Function<Sentinel, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                onFulfilled.apply(sentinel);
                return null;
            }
        });

        p.<Sentinel>then(x -> {
            assertFulfilled(p.inspect(), sentinel);
            return null;
        }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testInspect_when_inspecting_thenables_should_return_rejected_state_for_rejected_thenable() throws Exception {

        Throwable t = new RuntimeException();

        TrustedPromise<Sentinel> p = resolved(new Thenable<Sentinel>() {
            @Override
            public <U> Thenable<U> then(Function<Sentinel, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                onRejected.apply(t);
                return null;
            }
        });

        p.then(fail.onFulfilled, x -> {
            assertRejected(p.inspect(), t);
            return null;
        }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
