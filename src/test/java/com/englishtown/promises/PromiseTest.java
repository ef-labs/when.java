package com.englishtown.promises;

import org.junit.Test;
import sun.jvm.hotspot.utilities.IntegerEnum;

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

    private Runnable<Promise<Integer, Integer>, Reason<Integer>> onFail = new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
        @Override
        public Promise<Integer, Integer> run(Reason<Integer> value) {
            return null;
        }
    };

    private Runnable<Integer, Integer> onProgress = new Runnable<Integer, Integer>() {
        @Override
        public Integer run(Integer value) {
            return value;
        }
    };

    private Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testPromise_should_return_a_promise() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(null));
    }

    @Test
    public void testPromise_should_allow_a_single_callback_function() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(onSuccess));
    }

    @Test
    public void testPromise_should_allow_a_callback_and_errback_function() {
        assertNotNull(new When<Integer, Integer>().defer().getPromise().then(onSuccess, onFail));
    }

    @Test
    public void testPromise_should_allow_a_callback_errback_and_progback_function() {
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

    @Test
    public void testPromise_should_allow_functions_and_null_or_undefined_to_be_mixed() {
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
                new Runnable<Promise<Date, Integer>, Reason<Date>>() {
                    @Override
                    public Promise<Date, Integer> run(Reason<Date> value) {
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
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
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
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(2, value.data.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_switch_from_callbacks_to_errbacks_when_callback_throws() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        throw new RuntimeException();
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(1);
        done.assertSuccess();

    }

    @Test
    public void testPromise_should_switch_from_errbacks_to_callbacks_when_errback_does_not_explicitly_propagate() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        When<Integer, Integer> w1 = new When<>();
                        return w1.resolve(value.data + 1);
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
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        Deferred<Integer, Integer> d1 = new When<Integer, Integer>().defer();
                        d1.getResolver().resolve(value.data + 1);
                        return d1.getPromise();
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
    public void testPromise_should_propagate_rejections_when_errback_throws() {

        Done<Integer, Integer> done = new Done<>();
        Deferred<Integer, Integer> d = new When<Integer, Integer>().defer();

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        throw new RuntimeException();
                    }
                }
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                }
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
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        Deferred<Integer, Integer> d1 = new When<Integer, Integer>().defer();
                        d1.getResolver().reject(value.data + 1);
                        return d1.getPromise();
                    }
                }
        ).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(2, value.data.intValue());
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

        d.getPromise().then(null, null, new Runnable<Object, Object>() {
            @Override
            public Object run(Object value) {
                assertEquals(expected, value);
                done.success = true;
                return null;
            }
        });

        d.getResolver().progress(expected);
        done.assertSuccess();

    }

// TODO: Port promise unit tests for always/otherwise/yield/spread
//            'always': {
//        'should return a promise': function() {
//            assert.isFunction(defer().promise.always().then);
//        },
//
//        'should register callback': function(done) {
//            var d = when.defer();
//
//            d.promise.always(
//                    function(val) {
//                assert.equals(val, 1);
//                done();
//            }
//            );
//
//            d.resolve(1);
//        },
//
//        'should register errback': function(done) {
//            var d = when.defer();
//
//            d.promise.always(
//                    function(val) {
//                assert.equals(val, 1);
//                done();
//            }
//            );
//
//            d.reject(1);
//        },
//
//        'should register progback': function(done) {
//            var d = when.defer();
//
//            d.promise.always(null, function (status) {
//                assert.equals(status, 1);
//                done();
//            });
//
//            d.progress(1);
//        }
//
//    },
//
//            'otherwise': {
//        'should return a promise': function() {
//            assert.isFunction(defer().promise.otherwise().then);
//        },
//
//        'should register errback': function(done) {
//            var d = when.defer();
//
//            d.promise.otherwise(
//                    function(val) {
//                assert.equals(val, 1);
//                done();
//            }
//            );
//
//            d.reject(1);
//        }
//    },
//
//            'yield': {
//        'should return a promise': function() {
//            assert.isFunction(defer().promise.yield().then);
//        },
//
//        'should fulfill with the supplied value': function(done) {
//            when.resolve(other).yield(sentinel).then(
//                    function(value) { assert.same(value, sentinel); }
//            ).always(done);
//        },
//
//        'should fulfill with the value of a fulfilled promise': function(done) {
//            when.resolve(other).yield(when.resolve(sentinel)).then(
//                    function(value) { assert.same(value, sentinel); }
//            ).always(done);
//        },
//
//        'should reject with the reason of a rejected promise': function(done) {
//            when.resolve(other).yield(when.reject(sentinel)).then(
//                    fail,
//                    function(reason) { assert.same(reason, sentinel); }
//            ).always(done);
//        }
//    },
//
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
