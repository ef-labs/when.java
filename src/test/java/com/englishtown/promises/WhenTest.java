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

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 6:18 PM
 */
public class WhenTest {

    private final Fail<Integer, Integer> fail = new Fail<>();
    private final Fail<Boolean, Integer> fail2 = new Fail<>();

    private final FakePromise<Integer, Integer> fakePromise = new FakePromise<>();

    private class FakePromise<TResolve, TProgress> implements ProgressPromise<TResolve, TProgress> {

        private TResolve value;

        private FakePromise() {
        }

        private FakePromise(TResolve value) {
            this.value = value;
        }

        @Override
        public ProgressPromise<TResolve, TProgress> then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled) {
            return then(onFulfilled, null, null);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected) {
            return then(onFulfilled, onRejected, null);
        }

        @Override
        public ProgressPromise<TResolve, TProgress> then(Runnable<? extends ProgressPromise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<? extends ProgressPromise<TResolve, TProgress>, Value<TResolve>> onRejected, Runnable<Value<TProgress>, Value<TProgress>> onProgress) {
            if (onFulfilled != null) {
                onFulfilled.run(this.value);
            }
            return this;
        }
    }

    private class Constant<T> implements Runnable<ProgressPromise<T, Integer>, T> {

        private final T value;

        private Constant(T value) {
            this.value = value;
        }

        @Override
        public ProgressPromise<T, Integer> run(T val) {
            WhenProgress<T, Integer> w = new WhenProgress<>();
            return w.resolve(this.value);
        }
    }

    @Test
    public void whenTest_should_return_a_promise_for_a_value() {
        ProgressPromise<Integer, Integer> result = new WhenProgress<Integer, Integer>().when(1);
        assertNotNull(result);
    }

    @Test
    public void whenTest_should_return_a_promise_for_a_promise() {
        ProgressPromise<Integer, Integer> result = new WhenProgress<Integer, Integer>().when(fakePromise);
        assertNotNull(result);
    }

    @Test
    public void whenTest_should_not_return_the_input_promise() {
        ProgressPromise<Integer, Integer> result = new WhenProgress<Integer, Integer>().when(fakePromise);
        assertNotSame(fakePromise, result);
    }

    @Test
    public void whenTest_should_return_a_promise_that_forwards_for_a_value() {

        Done<Integer, Integer> done = new Done<>();
        ProgressPromise<Integer, Integer> result = new WhenProgress<Integer, Integer>().when(1, new Constant<>(2));

        assertNotNull(result);
        result.then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void whenTest_should_support_deep_nesting_in_promise_chains() {

        Done<Boolean, Integer> done = new Done<>();
        DeferredProgress<Boolean, Integer> d1;
        ProgressPromise<Boolean, Integer> result;
        final WhenProgress<Boolean, Integer> w1 = new WhenProgress<>();

        d1 = w1.defer();
        d1.getResolver().resolve(false);

        final Runnable<ProgressPromise<Boolean, Integer>, Boolean> identity = new Runnable<ProgressPromise<Boolean, Integer>, Boolean>() {
            @Override
            public ProgressPromise<Boolean, Integer> run(Boolean value) {
                return w1.resolve(value);
            }
        };

        result = w1.when(w1.when(d1.getPromise().then(new Runnable<ProgressPromise<Boolean, Integer>, Boolean>() {
            @Override
            public ProgressPromise<Boolean, Integer> run(Boolean value) {

                DeferredProgress<Boolean, Integer> d2 = w1.defer();
                d2.getResolver().resolve(value);

                return w1.when(d2.getPromise().then(identity), identity).then(
                        new Runnable<ProgressPromise<Boolean, Integer>, Boolean>() {
                            @Override
                            public ProgressPromise<Boolean, Integer> run(Boolean value) {
                                return w1.resolve(!value);
                            }
                        });
            }
        })));

        result.then(
                new Runnable<ProgressPromise<Boolean, Integer>, Boolean>() {
                    @Override
                    public ProgressPromise<Boolean, Integer> run(Boolean value) {
                        assertTrue(value);
                        return null;
                    }
                },
                fail2.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void whenTest_should_return_a_resolved_promise_for_a_resolved_input_promise() {

        Done<Boolean, Integer> done = new Done<>();
        WhenProgress<Boolean, Integer> when = new WhenProgress<>();

        when.when(when.resolve(true)).then(
                new Runnable<ProgressPromise<Boolean, Integer>, Boolean>() {
                    @Override
                    public ProgressPromise<Boolean, Integer> run(Boolean value) {
                        assertTrue(value);
                        return null;
                    }
                },
                fail2.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void whenTest_should_assimilate_untrusted_promises() {

        // untrusted promise should never be returned by when()
        ProgressPromise<Integer, Integer> untrusted = new FakePromise<>();
        ProgressPromise<Integer, Integer> result = new WhenProgress<Integer, Integer>().when(untrusted);

        assertNotSame(untrusted, result);
        assertFalse(result instanceof FakePromise);
    }

    @Test
    public void whenTest_should_assimilate_intermediate_promises_returned_by_callbacks() {

        Done<Integer, Integer> done = new Done<>();
        ProgressPromise<Integer, Integer> result;

        // untrusted promise returned by an intermediate
        // handler should be assimilated
        result = new WhenProgress<Integer, Integer>().when(1,
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        return new FakePromise<>(value + 1);
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

        assertFalse(result instanceof FakePromise);
        done.assertSuccess();
    }

    @Test
    public void whenTest_should_assimilate_intermediate_promises_and_forward_results() {

        Done<Integer, Integer> done = new Done<>();
        ProgressPromise<Integer, Integer> untrusted = new FakePromise<>(1);
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        ProgressPromise<Integer, Integer> result = when.when(untrusted, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return new FakePromise<>(value + 1);
            }
        }, null, null);

        assertNotSame(untrusted, result);
        assertFalse(result instanceof FakePromise);

        when.when(result,
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return new FakePromise<>(value + 1);
                    }
                },
                null, null
        ).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(3, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void whenTest_Promise() {

        When<Integer> when = new When<>();
        Deferred<Integer> d = when.defer();
        Done<Integer, Void> done = new Done<>();

        Promise<Integer> promise = d.getPromise();

        promise.then(
                new Runnable<ProgressPromise<Integer, Void>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Void> run(Integer value) {
                        assertEquals(10, value.intValue());
                        return null;
                    }
                },
                new Runnable<ProgressPromise<Integer, Void>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Void> run(Value<Integer> value) {
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        d.getResolver().resolve(10);
        done.assertSuccess();
    }

}
