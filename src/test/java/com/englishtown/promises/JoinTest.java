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

import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 4:32 PM
 *
 */
public class JoinTest {

    private final Fail<List<? extends Integer>, Integer> fail = new Fail<>();

    @Test
    public void testJoin_should_resolve_empty_input_promises() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();
        ProgressPromise<Integer, Integer>[] input = null;

        when.join(input).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        Integer[] expected = {};
                        assertArrayEquals(expected, results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testJoin_should_resolve_empty_input() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();
        Integer[] input = null;

        when.join(input).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        Integer[] expected = {};
                        assertArrayEquals(expected, results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testJoin_should_join_values() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        when.join(1, 2, 3).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        Integer[] expected = {1, 2, 3};
                        assertArrayEquals(expected, results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testJoin_should_join_promises_array() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        when.join(when.resolve(1), when.resolve(2), when.resolve(3)).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> results) {
                        Integer[] expected = {1, 2, 3};
                        assertArrayEquals(expected, results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

//            'should join mixed array': function(done) {
//        when.join(resolved(1), 2, resolved(3), 4).then(
//                function(results) {
//            assert.equals(results, [1, 2, 3, 4]);
//        },
//        fail
//        ).always(done);
//    },

    @Test
    public void testJoin_should_reject_if_any_input_promise_rejects() {

        WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        when.join(when.resolve(1), when.reject(2), when.resolve(3)).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, Value<List<? extends Integer>>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(Value<List<? extends Integer>> failed) {
                        assertEquals(2, failed.value.get(0).intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

}
