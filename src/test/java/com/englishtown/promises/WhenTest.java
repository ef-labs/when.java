package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhenTest {

    private Fail<Integer, Integer> fail = new Fail<>();
    private Fail<Boolean, Integer> fail2 = new Fail<>();

    private FakePromise<Integer, Integer> fakePromise = new FakePromise<>();

    private class FakePromise<TResolve, TProgress> implements Promise<TResolve, TProgress> {

        private TResolve value;

        private FakePromise() {
        }

        private FakePromise(TResolve value) {
            this.value = value;
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
        public Promise<TResolve, TProgress> then(Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled, Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected, Runnable<TProgress, TProgress> onProgress) {
            if (onFulfilled != null) {
                onFulfilled.run(this.value);
            }
            return this;
        }
    }

    private class Constant<T> implements Runnable<Promise<T, Integer>, T> {

        private T value;

        private Constant(T value) {
            this.value = value;
        }

        @Override
        public Promise<T, Integer> run(T val) {
            When<T, Integer> w = new When<>();
            return w.resolve(this.value);
        }
    }

    @Test
    public void whenTest_should_return_a_promise_for_a_value() {
        Promise<Integer, Integer> result = new When<Integer, Integer>().when(1);
        assertNotNull(result);
    }

    @Test
    public void whenTest_should_return_a_promise_for_a_promise() {
        Promise<Integer, Integer> result = new When<Integer, Integer>().when(fakePromise);
        assertNotNull(result);
    }

    @Test
    public void whenTest_should_not_return_the_input_promise() {
        Promise<Integer, Integer> result = new When<Integer, Integer>().when(fakePromise);
        assertNotSame(fakePromise, result);
    }

    @Test
    public void whenTest_should_return_a_promise_that_forwards_for_a_value() {

        Done<Integer, Integer> done = new Done<>();
        Promise<Integer, Integer> result = new When<Integer, Integer>().when(1, new Constant<>(2));

        assertNotNull(result);
        result.then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
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
        Deferred<Boolean, Integer> d1;
        Promise<Boolean, Integer> result;
        final When<Boolean, Integer> w1 = new When<>();

        d1 = w1.defer();
        d1.getResolver().resolve(false);

        final Runnable<Promise<Boolean, Integer>, Boolean> identity = new Runnable<Promise<Boolean, Integer>, Boolean>() {
            @Override
            public Promise<Boolean, Integer> run(Boolean value) {
                return w1.resolve(value);
            }
        };

        result = w1.when(w1.when(d1.getPromise().then(new Runnable<Promise<Boolean, Integer>, Boolean>() {
            @Override
            public Promise<Boolean, Integer> run(Boolean value) {

                Deferred<Boolean, Integer> d2 = w1.defer();
                d2.getResolver().resolve(value);

                return w1.when(d2.getPromise().then(identity), identity).then(
                        new Runnable<Promise<Boolean, Integer>, Boolean>() {
                            @Override
                            public Promise<Boolean, Integer> run(Boolean value) {
                                return w1.resolve(!value);
                            }
                        });
            }
        })));

        result.then(
                new Runnable<Promise<Boolean, Integer>, Boolean>() {
                    @Override
                    public Promise<Boolean, Integer> run(Boolean value) {
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
        When<Boolean, Integer> when = new When<>();

        when.when(when.resolve(true)).then(
                new Runnable<Promise<Boolean, Integer>, Boolean>() {
                    @Override
                    public Promise<Boolean, Integer> run(Boolean value) {
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

        // unstrusted promise should never be returned by when()
        Promise<Integer, Integer> untrusted = new FakePromise<>();
        Promise<Integer, Integer> result = new When<Integer, Integer>().when(untrusted);

        assertNotSame(untrusted, result);
        assertFalse(result instanceof FakePromise);
    }

    @Test
    public void whenTest_should_assimilate_intermediate_promises_returned_by_callbacks() {

        Done<Integer, Integer> done = new Done<>();
        Promise<Integer, Integer> result;

        // untrusted promise returned by an intermediate
        // handler should be assimilated
        result = new When<Integer, Integer>().when(1,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        return new FakePromise<>(value + 1);
                    }
                },
                null, null
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

        assertFalse(result instanceof FakePromise);
        done.assertSuccess();
    }

    @Test
    public void whenTest_should_assimilate_intermediate_promises_and_forward_results() {

        Done<Integer, Integer> done = new Done<>();
        Promise<Integer, Integer> untrusted = new FakePromise<>(1);
        When<Integer, Integer> when = new When<>();

        Promise<Integer, Integer> result = when.when(untrusted, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return new FakePromise<>(value + 1);
            }
        }, null, null);

        assertNotSame(untrusted, result);
        assertFalse(result instanceof FakePromise);

        when.when(result,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return new FakePromise<>(value + 1);
                    }
                },
                null, null
        ).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(3, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

}
