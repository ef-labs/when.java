package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import com.englishtown.promises.impl.NOPReporter;
import com.englishtown.promises.internal.handlers.RejectedHandler;
import org.junit.Test;

import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by adriangonzalez on 8/30/14.
 */
public class PromiseTest extends AbstractIntegrationTest {

    private Sentinel sentinel = new Sentinel();
    private Sentinel other = new Sentinel();
    private RuntimeException sentinelEx = new RuntimeException();
    private RuntimeException otherEx = new RuntimeException();

    private Done<Object> done = new Done<>();
    private Fail<Object> fail = new Fail<>();
    private Fail<Sentinel> fail2 = new Fail<>();
    private TestReporter testReporter;

    private Function<Object, Thenable<Object>> f1 = (x) -> null;
    private Function<Throwable, Thenable<Object>> f2 = (t) -> null;

    private static class TestReporter extends NOPReporter {

        public BiConsumer<RejectedHandler<?>, Object> onFatalRejectionConsumer;

        @Override
        public void onFatalRejection(RejectedHandler<?> rejection, Object context) {
            onFatalRejectionConsumer.accept(rejection, context);
        }
    }

    @Override
    public void setUp() throws Exception {
        reporter = testReporter = new TestReporter();
        super.setUp();
    }

    private void assertPending(State<?> s) {
        assertEquals(s.getState(), HandlerState.PENDING);
    }

    private <T> void assertFulfilled(State<T> s, T value) {
        assertEquals(s.getState(), HandlerState.FULFILLED);
        assertEquals(s.getValue(), value);
    }

    private void assertRejected(State<?> s, Throwable reason) {
        assertEquals(s.getState(), HandlerState.REJECTED);
        assertEquals(s.getReason(), reason);
    }

//    function f() {}

    @Test
    public void testPromise_done_when_fulfilled_should_invoke_handleValue() {

        when.resolve(sentinel).done((x) -> {
            assertEquals(sentinel, x);
            done.fulfill(x);
            return null;
        });

        done.assertFulfilled();
    }

    @Test
    public void testPromise_done_when_fulfilled_should_be_fatal_when_handleValue_throws() {

        Promise<Object> p = when.resolve(null);

        testReporter.onFatalRejectionConsumer = (h, e) -> {
            assertEquals(h.getValue(), sentinelEx);
            done.fulfill(h);
        };

        p.done((x) -> {
            throw sentinelEx;
        });

        done.assertFulfilled();
    }


    @Test
    public void testPromise_done_when_fulfilled_should_be_fatal_when_handleValue_rejects() {

        Promise<Object> p = when.resolve(null);

        testReporter.onFatalRejectionConsumer = (h, e) -> {
            assertEquals(h.getValue(), sentinelEx);
            done.fulfill(h);
        };

        p.done((x) -> {
            return when.reject(sentinelEx);
        });

        done.assertFulfilled();

    }

    @Test
    public void testPromise_done_when_rejected_should_invoke_handleFatalError() {

        when.reject(sentinelEx)
                .done(null, (e) -> {
                    assertEquals(e, sentinelEx);
                    done.fulfill(e);
                    return null;
                });

        done.assertFulfilled();

    }

