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
 * To change this template use File | Settings | File Templates.
 */
public class AllTest {

    private Fail<List<Integer>, Integer> fail = new Fail<>();

    @Test
    public void testAll_should_resolve_empty_input() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = new ArrayList<>();

        when.allPromises(input,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> result) {
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

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        when.allPromises(null, fail.onSuccess,
                new Runnable<Promise<List<Integer>, Integer>, Value<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Value<List<Integer>> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testAll_should_resolve_values_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        final List<Integer> input = Arrays.asList(1, 2, 3);

        when.all(input,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        assertArrayEquals(input.toArray(new Integer[3]), results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testAll_should_resolve_promises_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.allPromises(input,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
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

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        final List<Integer> input = Arrays.asList(null, 1, null, 1, 1);

        when.all(input,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        Integer[] expected = input.toArray(new Integer[5]);
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
    public void testAll_should_reject_if_any_input_promise_rejects() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.resolve(3));

        when.allPromises(input,
                fail.onSuccess,
                new Runnable<Promise<List<Integer>, Integer>, Value<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Value<List<Integer>> failed) {
                        assertEquals(2, failed.data.get(0).intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

//            'should throw if called with something other than a valid input plus callbacks': function() {
//        assert.exception(function() {
//            when.all(1, 2, 3);
//        });
//    },

    @Test
    public void testAll_should_accept_a_promise_for_an_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        When<List<Integer>, Integer> w1 = new When<>();

        final List<Integer> expected = Arrays.asList(1, 2, 3);
        Promise<List<Integer>, Integer> input = w1.resolve(expected);

        when.allPromise(input,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        assertArrayEquals(expected.toArray(new Integer[3]), results.toArray(new Integer[results.size()]));
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

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
