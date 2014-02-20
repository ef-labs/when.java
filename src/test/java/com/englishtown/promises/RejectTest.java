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
 * Time: 5:55 PM
 *
 */
public class RejectTest {

    private final Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testReject_should_reject_an_immediate_value() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.reject(expected).then(
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
    public void testReject_should_reject_an_immediate_value_2() {
        final Value<Integer> expected = new Value<>(123);

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.reject(expected).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(expected.value.intValue(), value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testReject_should_reject_an_immediate_value_3() {
        final Throwable error = new RuntimeException();

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.reject(error).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(error, value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testReject_should_reject_a_resolved_promise() {

        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        DeferredProgress<Integer, Integer> d = when.defer();
        d.getResolver().resolve(expected);

        when.reject(d.getPromise()).then(
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
    public void testReject_should_reject_a_rejected_promise() {

        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        DeferredProgress<Integer, Integer> d = when.defer();
        d.getResolver().reject(expected);

        when.reject(d.getPromise()).then(
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

}