    @Test
    public void testPromise_done_when_rejected_should_be_fatal_when_no_handleFatalError_provided() {

        Promise<Object> p = when.reject(sentinelEx);

        testReporter.onFatalRejectionConsumer = (h, e) -> {
            assertEquals(h.getValue(), sentinelEx);
            done.fulfill(h);
        };

        p.done(null);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_done_when_rejected_should_be_fatal_when_handleFatalError_throws() {

        Promise<Object> p = when.reject(otherEx);

        testReporter.onFatalRejectionConsumer = (h, e) -> {
            assertEquals(h.getValue(), sentinelEx);
            done.fulfill(h);
        };

        p.done(null, (e) -> {
            throw sentinelEx;
        });

        done.assertFulfilled();
    }

    @Test
    public void testPromise_done_when_rejected_should_be_fatal_when_handleFatalError_rejects() {

        Promise<Object> p = when.reject(null);

        testReporter.onFatalRejectionConsumer = (h, e) -> {
            assertEquals(h.getValue(), sentinelEx);
            done.fulfill(h);
        };

        p.done(null, (e) -> {
            return when.reject(sentinelEx);
        });

        done.assertFulfilled();
    }

    @Test
    public void testPromise_then_should_return_a_promise() {
        assertNotNull(when.defer().getPromise().then(null));
    }

    @Test
    public void testPromise_then_should_allow_a_single_callback_function() {
        assertNotNull(when.defer().getPromise().then(f1));
    }

    @Test
    public void testPromise_then_should_allow_a_callback_and_errback_function() {
        assertNotNull(when.defer().getPromise().then(f1, f2));
    }

    @Test
    public void testPromise_then_should_allow_null_and_undefined() {
        assertNotNull(when.defer().getPromise().then(null));
        assertNotNull(when.defer().getPromise().then(null, null));
    }

    @Test
    public void testPromise_then_should_allow_functions_and_null_or_undefined_to_be_mixed() {
        assertNotNull(when.defer().getPromise().then(f1, null));
        assertNotNull(when.defer().getPromise().then(null, f2));
    }

    @Test
    public void testPromise_should_preserve_object_whose_valueOf_differs_from_original_object() {

        Deferred<Object> d;
        Object expected;

        d = when.defer();
        expected = new Date();

        d.getPromise().then(
                (val) -> {
                    assertEquals(expected, val);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.resolve(expected);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_should_forward_result_when_callback_is_null() {

        Deferred<Integer> d = when.defer();

        d.getPromise().then(
                null,
                fail.onRejected
        ).then(
                (val) -> {
                    assertEquals(val, 1);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.resolve(1);

        done.assertFulfilled();

    }

    @Test
    public void testPromise_should_forward_callback_result_to_next_callback() {

        Deferred<Integer> d = when.defer();

        d.getPromise().then(
                (val) -> {
                    return resolved(val + 1);
                },
                fail.onRejected
        ).then(
                (val) -> {
                    assertEquals(val, 2);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.resolve(1);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_should_forward_undefined() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                (val) -> {
                    // intentionally return undefined
                    return null;
                },
                fail.onRejected
        ).then(
                (val) -> {
                    assertNull(val);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.resolve(1);

        done.assertFulfilled();

    }

    @Test
    public void testPromise_should_forward_undefined_rejection_value() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                fail.onFulfilled,
                (e) -> {
                    // presence of rejection handler is enough to switch back
                    // to resolve mode, even though it returns undefined.
                    // The ONLY way to propagate a rejection is to re-throw or
                    // return a rejected promise;
                    return null;
                }
        ).then(
                (val) -> {
                    assertNull(val);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.reject(sentinelEx);

        done.assertFulfilled();

    }

    @Test
    public void testPromise_should_forward_promised_callback_result_value_to_next_callback() {

        Deferred<Integer> d = when.defer();
        Fail<Integer> fail2 = new Fail<>();

        d.getPromise().then(
                (val) -> {
                    Deferred<Integer> d1 = when.defer();
                    d1.resolve(val + 1);
                    return d1.getPromise();
                },
                fail2.onRejected
        ).then(
                (val) -> {
                    assertEquals(val.intValue(), 2);
                    return null;
                },
                fail2.onRejected
        ).ensure(done::fulfill);

        d.resolve(1);

        done.assertFulfilled();

    }

    @Test
    public void testPromise_should_switch_from_callbacks_to_errbacks_when_callback_returns_a_rejection() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                (val) -> {
                    Deferred<Object> d1 = when.defer();
                    d1.reject(sentinelEx);
                    return d1.getPromise();
                },
                fail.onRejected
        ).then(
                fail.onFulfilled,
                (val) -> {
                    assertEquals(sentinelEx, val);
                    return null;
                }
        ).ensure(done::fulfill);

        d.resolve(1);

        done.assertFulfilled();

    }

    @Test
    public void testPromise_when_an_exception_is_thrown_should_reject_if_the_exception_is_a_value() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                (x) -> {
                    throw sentinelEx;
                },
                fail.onRejected
        ).then(
                fail.onFulfilled,
                (val) -> {
                    assertEquals(sentinelEx, val);
                    return null;
                }
        ).ensure(done::fulfill);

        d.resolve(null);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_when_an_exception_is_thrown_a_rejected_promise_should_reject_if_the_exception_is_a_value() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                null,
                (t) -> {
                    throw sentinelEx;
                }
        ).then(
                fail.onFulfilled,
                (val) -> {
                    assertEquals(sentinelEx, val);
                    return null;
                }
        ).ensure(done::fulfill);

        d.reject(null);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_when_an_exception_is_thrown_a_rejected_promise_should_switch_from_errbacks_to_callbacks_when_errback_does_not_explicitly_propagate() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                fail.onFulfilled,
                (val) -> {
                    return resolved(2);
                }
        ).then(
                (val) -> {
                    assertEquals(2, val);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.reject(sentinelEx);

        done.assertFulfilled();

    }

    @Test
    public void testPromise_should_switch_from_errbacks_to_callbacks_when_errback_returns_a_resolution() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                fail.onFulfilled,
                (val) -> {
                    Deferred<Object> d1 = when.defer();
                    d1.resolve(2);
                    return d.getPromise();
                }
        ).then(
                (val) -> {
                    assertEquals(2, val);
                    return null;
                },
                fail.onRejected
        ).ensure(done::fulfill);

        d.reject(sentinelEx);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_should_propagate_rejections_when_errback_returns_a_rejection() {

        Deferred<Object> d = when.defer();

        d.getPromise().then(
                fail.onFulfilled,
                (val) -> {
                    Deferred<Object> d1 = when.defer();
                    d1.reject(otherEx);
                    return d.getPromise();
                }
        ).then(
                fail.onFulfilled,
                (val) -> {
                    assertEquals(otherEx, val);
                    return null;
                }
        ).ensure(done::fulfill);

        d.reject(sentinelEx);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_otherwise_should_return_a_promise() {
        assertNotNull(when.defer().getPromise().otherwise(null));
    }

    @Test
    public void testPromise_otherwise_should_register_errback() {

        when.reject(sentinelEx)
                .otherwise(
                        (val) -> {
                            assertEquals(sentinelEx, val);
                            return null;
                        })
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_yield_should_return_a_promise() {
        assertNotNull(when.defer().getPromise().yield(null));
    }

    @Test
    public void testPromise_yield_should_fulfill_with_value_of_a_fulfilled_promise() {

        when.resolve(other)
                .yield(resolved(sentinel))
                .then(
                        (value) -> {
                            assertEquals(sentinel, value);
                            return null;
                        }
                )
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_yield_should_reject_with_the_reason_of_a_rejected_promise() {

        when.resolve(other)
                .yield(when.reject(sentinelEx))
                .then(
                        fail.onFulfilled,
                        (reason) -> {
                            assertEquals(sentinelEx, reason);
                            return null;
                        }
                )
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_tap_should_return_a_promise() {
        assertNotNull(when.defer().getPromise().tap(null));
    }

    @Test
    public void testPromise_tap_should_fulfill_with_the_original_value() {

        when.resolve(sentinel)
                .tap((x) -> {
                    return resolved(other);
                })
                .then((value) -> {
                    assertEquals(sentinel, value);
                    return null;
                })
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_tap_should_reject_with_thrown_exception_if_tap_function_throws() {

        when.resolve(other)
                .tap((x) -> {
                    throw sentinelEx;
                })
                .then(
                        fail2.onFulfilled,
                        (value) -> {
                            assertEquals(sentinelEx, value);
                            return null;
                        })
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_tap_should_reject_with_rejection_reason_if_tap_function_rejects() {

        when.resolve(other)
                .tap(
                        (x) -> {
                            return when.reject(sentinelEx);
                        })
                .then(
                        fail2.onFulfilled,
                        (value) -> {
                            assertEquals(sentinelEx, value);
                            return null;
                        })
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_inspect_when_inspecting_promises_should_return_pending_state_for_pending_promise() {

        Promise<Object> promise = when.promise((x, y) -> {
        });

        assertPending(promise.inspect());
    }

    @Test
    public void testPromise_inspect_when_inspecting_promises_should_return_fulfilled_state_for_fulfilled_promise() {

        Promise<Sentinel> promise = when.resolve(sentinel);

        promise.then((x) -> {
            assertFulfilled(promise.inspect(), sentinel);
            return null;
        }).ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_inspect_when_inspecting_promises_should_return_rejected_state_for_rejected_promise() {

        Promise<Object> promise = when.reject(sentinelEx);

        promise.then(fail.onFulfilled, (t) -> {
            assertRejected(promise.inspect(), sentinelEx);
            return null;
        }).ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_inspect_when_inspecting_thenables_should_return_pending_state_for_pending_thenable() {

        Thenable<Object> thenable = new Thenable<Object>() {
            @Override
            public <U> Thenable<U> then(Function<Object, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                return null;
            }
        };

        Promise<Object> p = when.when(thenable);

        assertPending(p.inspect());
    }

    @Test
    public void testPromise_inspect_when_inspecting_thenables_should_return_fulfilled_state_for_fulfilled_thenable() {

        Thenable<Sentinel> thenable = new Thenable<Sentinel>() {
            @Override
            public <U> Thenable<U> then(Function<Sentinel, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                return onFulfilled.apply(sentinel);
            }
        };

        Promise<Sentinel> p = when.when(thenable);

        p.then((x) -> {
            assertFulfilled(p.inspect(), sentinel);
            return null;
        }).ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testPromise_inspect_when_inspecting_thenables_should_return_rejected_state_for_rejected_thenable() {

        Thenable<Object> thenable = new Thenable<Object>() {
            @Override
            public <U> Thenable<U> then(Function<Object, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
                return onRejected.apply(sentinelEx);
            }
        };

        Promise<Object> p = when.when(thenable);

        p.then(fail.onFulfilled, (t) -> {
            assertRejected(p.inspect(), sentinelEx);
            return null;
        }).ensure(done::fulfill);

        done.assertFulfilled();
    }

}
