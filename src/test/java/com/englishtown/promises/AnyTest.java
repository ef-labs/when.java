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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/2/13
 * Time: 5:42 AM
 */
public class AnyTest {

    private final Fail<Integer, Integer> fail = new Fail<>();
    private final Fail<Integer, Integer> fail2 = new Fail<>();

    @Test
    public void testAny_should_resolve_to_undefined_with_empty_input_array() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<ProgressPromise<Integer, Integer>> input = new ArrayList<>();

        when.any(input).then(new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                assertNull(value);
                return null;
            }
        }, fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_reject_with_null_input_array() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();

        when.any(null).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        fail.onSuccess.run(value);
                        return null;
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> value) {
                        assertNotNull(value.getCause());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_resolve_with_an_input_value() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.anyValues(input).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_resolve_with_a_promised_input_value() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.any(input).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_resolve_with_a_promised_input_value_if_any_resolve() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.reject(1), when.reject(2), when.resolve(3));

        when.any(input).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(3, value.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_reject_with_all_rejected_input_values_if_all_inputs_are_rejected() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.reject(1), when.reject(2), when.reject(3));

        when.any(input).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        fail.onSuccess.run(value);
                        return null;
                    }
                },
                new Runnable<ProgressPromise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Value<Integer> result) {
                        assertEquals(1, result.getValue().intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertFailed();
    }

    @Test
    public void testAny_should_resolve_when_first_input_promise_resolves() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.reject(3));

        when.any(input).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_notify_progress() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        DeferredProgress<Integer, Integer> d1 = when.defer();
        DeferredProgress<Integer, Integer> d2 = when.defer();
        DeferredProgress<Integer, Integer> d3 = when.defer();
        final Done<List<Integer>, Integer> done = new Done<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(d1.getPromise(), d2.getPromise(), d3.getPromise());

        final int expected = 5;

        when.any(input).then(
                fail2.onSuccess,
                fail.onFail,
                new Runnable<Value<Integer>, Value<Integer>>() {
                    @Override
                    public Value<Integer> run(Value<Integer> value) {
                        assertEquals(expected, value.getValue().intValue());
                        done.success = true;
                        return null;
                    }
                });

        done.success = false;
        d1.getResolver().notify(expected);
        done.assertSuccess();

        done.success = false;
        d2.getResolver().notify(expected);
        done.assertSuccess();

        done.success = false;
        d3.getResolver().notify(expected);
        done.assertSuccess();
    }

// Not relevant for Java with strong typing
//            'should throw if called with something other than a valid input plus callbacks': function() {
//        assert.exception(function() {
//            when.any(1, 2, 3);
//        });
//    },

//    @Test
//    public void testAny_should_accept_a_promise_for_an_array() {
//
//        WhenProgress<Integer, Integer> when = new WhenProgress<>();
//        Done<List<Integer>, Integer> done = new Done<>();
//
//        List<Integer> expected = Arrays.asList(1, 2, 3);
//        WhenProgress<List<Integer>, Integer> w1 = new WhenProgress<>();
//        ProgressPromise<List<Integer>, Integer> input = w1.resolve(expected);
//
//        when.anyPromise(input,
//                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
//                    @Override
//                    public ProgressPromise<Integer, Integer> run(Integer result) {
//                        assertEquals(1, result.intValue());
//                        return null;
//                    }
//                },
//                fail.onFail).then(done.onSuccess, done.onFail);
//
//        done.assertSuccess();
//    }

    @Test
    public void testAny_should_allow_zero_handlers() {


        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<Integer, Integer> done = new Done<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.anyValues(input).then(
                new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
                    @Override
                    public ProgressPromise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

// Not relevant for Java with strong typing
//            'should resolve to undefined when input promise does not resolve to array': function(done) {
//        when.any(resolved(1),
//                function(result) {
//            refute.defined(result);
//        },
//        fail
//        ).always(done);
//    }

}
