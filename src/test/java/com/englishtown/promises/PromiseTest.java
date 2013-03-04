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

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 4:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class PromiseTest {

    private Runnable<Promise<Integer, Integer>, Integer> onSuccess = new Runnable<Promise<Integer, Integer>, Integer>() {
        @Override
        public Promise<Integer, Integer> run(Integer value) {
            return null;
        }
    };

    private Runnable<Promise<Integer, Integer>, Value<Integer>> onFail = new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
        @Override
        public Promise<Integer, Integer> run(Value<Integer> value) {
            return null;
        }
    };

    private Runnable<Value<Integer>, Value<Integer>> onProgress = new Runnable<Value<Integer>, Value<Integer>>() {
        @Override
        public Value<Integer> run(Value<Integer> value) {
            return value;
        }
    };

    private Fail<Integer, Integer> fail = new Fail<>();
    private Fail<Object, Object> fail2 = new Fail<>();

    private Object sentinel = new Object();
    private Object other = new Object();

    @Test
    public void testPromise_should_return_a_promise() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(null));
    }

    @Test
    public void testThen_should_allow_a_single_callback_function() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(onSuccess));
    }

    @Test
    public void testThen_should_allow_a_callback_and_errback_function() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(onSuccess, onFail));
    }

    @Test
    public void testThen_should_allow_a_callback_errback_and_progback_function() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(onSuccess, onFail, onProgress));
    }

//            'should allow null and undefined': function() {
//        assert.isFunction(defer().promise.then().then);
//
//        assert.isFunction(defer().promise.then(null).then);
//        assert.isFunction(defer().promise.then(null, null).then);
//        assert.isFunction(defer().promise.then(null, null, null).then);
//
//        assert.isFunction(defer().promise.then(undef).then);
//        assert.isFunction(defer().promise.then(undef, undef).then);
//        assert.isFunction(defer().promise.then(undef, undef, undef).then);
//    },


//    'should ignore non-functions': {
//        'when fulfillment handler': {
//            'is empty string': function(done) {
//                when.resolve(true).then('').then(assert, fail).always(done);
//            },
//            'is false': function(done) {
//                when.resolve(true).then(false).then(assert, fail).always(done);
//            },
//            'is true': function(done) {
//                when.resolve(true).then(true).then(assert, fail).always(done);
//            },
//            'is object': function(done) {
//                when.resolve(true).then({}).then(assert, fail).always(done);
//            },
//            'is falsey': function(done) {
//                when.resolve(true).then(0).then(assert, fail).always(done);
//            },
//            'is truthy': function(done) {
//                when.resolve(true).then(1).then(assert, fail).always(done);
//            }
//        },
//
//        'when rejection handler': {
//            'is empty string': function(done) {
//                when.reject(true).then(null, '').then(fail, assert).always(done);
//            },
//            'is false': function(done) {
//                when.reject(true).then(null, false).then(fail, assert).always(done);
//            },
//            'is true': function(done) {
//                when.reject(true).then(null, true).then(fail, assert).always(done);
//            },
//            'is object': function(done) {
//                when.reject(true).then(null, {}).then(fail, assert).always(done);
//            },
//            'is falsey': function(done) {
//                when.reject(true).then(null, 0).then(fail, assert).always(done);
//            },
//            'is truthy': function(done) {
//                when.reject(true).then(null, 1).then(fail, assert).always(done);
//            }
//        },
//
//        'when progress handler': {
//            'is empty string': function(done) {
//                var d = when.defer();
//                d.promise.then(null, null, '').then(fail, fail, assert).then(null, null, done);
//                d.notify(true);
//            },
//            'is false': function(done) {
//                var d = when.defer();
//                d.promise.then(null, null, false).then(fail, fail, assert).then(null, null, done);
//                d.notify(true);
//            },
//            'is true': function(done) {
//                var d = when.defer();
//                d.promise.then(null, null, true).then(fail, fail, assert).then(null, null, done);
//                d.notify(true);
//            },
//            'is object': function(done) {
//                var d = when.defer();
//                d.promise.then(null, null, {}).then(fail, fail, assert).then(null, null, done);
//                d.notify(true);
//            },
//            'is falsey': function(done) {
//                var d = when.defer();
//                d.promise.then(null, null, 0).then(fail, fail, assert).then(null, null, done);
//                d.notify(true);
//            },
//            'is truthy': function(done) {
//                var d = when.defer();
//                d.promise.then(null, null, 1).then(fail, fail, assert).then(null, null, done);
//                d.notify(true);
//            }
//        }
//    }

    @Test
    public void testThen_should_allow_functions_and_null_or_undefined_to_be_mixed() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(null, onFail));
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(null, null, onProgress));
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(null, onFail, onProgress));
    }

    @Test
    public void testPromise_should_preserve_object_whose_valueOf_differs_from_original_object() {

        Done<Date, Integer> done = new Done<>();
        Deferred<Date, Integer> d = new When<Date, Integer>().defer();
        final Date expected = new Date();

        d.getPromise().then(
                new Runnable<Promise<Date, Integer>, Date>() {
                    @Override
                    public Promise<Date, Integer> run(Date value) {
                        assertEquals(expected, value);
                        return null;
                    }
                },
                new Runnable<Promise<Date, Integer>, Value<Date>>() {
                    @Override
                    public Promise<Date, Integer> run(Value<Date> value) {
                        fail();
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(expected);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_forward_result_when_callback_is_null() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                null,
                fail.onFail
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_forward_callback_result_to_next_callback() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        When<Integer, Integer> w1 = new When<>();
                        return w1.resolve(value + 1);
                    }
                },
                fail.onFail
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_forward_undefined() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        // intentionally return undefined
                        return null;
                    }
                },
                fail.onFail
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertNull(value);
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_forward_undefined_rejection_value() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        // presence of rejection handler is enough to switch back
                        // to resolve mode, even though it returns undefined.
                        // The ONLY way to propagate a rejection is to re-throw or
                        // return a rejected promise;
                        return null;
                    }
                }
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertNull(value);
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_forward_promised_callback_result_value_to_next_callback() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        Deferred<Integer, Integer> d1 = new When<Integer, Integer>().defer();
                        d1.getResolver().resolve(value + 1);
                        return d1.getPromise();
                    }
                },
                fail.onFail
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_switch_from_callbacks_to_errbacks_when_callback_returns_a_rejection() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        Deferred<Integer, Integer> d1 = new When<Integer, Integer>().defer();
                        d1.getResolver().reject(value + 1);
                        return d1.getPromise();
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(2, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testWhen_an_exception_is_thrown_a_resolved_promise_should_reject_if_the_exception_is_a_value() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();
        final RuntimeException err = new RuntimeException();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        throw err;
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(err, value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve((Integer) null);
        done.assertSuccess();

    }

//            'should reject if the exception is a resolved promise': function(done) {
//                var d, expected;
//
//                d = when.defer();
//                expected = when.resolve();
//
//                d.promise.then(
//                        function() {
//                    throw expected;
//                },
//                fail
//                ).then(
//                        fail,
//                        function(val) {
//                    assert.same(val, expected);
//                }
//                ).always(done);
//
//                d.resolve();
//            },
//
//            'should reject if the exception is a rejected promise': function(done) {
//                var d, expected;
//
//                d = when.defer();
//                expected = when.reject();
//
//                d.promise.then(
//                        function() {
//                    throw expected;
//                },
//                fail
//                ).then(
//                        fail,
//                        function(val) {
//                    assert.same(val, expected);
//                }
//                ).always(done);
//
//                d.resolve();
//            }
//
//        },
//
//        'a rejected promise': {
//

    @Test
    public void testWhen_an_exception_is_thrown_a_rejected_promise_should_reject_if_the_exception_is_a_value() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();
        final RuntimeException err = new RuntimeException();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        throw err;
                    }
                }
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(err, value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject((Integer) null);
        done.assertSuccess();

    }

