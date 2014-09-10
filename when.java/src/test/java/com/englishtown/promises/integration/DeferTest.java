package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import org.junit.Test;

import java.util.function.Function;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Integration tests for when.defer()
 */
public class DeferTest extends AbstractIntegrationTest {

    private final Done<Sentinel> done2 = new Done<>();
    private final Fail<Sentinel> fail2 = new Fail<>();

    private final Sentinel sentinel = new Sentinel();

    private static class FakeResolved<T> implements Thenable<T> {

        private final T val;

        public FakeResolved(T val) {
            this.val = val;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <U> Thenable<U> then(Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
            return onFulfilled != null ? onFulfilled.apply(val) : new FakeResolved<>((U) val);
        }
    }

    private <T> Thenable<T> fakeResolved(T val) {
        return new FakeResolved<>(val);
    }

    private static class FakeRejected<T> implements Thenable<T> {

        private final Throwable reason;

        public FakeRejected(Throwable reason) {
            this.reason = reason;
        }

        @Override
        public <U> Thenable<U> then(Function<T, ? extends Thenable<U>> onFulfilled, Function<Throwable, ? extends Thenable<U>> onRejected) {
            return onRejected != null ? onRejected.apply(reason) : new FakeRejected<>(reason);
        }
    }

    private <T> Thenable<T> fakeRejected(Throwable reason) {
        return new FakeRejected<>(reason);
    }


    /*


function fakeResolved(val) {
	return {
		then: function(callback) {
			return fakeResolved(callback ? callback(val) : val);
		}
	};
}

function fakeRejected(reason) {
	return {
		then: function(callback, errback) {
			return errback ? fakeResolved(errback(reason)) : fakeRejected(reason);
		}
	};
}

     */

