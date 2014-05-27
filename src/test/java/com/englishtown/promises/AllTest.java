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
 * Date: 2/4/13
 * Time: 3:15 PM
 */
public class AllTest {

    private final Fail<List<? extends Integer>, Integer> fail = new Fail<>();

    @Test
    public void testAll_should_resolve_empty_input() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        List<ProgressPromise<Integer, Integer>> input = new ArrayList<>();

        when.all(input).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> result) {
                        assertArrayEquals(new Integer[0], result.toArray(new Integer[result.size()]));
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testAll_should_reject_null_input() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.all(null).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, Value<List<? extends Integer>>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(Value<List<? extends Integer>> value) {
                        assertNotNull(value.getCause());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testAll_should_resolve_values_array() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        final List<Integer> input = Arrays.asList(1, 2, 3);

        when.allValues(input).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        assertArrayEquals(input.toArray(new Integer[3]), results.toArray(new Integer[results.size()]));
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testAll_should_resolve_promises_array() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.all(input).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        Integer[] expected = {1, 2, 3};
                        assertArrayEquals(expected, results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAll_should_resolve_sparse_array_input() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        final List<Integer> input = Arrays.asList(null, 1, null, 1, 1);

        when.allValues(input).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        Integer[] expected = input.toArray(new Integer[5]);
                        assertArrayEquals(expected, results.toArray(new Integer[results.size()]));
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAll_should_reject_if_any_input_promise_rejects() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.resolve(3));

        when.all(input).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, Value<List<? extends Integer>>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(Value<List<? extends Integer>> failed) {
                        assertEquals(2, failed.getValue().get(0).intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

//    @Test
//    public void testAll_should_notify_progress() {
//
//        WhenProgress<Integer, Integer> when = new WhenProgress<>();
//        DeferredProgress<Integer, Integer> d1 = when.defer();
//        DeferredProgress<Integer, Integer> d2 = when.defer();
//        DeferredProgress<Integer, Integer> d3 = when.defer();
//        final Done<List<Integer>, Integer> done = new Done<>();
//        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(d1.getPromise(), d2.getPromise(), d3.getPromise());
//
//        final int expected = 5;
//
//        when.all(input,
//                fail.onSuccess,
//                fail.onFail,
//                new Runnable<Value<Integer>, Value<Integer>>() {
//                    @Override
//                    public Value<Integer> run(Value<Integer> value) {
//                        assertEquals(expected, value.getValue().intValue());
//                        done.success = true;
//                        return null;
//                    }
//                });
//
//        done.success = false;
//        d1.getResolver().progress(expected);
//        done.assertSuccess();
//
//        done.success = false;
//        d2.getResolver().progress(expected);
//        done.assertSuccess();
//
//        done.success = false;
//        d3.getResolver().progress(expected);
//        done.assertSuccess();
//    }

//            'should throw if called with something other than a valid input plus callbacks': function() {
//        assert.exception(function() {
//            when.all(1, 2, 3);
//        });
//    },

//    @Test
//    public void testAll_should_accept_a_promise_for_an_array() {
//
//        Done<List<Integer>, Integer> done = new Done<>();
//        WhenProgress<Integer, Integer> when = new WhenProgress<>();
//        WhenProgress<List<Integer>, Integer> w1 = new WhenProgress<>();
//
//        final List<Integer> expected = Arrays.asList(1, 2, 3);
//        ProgressPromise<List<Integer>, Integer> input = w1.resolve(expected);
//
//        when.allPromise(input,
//                new Runnable<ProgressPromise<List<Integer>, Integer>, List<Integer>>() {
//                    @Override
//                    public ProgressPromise<List<Integer>, Integer> run(List<Integer> results) {
//                        assertArrayEquals(expected.toArray(new Integer[3]), results.toArray(new Integer[results.size()]));
//                        return null;
//                    }
//                },
//                fail.onFail,
//                null
//        ).then(done.onSuccess, done.onFail);
//
//        done.assertSuccess();
//    }

//            'should resolve to empty array when input promise does not resolve to array': function(done) {
//        when.all(resolved(1),
//                function(result) {
//            assert.equals(result, []);
//        },
//        fail
//        ).always(done);
//    }
//
}
