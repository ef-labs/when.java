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
 * Date: 2/2/13
 * Time: 6:32 AM
 *
 */
public class ChainTest {

    private final Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testChain_should_return_a_promise_for_an_input_value() {

        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Promise<Integer, Integer> result;

        d = when.defer();

        result = when.chain(when.resolve(1), d.getResolver());

        assertNotNull(result);
        assertNotSame(result, d);
        assertNotSame(result, d.getPromise());

    }

    @Test
    public void testChain_should_return_a_promise_for_an_input_promise() {

        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d1, d2;
        Promise<Integer, Integer> result;

        d1 = when.defer();
        d2 = when.defer();

        result = when.chain(d1.getPromise(), d2.getResolver());

        assertNotNull(result);
        assertNotSame(result, d1);
        assertNotSame(result, d1.getPromise());
        assertNotSame(result, d2);
        assertNotSame(result, d2.getPromise());

    }

    @Test
    public void testChain_should_resolve_resolver_with_input_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        when.chain(when.resolve(1), d.getResolver());
        done.assertSuccess();

    }

    @Test
    public void testChain_should_resolve_resolver_with_input_promise_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        input = when.defer();
        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver());
        done.assertSuccess();

    }

    @Test
    public void testChain_should_resolve_resolver_with_provided_value_when_input_is_a_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        when.chain(when.resolve(1), d.getResolver(), 2);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_resolve_resolver_with_provided_value_when_input_is_a_promise() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        input = when.defer();
        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), 2);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_reject_resolver_with_input_promise_rejection_reason() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(1, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        input = when.defer();
        input.getResolver().reject(1);

        when.chain(input.getPromise(), d.getResolver());
        done.assertSuccess();

    }

    @Test
    public void testChain_should_reject_resolver_with_input_promise_rejection_reason_when_optional_value_provided() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(1, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        input = when.defer();
        input.getResolver().reject(1);

        when.chain(input.getPromise(), d.getResolver(), 2);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_return_a_promise_that_resolves_with_the_input_promise_resolution_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver()).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testChain_should_return_a_promise_that_resolves_with_the_optional_resolution_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), 2).then(
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
    public void testChain_should_return_a_promise_that_resolves_with_the_optional_null_resolution_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), null).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertNull(value);
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testChain_should_return_a_promise_that_rejects_with_the_input_promise_rejection_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().reject(1);

        when.chain(input.getPromise(), d.getResolver()).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(1, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testChain_should_return_a_promise_that_forwards_progress_to_provided_resolver() {

        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;
        final Integer expected = 5;
        final Value<Boolean> success1 = new Value<>(false);
        final Value<Boolean> success2 = new Value<>(false);

        input = when.defer();
        d = when.defer();

        d.getPromise().then(null, null, new Runnable<Value<Integer>, Value<Integer>>() {
            @Override
            public Value<Integer> run(Value<Integer> progress) {
                assertEquals(expected, progress.value);
                success1.value = true;
                return progress;
            }
        });

        when.chain(input.getPromise(), d.getResolver()).then(
                fail.onSuccess,
                fail.onFail,
                new Runnable<Value<Integer>, Value<Integer>>() {
                    @Override
                    public Value<Integer> run(Value<Integer> progress) {
                        assertEquals(expected, progress.value);
                        success2.value = true;
                        return progress;
                    }
                }
        );

        input.getResolver().notify(expected);
        assertTrue(success1.value);
        assertTrue(success2.value);

    }

}
