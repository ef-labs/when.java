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
 *
 */
public class PromiseTest {

    private final Runnable<ProgressPromise<Integer, Integer>, Integer> onSuccess = new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
        @Override
        public ProgressPromise<Integer, Integer> run(Integer value) {
            return null;
        }
    };

    private final Runnable<ProgressPromise<Integer, Integer>, Value<Integer>> onFail = new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
        @Override
        public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
            return null;
        }
    };

    private final Runnable<Value<Integer>, Value<Integer>> onProgress = new Runnable<Value<Integer>, Value<Integer>>() {
        @Override
        public Value<Integer> run(Value<Integer> value) {
            return value;
        }
    };

    private final Fail<Integer, Integer> fail = new Fail<>();
    private final Fail<Object, Object> fail2 = new Fail<>();

    private final Object sentinel = new Object();
    private final Object other = new Object();

    @Test
    public void testPromise_should_return_a_promise() {
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(null));
    }

    @Test
    public void testThen_should_allow_a_single_callback_function() {
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(onSuccess));
    }

    @Test
    public void testThen_should_allow_a_callback_and_errback_function() {
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(onSuccess, onFail));
    }

    @Test
    public void testThen_should_allow_a_callback_errback_and_progback_function() {
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(onSuccess, onFail, onProgress));
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
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(null, onFail));
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(null, null, onProgress));
        assertNotNull(new WhenProgress<Integer, Integer>().defer().getPromise().then(null, onFail, onProgress));
    }

    @Test
    public void testPromise_should_preserve_object_whose_valueOf_differs_from_original_object() {

        Done<Date, Integer> done = new Done<>();
        DeferredProgress<Date, Integer> d = new WhenProgress<Date, Integer>().defer();
        final Date expected = new Date();

        d.getPromise().then(
                new Runnable<ProgressPromise<Date, Integer>, Date>() {
                    @Override
                    public ProgressPromise<Date, Integer> run(Date value) {
                        assertEquals(expected, value);
                        return null;
                    }
                },
                new Runnable<ProgressPromise<Date, Integer>, Value<Date>>() {
                    @Override
                    public ProgressPromise<Date, Integer> run(Value<Date> value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                null,
                fail.onFail
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        WhenProgress<Integer, Integer> w1 = new WhenProgress<>();
                        return w1.resolve(value + 1);
                    }
                },
                fail.onFail
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        // intentionally return undefined
                        return null;
                    }
                },
                fail.onFail
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        // presence of rejection handler is enough to switch back
                        // to resolve mode, even though it returns undefined.
                        // The ONLY way to propagate a rejection is to re-throw or
                        // return a rejected promise;
                        return null;
                    }
                }
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        DeferredProgress<Integer, Integer> d1 = new WhenProgress<Integer, Integer>().defer();
                        d1.getResolver().resolve(value + 1);
                        return d1.getPromise();
                    }
                },
                fail.onFail
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        DeferredProgress<Integer, Integer> d1 = new WhenProgress<Integer, Integer>().defer();
                        d1.getResolver().reject(value + 1);
                        return d1.getPromise();
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(2, value.getValue().intValue());
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();
        final RuntimeException err = new RuntimeException();

        d.getPromise().then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        throw err;
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(err, value.getCause());
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();
        final RuntimeException err = new RuntimeException();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        throw err;
                    }
                }
        ).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(err, value.getCause());
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
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        DeferredProgress<Integer, Integer> d = when.defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        return when.resolve(value.getValue() + 1);
                    }
                }
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        DeferredProgress<Integer, Integer> d = when.defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        DeferredProgress<Integer, Integer> d = when.defer();
                        d.getResolver().resolve(value.getValue() + 1);
                        return d.getPromise();
                    }
                }
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        DeferredProgress<Integer, Integer> d1 = new WhenProgress<Integer, Integer>().defer();
                        d1.getResolver().reject(value.getValue() + 1);
                        return d1.getPromise();
                    }
                }
        ).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(2, value.getValue().intValue());
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
        DeferredProgress<Object, Object> d = new WhenProgress<>().defer();
        final Object expected = new Object();

        d.getPromise().then(null, null, new Runnable<Value<Object>, Value<Object>>() {
            @Override
            public Value<Object> run(Value<Object> value) {
                assertEquals(expected, value.getValue());
                done.success = true;
                return null;
            }
        });

        d.getResolver().notify(expected);
        done.assertSuccess();

    }

    @Test
    public void testAlways_should_return_a_promise() {
        WhenProgress<Object, Object> when = new WhenProgress<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.defer().getPromise();

        assertNotNull(p.always(null));
    }

    @Test
    public void testAlways_should_register_callback() {

        final Done<Integer, Integer> done = new Done<>();
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        ((PromiseExt<Integer, Integer>) d.getPromise()).always(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        ((PromiseExt<Integer, Integer>) d.getPromise()).always(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
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
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();

        ((PromiseExt<Integer, Integer>) d.getPromise()).always(
                null,
                new Runnable<Value<Integer>, Value<Integer>>() {
                    @Override
                    public Value<Integer> run(Value<Integer> value) {
                        assertEquals(1, value.getValue().intValue());
                        done.success = true;
                        return null;
                    }
                });

        d.getResolver().notify(1);
        done.assertSuccess();
    }

    @Test
    public void testOtherwise_should_return_a_promise() {
        PromiseExt<Integer, Integer> p = (PromiseExt<Integer, Integer>) new WhenProgress<Integer, Integer>().defer().getPromise();
        assertNotNull(p.otherwise(null));
    }

    @Test
    public void testOtherwise_should_register_errback() {

        final Done<Integer, Integer> done = new Done<>();
        DeferredProgress<Integer, Integer> d = new WhenProgress<Integer, Integer>().defer();
        PromiseExt<Integer, Integer> p = (PromiseExt<Integer, Integer>) d.getPromise();

        p.otherwise(new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                assertEquals(1, value.getValue().intValue());
                done.success = true;
                return null;
            }
        });

        d.getResolver().reject(1);
        done.assertSuccess();
    }

    @Test
    public void testYield_should_return_a_promise() {
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) new WhenProgress<>().defer().getPromise();
        assertNotNull(p.yield(null));
    }

    @Test
    public void testYield_should_fulfill_with_the_supplied_value() {

        Done<Object, Object> done = new Done<>();
        WhenProgress<Object, Object> when = new WhenProgress<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.resolve(other);

        p.yield(sentinel).then(
                new Runnable<ProgressPromise<Object, Object>, Object>() {
                    @Override
                    public ProgressPromise<Object, Object> run(Object value) {
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
        WhenProgress<Object, Object> when = new WhenProgress<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.resolve(other);

        p.yield(when.resolve(sentinel)).then(
                new Runnable<ProgressPromise<Object, Object>, Object>() {
                    @Override
                    public ProgressPromise<Object, Object> run(Object value) {
                        assertEquals(sentinel, value);
                        return null;
                    }
                }).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testYield_should_reject_with_the_reason_of_a_rejected_promise() {

        Done<Object, Object> done = new Done<>();
        WhenProgress<Object, Object> when = new WhenProgress<>();
        PromiseExt<Object, Object> p = (PromiseExt<Object, Object>) when.resolve(other);

        p.yield(when.reject(sentinel)).then(
                fail2.onSuccess,
                new Runnable<ProgressPromise<Object, Object>, Value<Object>>() {
                    @Override
                    public ProgressPromise<Object, Object> run(Value<Object> reason) {
                        assertEquals(sentinel, reason.getValue());
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