    @Test
    public void testDefer_fulfilled() throws Exception {

        Deferred<Integer> d = when.defer();

        Function<Integer, Promise<Integer>> fulfilled = i -> {
            assertThat(i, instanceOf(Integer.class));
            int val = 2 * i;
            return when.resolve(val);
        };

        d.getPromise()
                .then(fulfilled)
                .then(fulfilled)
                .then(fulfilled)
                .then(i -> when.resolve("value=" + i))
                .then(s -> {
                    assertNotNull(s);
                    assertEquals("value=8", s);
                    return when.resolve(50L);
                }).then(
                l -> {
                    assertNotNull(l);
                    assertEquals(50, l.longValue());
                    assertThat(l, instanceOf(Long.class));
                    return when.resolve(sentinel);
                }).then(done2.onFulfilled, done2.onRejected);

        d.resolve(1);
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_fulfilled_null() throws Exception {

        Deferred<Integer> d = when.defer();
        Function<Integer, Promise<Integer>> onFulfilled = null;

        d.getPromise()
                .then(onFulfilled)
                .then(onFulfilled)
                .then(i -> {
                    assertNotNull(i);
                    assertEquals(1, i.intValue());
                    return (Promise<Integer>) null;
                })
                .<Sentinel>then(i -> {
                    assertNull(i);
                    return null;
                })
                .then(done2.onFulfilled, done2.onRejected);

        d.resolve(1);
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_fulfilled_throws() throws Exception {

        Deferred<String> d = when.defer();
        RuntimeException t = new RuntimeException();

        Function<String, Promise<Sentinel>> onFulfilled = x -> {
            throw t;
        };

        d.getPromise()
                .then(onFulfilled)
                .then(fail2.onFulfilled,
                        val -> {
                            assertEquals(t, val);
                            return null;
                        })
                .then(done2.onFulfilled, done2.onRejected);

        d.resolve((String) null);
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_rejected() throws Exception {

        Deferred<Sentinel> d = when.defer();
        Throwable cause = new RuntimeException();

        d.getPromise()
                .then(
                        fail2.onFulfilled,
                        t -> {
                            assertEquals(cause, t);
                            return when.resolve(sentinel);
                        })
                .then(
                        val -> {
                            assertNotNull(val);
                            assertEquals(sentinel, val);
                            return when.reject(null);
                        },
                        fail2.onRejected)
                .then(
                        fail2.onFulfilled,
                        t -> {
                            assertNull(t);
                            return when.resolve(null);
                        })
                .then(done2.onFulfilled, done2.onRejected);

        d.reject(cause);
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_rejected_throws() throws Exception {

        Deferred<String> d = when.defer();

        Function<Throwable, Promise<Sentinel>> onRejected = t -> {
            throw new RuntimeException();
        };

        d.getPromise()
                .then(null, onRejected)
                .then(
                        fail2.onFulfilled,
                        t -> {
                            assertNotNull(t);
                            return null;
                        })
                .then(done2.onFulfilled, done2.onRejected);

        d.reject(null);
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_resolve_should_fulfill_with_an_immediate_value() throws Exception {

        Deferred<Sentinel> d = when.defer();

        d.getPromise().then(
                val -> {
                    assertEquals(sentinel, val);
                    done2.fulfill(val);
                    return null;
                },
                done2.onRejected
        );

        d.resolve(sentinel);
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_resolve_should_fulfill_with_fulfilled_promised() throws Exception {
        Deferred<Sentinel> d = when.defer();

        d.getPromise().then(
                val -> {
                    assertEquals(sentinel, val);
                    done2.fulfill(val);
                    return null;
                },
                done2.onRejected
        );

        d.resolve(fakeResolved(sentinel));
        done2.assertFulfilled();
    }

    @Test
    public void testDefer_resolve_should_reject_with_rejected_promise() throws Exception {

        Deferred<Sentinel> d = when.defer();
        Throwable t = new RuntimeException();

        d.getPromise()
                .then(
                        fail2.onFulfilled,
                        x -> {
                            assertEquals(t, x);
                            return null;
                        })
                .then(done2.onFulfilled, done2.onRejected);

        d.resolve(fakeRejected(t));
        done2.assertFulfilled();

    }

    @Test
    public void testDefer_resolve_should_invoke_newly_added_callback_when_already_resolved() throws Exception {

        Deferred<Sentinel> d = when.defer();
        d.resolve(sentinel);

        d.getPromise().then(
                val -> {
                    assertEquals(sentinel, val);
                    return null;
                },
                fail2.onRejected
        ).then(done2.onFulfilled, done2.onRejected);

        done2.assertFulfilled();
    }

    @Test
    public void testDefer_reject_should_reject_with_an_immediate_value() throws Exception {

        Deferred<Sentinel> d = when.defer();
        Throwable t = new RuntimeException();

        d.getPromise().then(
                fail2.onFulfilled,
                val -> {
                    assertEquals(t, val);
                    return null;
                }
        ).then(done2.onFulfilled, done2.onRejected);

        d.reject(t);
        done2.assertFulfilled();

    }

//    @Test
//    public void testDefer_reject_should_reject_with_fulfilled_promised() throws Exception {
//
//        Deferred<Sentinel> d = when.defer();
//        Promise<Sentinel> expected = fakeResolved(sentinel);
//
//        d = when.defer();
//        expected = fakeResolved(sentinel);
//
//        d.promise.then(
//                fail,
//                function(val) {
//            assert.same(val, expected);
//        }
//        ).ensure(done);
//
//        d.reject(expected);
//
//    }

//    @Test
//    public void testDefer_reject_should_reject_with_rejected_promise() throws Exception {
//
//            var d, expected;
//
//            d = when.defer();
//            expected = fakeRejected(sentinel);
//
//            d.promise.then(
//                    fail,
//                    function(val) {
//                assert.same(val, expected);
//            }
//            ).ensure(done);
//
//            d.reject(expected);
//        }
//
//    }

    @Test
    public void testDefer_reject_should_invoke_newly_added_errback_when_already_rejected() throws Exception {

        Deferred<Sentinel> d = when.defer();
        Throwable x = new RuntimeException();

        d.reject(x);

        d.getPromise().then(
                fail2.onFulfilled,
                val -> {
                    assertEquals(x, val);
                    return null;
                }
        ).then(done2.onFulfilled, done2.onRejected);

        done2.assertFulfilled();

    }

    /*

    @Test
    public void testDefer_reject_() throws Exception {
    }

	'reject': {

	'notify': {

		'should notify of progress updates': function(done) {
			var d = when.defer();

			d.promise.then(
				fail,
				fail,
				function(val) {
					assert.same(val, sentinel);
					done();
				}
			);

			d.notify(sentinel);
		},

		'should propagate progress to downstream promises': function(done) {
			var d = when.defer();

			d.promise
			.then(fail, fail,
				function(update) {
					return update;
				}
			)
			.then(fail, fail,
				function(update) {
					assert.same(update, sentinel);
					done();
				}
			);

			d.notify(sentinel);
		},

		'should propagate transformed progress to downstream promises': function(done) {
			var d = when.defer();

			d.promise
			.then(fail, fail,
				function() {
					return sentinel;
				}
			)
			.then(fail, fail,
				function(update) {
					assert.same(update, sentinel);
					done();
				}
			);

			d.notify(other);
		},

		'should propagate caught exception value as progress': function(done) {
			var d = when.defer();

			d.promise
			.then(fail, fail,
				function() {
					throw sentinel;
				}
			)
			.then(fail, fail,
				function(update) {
					assert.same(update, sentinel);
					done();
				}
			);

			d.notify(other);
		},

		'should forward progress events when intermediary callback (tied to a resolved promise) returns a promise': function(done) {
			var d, d2;

			d = when.defer();
			d2 = when.defer();

			// resolve d BEFORE calling attaching progress handler
			d.resolve();

			d.promise.then(
				function() {
					return when.promise(function(resolve, reject, notify) {
						setTimeout(function() {
							notify(sentinel);
						}, 0);
					});
				}
			).then(null, null,
				function onProgress(update) {
					assert.same(update, sentinel);
					done();
				}
			);
		},

		'should forward progress events when intermediary callback (tied to an unresolved promise) returns a promise': function(done) {
			var d = when.defer();

			d.promise.then(
				function() {
					return when.promise(function(resolve, reject, notify) {
						setTimeout(function() {
							notify(sentinel);
						}, 0);
					});
				}
			).then(null, null,
				function onProgress(update) {
					assert.same(update, sentinel);
					done();
				}
			);

			// resolve d AFTER calling attaching progress handler
			d.resolve();
		},

		'should forward progress when resolved with another promise': function(done) {
			var d, d2;

			d = when.defer();
			d2 = when.defer();

			d.promise
			.then(fail, fail,
				function() {
					return sentinel;
				}
			)
			.then(fail, fail,
				function(update) {
					assert.same(update, sentinel);
					done();
				}
			);

			d.resolve(d2.promise);

			d2.notify();
		},

		'should allow resolve after progress': function(done) {
			var d = when.defer();

			var progressed = false;
			d.promise.then(
				function() {
					assert(progressed);
					done();
				},
				fail,
				function() {
					progressed = true;
				}
			);

			d.notify();
			d.resolve();
		},

		'should allow reject after progress': function(done) {
			var d = when.defer();

			var progressed = false;
			d.promise.then(
				fail,
				function() {
					assert(progressed);
					done();
				},
				function() {
					progressed = true;
				}
			);

			d.notify();
			d.reject();
		},

		'should be indistinguishable after resolution': function() {
			var d, before, after;

			d = when.defer();

			before = d.notify(sentinel);
			d.resolve();
			after = d.notify(sentinel);

			assert.same(before, after);
		}
	},

	'should return silently on progress when already resolved': function() {
		var d = when.defer();
		d.resolve();

		refute.defined(d.notify());
	},

	'should return silently on progress when already rejected': function() {
		var d = when.defer();
		d.reject();

		refute.defined(d.notify());
	}

     */
}