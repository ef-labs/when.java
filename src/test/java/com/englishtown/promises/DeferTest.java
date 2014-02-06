/*
 * The MIT License (MIT)
 * Copyright © 2013 Englishtown <opensource@englishtown.com>
 * http://englishtown.mit-license.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/30/13
 * Time: 2:12 AM
 */
public class DeferTest {

    private final Object sentinel = new Object();
    private final Object other = new Object();
    private final Fail<Object, Object> fail = new Fail<>();

    @Test
    public void testResolve_should_fulfill_with_an_immediate_value() {

        Done<Object, Object> done = new Done<>();
        Deferred<Object, Object> d = new When<>().defer();

        d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(value, sentinel);
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(sentinel);
        done.assertSuccess();

    }

    private class FakeResolved<TResolve, TProgress> implements Promise<TResolve, TProgress> {

        private TResolve value;
        private Promise<TResolve, TProgress> promise;

        public FakeResolved(TResolve value) {
            this.value = value;
        }

        public FakeResolved(Promise<TResolve, TProgress> promise) {
            this.promise = promise;
        }

        @Override
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        @Override
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        @Override
        public Promise<TResolve, TProgress> then(
                Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            return (onFulfilled != null ?
                    onFulfilled.run(this.value) :
                    new FakeResolved<TResolve, TProgress>(this.value));

        }
    }

    private class FakeRejected<TResolve, TProgress> implements Promise<TResolve, TProgress> {

        private final Value<TResolve> reason;

        public FakeRejected(Value<TResolve> reason) {
            this.reason = reason;
        }

        @Override
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        @Override
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        @Override
        public Promise<TResolve, TProgress> then(
                Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected,
                Runnable<Value<TProgress>, Value<TProgress>> onProgress) {

            return (onRejected != null ?
                    new FakeResolved<>(onRejected.run(this.reason)) :
                    new FakeRejected<TResolve, TProgress>(this.reason));

        }
    }

