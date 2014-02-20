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

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 6:02 PM
 *
 */
public class ResolveTest {

    private final Fail<Integer, Integer> fail = new Fail<>();
    private final Fail<Object, Object> fail2 = new Fail<>();
    private final Object sentinel = new Object();
//    private final Object other = new Object();

    @Test
    public void testResolve_should_resolve_an_immediate_value() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.resolve(expected).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(expected, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testResolve_should_resolve_a_resolved_promise() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        DeferredProgress<Integer, Integer> d = when.defer();
        d.getResolver().resolve(expected);

        when.resolvePromise(d.getPromise()).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(expected, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testResolve_should_reject_a_rejected_promise() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        DeferredProgress<Integer, Integer> d = when.defer();
        d.getResolver().reject(expected);

        when.resolvePromise(d.getPromise()).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(expected, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testResolve_when_assimilating_untrusted_thenables_should_trap_exceptions_during_assimilation() {

        WhenProgress<Object, Object> when = new WhenProgress<>();
        Done<Object, Object> done = new Done<>();
        final RuntimeException err = new RuntimeException();

        when.resolvePromise(new Thenable<Object, Object>() {
            @Override
            public ProgressPromise<Object, Object> then(Runnable<? extends ProgressPromise<Object, Object>, Object> onFulfilled, Runnable<? extends ProgressPromise<Object, Object>, Value<Object>> onRejected, Runnable<Value<Object>, Value<Object>> onProgress) {
                throw err;
            }
        }).then(
                fail2.onSuccess,
                new Runnable<ProgressPromise<Object, Object>, Value<Object>>() {
                    @Override
                    public ProgressPromise<Object, Object> run(Value<Object> value) {
                        assertEquals(err, value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testResolve_when_assimilating_untrusted_thenables_should_ignore_exceptions_after_fulfillment() {

        WhenProgress<Object, Object> when = new WhenProgress<>();
        Done<Object, Object> done = new Done<>();

        when.resolvePromise(new Thenable<Object, Object>() {
            @Override
            public ProgressPromise<Object, Object> then(Runnable<? extends ProgressPromise<Object, Object>, Object> onFulfilled, Runnable<? extends ProgressPromise<Object, Object>, Value<Object>> onRejected, Runnable<Value<Object>, Value<Object>> onProgress) {
                onFulfilled.run(sentinel);
                throw new RuntimeException();
            }
        }
        ).then(
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
    public void testResolve_when_assimilating_untrusted_thenables_should_ignore_exceptions_after_rejection() {

        WhenProgress<Object, Object> when = new WhenProgress<>();
        Done<Object, Object> done = new Done<>();

        when.resolvePromise(new Thenable<Object, Object>() {
            @Override
            public ProgressPromise<Object, Object> then(Runnable<? extends ProgressPromise<Object, Object>, Object> onFulfilled, Runnable<? extends ProgressPromise<Object, Object>, Value<Object>> onRejected, Runnable<Value<Object>, Value<Object>> onProgress) {
                onRejected.run(new Value<>(sentinel));
                throw new RuntimeException();
            }
        }
        ).then(
                fail2.onSuccess,
                new Runnable<ProgressPromise<Object, Object>, Value<Object>>() {
                    @Override
                    public ProgressPromise<Object, Object> run(Value<Object> value) {
                        assertEquals(sentinel, value.value);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

// Cannot do in Java with strong typing, onFulfilled must be called with TResolve
//    @Test
//    public void testResolve_when_assimilating_untrusted_thenables_should_assimilate_thenable_used_as_fulfillment_value() {
//        when.resolve({
//                then:function(onFulfilled) {
//            onFulfilled({
//                    then:function(onFulfilled) {
//                onFulfilled(sentinel);
//            }
//            });
//            throw other;
//        }
//        }).then(
//                function(val) {
//            assert.same(val, sentinel);
//        },
//        fail
//        ).always(done);
//    }

}
