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
 * Date: 2/13/13
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class SequenceTest {

    private Fail<List<Integer>, Integer> fail = new Fail<>();

    private Runnable<Promise<Integer, Integer>, Void> createTask(final int y) {
        return new Runnable<Promise<Integer, Integer>, Void>() {
            @Override
            public Promise<Integer, Integer> run(Void value) {
                When<Integer, Integer> w = new When<>();
                return w.resolve(y);
            }
        };
    }

    private Runnable<Promise<Integer, Integer>, Void> createTask(final Promise<Integer, Integer> promise) {
        return new Runnable<Promise<Integer, Integer>, Void>() {
            @Override
            public Promise<Integer, Integer> run(Void value) {
                return promise;
            }
        };
    }

    private Runnable<Promise<Integer, Integer>, int[]> expectArgs(final int[] expected) {
        return new Runnable<Promise<Integer, Integer>, int[]>() {
            @Override
            public Promise<Integer, Integer> run(int[] value) {
                assertEquals(expected, value);
                return null;
            }
        };
    }

    @Test
    public void testSequence_should_execute_tasks_in_order() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Runnable<Promise<Integer, Integer>, Void>> tasks =
                Arrays.asList(
                        createTask(1),
                        createTask(2),
                        createTask(3));

        when.sequence(tasks).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        Integer[] expected = {1, 2, 3};
                        assertArrayEquals(expected, value.toArray(new Integer[value.size()]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testSequence_should_resolve_to_empty_array_when_no_tasks_supplied() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Runnable<Promise<Integer, Integer>, Void>> tasks = new ArrayList<>();

        when.sequence(tasks).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        assertNotNull(value);
                        assertEquals(0, value.size());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testSequence_should_pass_args_to_all_tasks() {


        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        int[] expected = {1, 2, 3};

        List<Runnable<Promise<Integer, Integer>, int[]>> tasks =
                Arrays.asList(
                        expectArgs(expected),
                        expectArgs(expected),
                        expectArgs(expected));

        when.sequence(tasks, expected).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testSequence_should_execute_delayed_tasks_in_order() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d1 = when.defer();
        Deferred<Integer, Integer> d2 = when.defer();
        Deferred<Integer, Integer> d3 = when.defer();
        List<Runnable<Promise<Integer, Integer>, Void>> tasks =
                Arrays.asList(
                        createTask(d1.getPromise()),
                        createTask(d2.getPromise()),
                        createTask(d3.getPromise()));

        when.sequence(tasks).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        Integer[] expected = {1, 2, 3};
                        assertArrayEquals(expected, value.toArray(new Integer[value.size()]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        d1.getResolver().resolve(1);
        d3.getResolver().resolve(3);
        d2.getResolver().resolve(2);

        done.assertSuccess();
    }

}