    @Test
    public void testResolve_should_fulfill_with_fulfilled_promised() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(value, sentinel);
                        return null;
                    }
                },
                done.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(new FakeResolved<>(sentinel));
        done.assertSuccess();

    }

    @Test
    public void testResolve_should_reject_with_rejected_promise() {

        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        Value<Object> reason = new Value<>(sentinel, null);
        d.getResolver().resolve(new FakeRejected<>(reason));
        done.assertSuccess();

    }

    @Test
    public void testResolve_should_return_a_promise_for_the_resolution_value() {
        final Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().resolve(sentinel).then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(final Object value1) {

                        d.getPromise().then(
                                new Runnable<Promise<Object, Object>, Object>() {
                                    @Override
                                    public Promise<Object, Object> run(Object value2) {
                                        assertEquals(value1, value2);
                                        return null;
                                    }

                                },
                                null,
                                null);
                        return null;

                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testResolve_should_return_a_promise_for_a_promised_resolution_value() {

        When<Object, Object> when = new When<>();
        final Deferred<Object, Object> d = when.defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().resolve(when.resolve(sentinel)).then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(final Object value1) {
                        d.getPromise().then(
                                new Runnable<Promise<Object, Object>, Object>() {
                                    @Override
                                    public Promise<Object, Object> run(Object value2) {
                                        assertEquals(value1, value2);
                                        return null;
                                    }
                                });
                        return null;
                    }
                }
                ,
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testResolve_should_return_a_promise_for_a_promised_rejection_value() {
        When<Object, Object> when = new When<>();
        Done<Object, Object> done = new Done<>();
        final Deferred<Object, Object> d = when.defer();

        // Both the returned promise, and the deferred's own promise should
        // be rejected with the same value
        d.getResolver().resolve(when.reject(sentinel)).then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(final Value<Object> value1) {
                        d.getPromise().then(
                                fail.onSuccess,
                                new Runnable<Promise<Object, Object>, Value<Object>>() {
                                    @Override
                                    public Promise<Object, Object> run(Value<Object> value2) {
                                        assertEquals(value1, value2);
                                        assertEquals(value1.value, value2.value);
                                        return null;
                                    }
                                });
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testResolve_should_invoke_newly_added_callback_when_already_resolved() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().resolve(sentinel);

        d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(value, sentinel);
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }


    @Test
    public void testReject_should_reject_with_an_immediate_value() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(sentinel);
        done.assertSuccess();

    }

    @Test
    public void testReject_should_reject_with_fulfilled_promised() {

        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();
        final Promise<Object, Object> expected = new FakeResolved<>(sentinel);

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(expected, value.value);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(expected);
        done.assertSuccess();
    }

    @Test
    public void testReject_should_reject_with_rejected_promise() {

        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();
        final Promise<Object, Object> expected = new FakeRejected<>(new Value<>(sentinel));

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(expected, value.value);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(expected);
        done.assertSuccess();
    }

    @Test
    public void testReject_should_return_a_promise_for_the_rejection_value() {
        final Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        // Both the returned promise, and the deferred's own promise should
        // be rejected with the same value
        d.getResolver().reject(sentinel).then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(final Value<Object> value1) {

                        d.getPromise().then(
                                fail.onSuccess,
                                new Runnable<Promise<Object, Object>, Value<Object>>() {
                                    @Override
                                    public Promise<Object, Object> run(Value<Object> value2) {
                                        assertEquals(value1, value2);
                                        return null;
                                    }
                                }
                        );
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testReject_should_invoke_newly_added_errback_when_already_rejected() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().reject(sentinel);

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testNotify_should_notify_of_progress_updates() {
        Deferred<Object, Object> d = new When<>().defer();
        final Done<Object, Object> done = new Done<>();

        d.getPromise().then(
                fail.onSuccess,
                fail.onFail,
                new Runnable<Value<Object>, Value<Object>>() {
                    @Override
                    public Value<Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        done.success = true;
                        return value;
                    }
                }
        );

        d.getResolver().notify(sentinel);
        done.assertSuccess();
    }

    @Test
    public void testNotify_should_propagate_progress_to_downstream_promises() {
        Deferred<Object, Object> d = new When<>().defer();
        final Done<Object, Object> done = new Done<>();

        d.getPromise()
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                return value;
                            }
                        }
                )
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                assertEquals(value.value, sentinel);
                                done.success = true;
                                return null;
                            }
                        }
                );

        d.getResolver().notify(sentinel);
        done.assertSuccess();

    }

    @Test
    public void testNotify_should_propagate_transformed_progress_to_downstream_promises() {
        Deferred<Object, Object> d = new When<>().defer();
        final Done<Object, Object> done = new Done<>();

        d.getPromise()
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                return new Value<>(sentinel);
                            }
                        }
                )
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                assertEquals(value.value, sentinel);
                                done.success = true;
                                return null;
                            }
                        }
                );

        d.getResolver().notify(other);
        done.assertSuccess();

    }

    @Test
    public void testNotify_should_propagate_caught_exception_value_as_progress() {
        Deferred<Object, Object> d = new When<>().defer();
        final Done<Object, Object> done = new Done<>();
        final RuntimeException error = new RuntimeException();

        d.getPromise()
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                throw error;
                            }
                        }
                )
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                assertEquals(error, value.error);
                                done.success = true;
                                return null;
                            }
                        }
                );

        d.getResolver().notify(other);
        done.assertSuccess();
    }

    @Test
    public void
    testNotify_should_forward_progress_events_when_intermediary_callback_tied_to_a_resolved_promise_returns_a_promise() {

        final Done<Object, Object> done = new Done<>();
        Deferred<Object, Object> d = new When<>().defer();
        final Deferred<Object, Object> d2 = new When<>().defer();

        // resolve d BEFORE calling attaching progress handler
        d.getResolver().resolve(null);

        d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        return d2.getPromise();
                    }
                }
        ).then(fail.onSuccess, fail.onFail,
                new Runnable<Value<Object>, Value<Object>>() {
                    @Override
                    public Value<Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        done.success = true;
                        return null;
                    }
                }
        );

        d2.getResolver().notify(sentinel);
        done.assertSuccess();

    }

    @Test
    public void
    testNotify_should_forward_progress_events_when_intermediary_callback_tied_to_an_unresolved_promise_returns_a_promise() {

        final Done<Object, Object> done = new Done<>();
        Deferred<Object, Object> d = new When<>().defer();
        final Deferred<Object, Object> d2 = new When<>().defer();

        d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        return d2.getPromise();
                    }
                }
        ).then(fail.onSuccess, fail.onFail,
                new Runnable<Value<Object>, Value<Object>>() {
                    @Override
                    public Value<Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        done.success = true;
                        return null;
                    }
                }
        );

        // resolve d AFTER calling attaching progress handler
        d.getResolver().resolve(null);
        d2.getResolver().notify(sentinel);
        done.assertSuccess();
    }

    @Test
    public void testNotify_should_forward_progress_when_resolved_with_another_promise() {

        final Done<Object, Object> done = new Done<>();
        Deferred<Object, Object> d = new When<>().defer();
        final Deferred<Object, Object> d2 = new When<>().defer();

        d.getPromise()
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                return new Value<>(sentinel);
                            }
                        }
                )
                .then(fail.onSuccess, fail.onFail,
                        new Runnable<Value<Object>, Value<Object>>() {
                            @Override
                            public Value<Object> run(Value<Object> value) {
                                assertEquals(value.value, sentinel);
                                done.success = true;
                                return null;
                            }
                        }
                );

        d.getResolver().resolve(d2.getPromise());
        d2.getResolver().notify(null);
        done.assertSuccess();

    }


    @Test
    public void testNotify_should_allow_resolve_after_progress() {
        Deferred<Object, Object> d = new When<>().defer();
        final Done<Object, Object> done = new Done<>();

        final ValueHolder<Boolean> progressed = new ValueHolder<>(false);

        d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertTrue(progressed.value);
                        done.success = true;
                        return null;
                    }
                },
                fail.onFail,
                new Runnable<Value<Object>, Value<Object>>() {
                    @Override
                    public Value<Object> run(Value<Object> value) {
                        progressed.value = true;
                        return null;
                    }
                }
        );

        d.getResolver().notify(null);
        d.getResolver().resolve(null);
        done.assertSuccess();
    }

    @Test
    public void testNotify_should_allow_reject_after_progress() {
        Deferred<Object, Object> d = new When<>().defer();
        final ValueHolder<Boolean> progressed = new ValueHolder<>(false);
        final Done<Object, Object> done = new Done<>();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertTrue(progressed.value);
                        done.success = true;
                        return null;
                    }
                },
                new Runnable<Value<Object>, Value<Object>>() {
                    @Override
                    public Value<Object> run(Value<Object> value) {
                        progressed.value = true;
                        return null;
                    }
                }
        );

        d.getResolver().notify(null);
        d.getResolver().reject((Value<Object>) null);
        done.assertSuccess();
    }

    @Test
    public void testNotify_should_be_indistinguishable_after_resolution() {
        Value<Object> before, after;

        Deferred<Object, Object> d = new When<>().defer();

        before = d.getResolver().notify(sentinel);
        d.getResolver().resolve(null);
        after = d.getResolver().notify(sentinel);

        assertEquals(before.value, after.value);
    }

    @Test
    public void testDefer_should_return_a_promise_for_passed_in_resolution_value_when_already_resolved() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().resolve(other);

        d.getResolver().resolve(sentinel).then(new Runnable<Promise<Object, Object>, Object>() {
            @Override
            public Promise<Object, Object> run(Object value) {
                assertEquals(value, sentinel);
                return null;
            }
        }).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testDefer_should_return_a_promise_for_passed_in_rejection_value_when_already_resolved() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().resolve(other);

        d.getResolver().reject(sentinel).then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

//    @Test
//    public void testDefer_should_return_silently_on_progress_when_already_resolved() {
//        Deferred<Object, Object> d = new When<>().defer();
//
//        d.getResolver().resolve(null);
//        assertNull(d.getResolver().notify(null));
//    }

    @Test
    public void testDefer_should_return_a_promise_for_passed_in_resolution_value_when_already_rejected() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().reject(other);

        d.getResolver().resolve(sentinel).then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(value, sentinel);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testDefer_should_return_a_promise_for_passed_in_rejection_value_when_already_rejected() {
        Deferred<Object, Object> d = new When<>().defer();
        Done<Object, Object> done = new Done<>();

        d.getResolver().reject(other);

        d.getResolver().reject(sentinel).then(
                fail.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> value) {
                        assertEquals(value.value, sentinel);

                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

//    @Test
//    public void testDefer_should_return_silently_on_progress_when_already_rejected() {
//        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();
//        Integer reason = null;
//        d.getResolver().reject(reason);
//
//        assertNull(d.getResolver().notify(1));
//    }

}
