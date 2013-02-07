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
 * Time: 3:06 AM
 * To change this template use File | Settings | File Templates.
 */
public class SomeTest {

    private Fail<List<Integer>, Integer> fail = new Fail<>();

    @Test
    public void testSome_should_resolve_empty_input() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = new ArrayList<>();

        when.somePromises(input, 1,
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
    public void testSome_should_reject_null_input() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = null;

        when.somePromises(input, 1,
                fail.onSuccess,
                fail.onFail
        ).then(
                fail.onSuccess,
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
    public void testSome_should_resolve_values_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        final List<Integer> input = Arrays.asList(1, 2, 3);

        when.some(input, 2,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        Integer[] expected = {1, 2};
                        assertNotNull(results);
                        assertArrayEquals(expected, results.toArray(new Integer[2]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testSome_should_resolve_promises_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.somePromises(input, 2,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        Integer[] expected = {1, 2};
                        assertNotNull(results);
                        assertArrayEquals(expected, results.toArray(new Integer[2]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testSome_should_resolve_sparse_array_input() {

        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        final List<Integer> input = Arrays.asList(null, 1, null, 2, 3);

        when.some(input, 2,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        Integer[] expected = {1, 2};
                        assertNotNull(results);
                        assertArrayEquals(expected, results.toArray(new Integer[2]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testSome_should_reject_with_all_rejected_input_values_if_resolving_howMany_becomes_impossible() {


        Done<List<Integer>, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.reject(3));

        when.somePromises(input, 2,
                fail.onSuccess,
                new Runnable<Promise<List<Integer>, Integer>, Value<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Value<List<Integer>> failed) {
                        Integer[] expected = {2, 3};
                        assertNotNull(failed);
                        assertArrayEquals(expected, failed.data.toArray(new Integer[2]));
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertFailed();

    }

//            'should throw if called with something other than a valid input, count, and callbacks': function() {
//        assert.exception(function() {
//            when.some(1, 2, 3, 2);
//        });
//    },

    @Test
    public void testSome_should_accept_a_promise_for_an_array() {

        When<Integer, Integer> when = new When<>();
        When<List<Integer>, Integer> w1 = new When<>();
        final List<Integer> expected = Arrays.asList(1, 2, 3);
        Promise<List<Integer>, Integer> input = w1.resolve(expected);
        Done<List<Integer>, Integer> done = new Done<>();

        when.somePromise(input, 2,
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
                        Integer[] slice = expected.subList(0, 2).toArray(new Integer[2]);
                        assertArrayEquals(slice, results.toArray(new Integer[2]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

// Java is strongly typed so unit test is not relevant
//            'should resolve to empty array when input promise does not resolve to array': function(done) {
//        when.some(resolved(1), 1,
//                function(result) {
//            assert.equals(result, []);
//        },
//        fail
//        ).always(done);
//    }
//

}