//
//            'should reject if the exception is a resolved promise': function(done) {
//                var d, expected;
//
//                d = when.defer();
//                expected = when.resolve();
//
//                d.promise.then(
//                        null,
//                        function() {
//                    throw expected
//                }
//                ).then(
//                        fail,
//                        function(val) {
//                    assert.same(val, expected);
//                }
//                ).always(done);
//
//                d.reject();
//            },
//
//            'should reject if the exception is a rejected promise': function(done) {
//                var d, expected;
//
//                d = when.defer();
//                expected = when.reject();
//
//                d.promise.then(
//                        null,
//                        function() {
//                    throw expected;
//                }
//                ).then(
//                        fail,
//                        function(val) {
//                    assert.same(val, expected);
//                }
//                ).always(done);
//
//                d.reject();
//            }
//
//        }
//    },

    @Test
    public void testPromise_should_switch_from_errbacks_to_callbacks_when_errback_does_not_explicitly_propagate() {

        Done<Integer, Integer> done = new Done<>();
        final When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        return when.resolve(value.value + 1);
                    }
                }
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(1);
        done.assertSuccess();
    }

    @Test
    public void testPromise_should_switch_from_errbacks_to_callbacks_when_errback_returns_a_resolution() {

        Done<Integer, Integer> done = new Done<>();
        final When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        Deferred<Integer, Integer> d = when.defer();
                        d.getResolver().resolve(value.value + 1);
                        return d.getPromise();
                    }
                }
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_propagate_rejections_when_errback_returns_a_rejection() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        Deferred<Integer, Integer> d1 = new When<Integer, Integer>().defer();
                        d1.getResolver().reject(value.value + 1);
                        return d1.getPromise();
                    }
                }
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(2, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().reject(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_call_progback() {

        final Done<Object, Object> done = new Done<>();
        Deferred<Object, Object> d = new When<>().defer();
        final Object expected = new Object();

        d.getPromise().then(null, null, new Runnable<Value<Object>, Value<Object>>() {
            @Override
            public Value<Object> run(Value<Object> value) {
                assertEquals(expected, value.value);
                done.success = true;
                return null;
            }
        });

        d.getResolver().notify(expected);
        done.assertSuccess();

    }

    @Test
    public void testAlways_should_return_a_promise() {
        When<Object, Object> when = new When<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.defer().getPromise();

        assertNotNull(p.always(null));
    }

    @Test
    public void testAlways_should_register_callback() {

        final Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        ((PromiseExt<Integer, Integer>) d.getPromise()).always(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        done.success = true;
                        return null;
                    }
                });

        d.getResolver().resolve(1);
        done.assertSuccess();
    }

    @Test
    public void testAlways_should_register_errback() {

        final Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        ((PromiseExt<Integer, Integer>) d.getPromise()).always(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        done.success = true;
                        return null;
                    }
                });

        d.getResolver().reject(1);
        done.assertSuccess();
    }

    @Test
    public void testAlways_should_register_progback() {

        final Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        ((PromiseExt<Integer, Integer>) d.getPromise()).always(
                null,
                new Runnable<Value<Integer>, Value<Integer>>() {
                    @Override
                    public Value<Integer> run(Value<Integer> value) {
                        assertEquals(1, value.value.intValue());
                        done.success = true;
                        return null;
                    }
                });

        d.getResolver().notify(1);
        done.assertSuccess();
    }

    @Test
    public void testOtherwise_should_return_a_promise() {
        PromiseExt<Integer, Integer> p = (PromiseExt<Integer, Integer>) new When<Integer, Integer>().defer().getPromise();
        assertNotNull(p.otherwise(null));
    }

    @Test
    public void testOtherwise_should_register_errback() {

        final Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();
        PromiseExt<Integer, Integer> p = (PromiseExt<Integer, Integer>) d.getPromise();

        p.otherwise(new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
            @Override
            public Promise<Integer, Integer> run(Value<Integer> value) {
                assertEquals(1, value.value.intValue());
                done.success = true;
                return null;
            }
        });

        d.getResolver().reject(1);
        done.assertSuccess();
    }

    @Test
    public void testYield_should_return_a_promise() {
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) new When<>().defer().getPromise();
        assertNotNull(p.yield(null));
    }

    @Test
    public void testYield_should_fulfill_with_the_supplied_value() {

        Done<Object, Object> done = new Done<>();
        When<Object, Object> when = new When<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.resolve(other);

        p.yield(sentinel).then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(sentinel, value);
                        return null;
                    }
                },
                fail2.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testYield_should_fulfill_with_the_value_of_a_fulfilled_promise() {

        Done<Object, Object> done = new Done<>();
        When<Object, Object> when = new When<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.resolve(other);

        p.yield(when.resolve(sentinel)).then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(sentinel, value);
                        return null;
                    }
                }).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testYield_should_reject_with_the_reason_of_a_rejected_promise() {

        Done<Object, Object> done = new Done<>();
        When<Object, Object> when = new When<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.resolve(other);

        p.yield(when.reject(sentinel)).then(
                fail2.onSuccess,
                new Runnable<Promise<Object, Object>, Value<Object>>() {
                    @Override
                    public Promise<Object, Object> run(Value<Object> reason) {
                        assertEquals(sentinel, reason.value);
                        return null;
                    }
                }).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

//            'spread': {
//        'should return a promise': function() {
//            assert.isFunction(defer().promise.spread().then);
//        },
//
//        'should apply onFulfilled with array as argument list': function(done) {
//            var expected = [1, 2, 3];
//            when.resolve(expected).spread(function() {
//                assert.equals(slice.call(arguments), expected);
//            }).always(done);
//        },
//
//        'should resolve array contents': function(done) {
//            var expected = [when.resolve(1), 2, when.resolve(3)];
//            when.resolve(expected).spread(function() {
//                assert.equals(slice.call(arguments), [1, 2, 3]);
//            }).always(done);
//        },
//
//        'should reject if any item in array rejects': function(done) {
//            var expected = [when.resolve(1), 2, when.reject(3)];
//            when.resolve(expected)
//                    .spread(fail)
//                    .then(
//                            fail,
//                            function() {
//                assert(true);
//            }
//            ).always(done);
//        },
//
//        'when input is a promise': {
//            'should apply onFulfilled with array as argument list': function(done) {
//                var expected = [1, 2, 3];
//                when.resolve(when.resolve(expected)).spread(function() {
//                    assert.equals(slice.call(arguments), expected);
//                }).always(done);
//            },
//
//            'should resolve array contents': function(done) {
//                var expected = [when.resolve(1), 2, when.resolve(3)];
//                when.resolve(when.resolve(expected)).spread(function() {
//                    assert.equals(slice.call(arguments), [1, 2, 3]);
//                }).always(done);
//            },
//
//            'should reject if input is a rejected promise': function(done) {
//                var expected = when.reject([1, 2, 3]);
//                when.resolve(expected)
//                        .spread(fail)
//                        .then(
//                                fail,
//                                function() {
//                    assert(true);
//                }
//                ).always(done);
//            }
//        }
//    }
//

}
