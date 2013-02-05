package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/5/13
 * Time: 4:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReduceTest {

    private Fail<Integer, Integer> fail = new Fail<>();
    private Fail<String, Integer> fail2 = new Fail<>();

    private Reducer2<Integer> plus = new Reducer2<Integer>() {
        @Override
        public Integer run(Integer previousValue, Integer currentValue, int currentIndex, int total) {
            return previousValue + currentValue;
        }
    };

    Reducer2<String> plus2 = new Reducer2<String>() {
        @Override
        public String run(String previousValue, String currentValue, int currentIndex, int total) {
            return previousValue + currentValue;
        }
    };


    @Test
    public void testReduce_should_reduce_values_without_initial_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.reduce(input, plus).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(6, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_values_with_initial_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.reduce(input, plus, 1).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(7, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_values_with_initial_promise() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.reduce(input, plus, when.resolve(1)).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(7, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_promised_values_without_initial_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.reducePromises(input, plus).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(6, result.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

    @Test
    public void testReduce_should_reduce_promised_values_with_initial_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.reducePromises(input, plus, 1).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(7, result.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_promised_values_with_initial_promise() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.reducePromises(input, plus, when.resolve(1)).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(7, result.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_empty_input_with_initial_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = new ArrayList<>();

        when.reduce(input, plus, 1).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_empty_input_with_initial_promise() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = new ArrayList<>();

        when.reduce(input, plus, when.resolve(1)).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reject_when_input_contains_rejection() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.resolve(3));

        when.reducePromises(input, plus, when.resolve(1)).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> result) {
                        assertEquals(2, result.data.intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reject_with_TypeError_when_input_is_empty_and_no_initial_value_or_promise_provided() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = new ArrayList<>();

        when.reducePromises(input, plus).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_allow_sparse_array_input_without_initial() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = Arrays.asList(null, null, 1, null, 1, 1);

        when.reduce(input, plus).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(3, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_allow_sparse_array_input_with_initial() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        List<Integer> input = Arrays.asList(null, null, 1, null, 1, 1);

        when.reduce(input, plus, 1).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(4, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_reduce_in_input_order() {

        Done<String, Integer> done = new Done<>();
        When<String, Integer> when = new When<>();
        Deferred<String, Integer> d1 = When.defer();
        Deferred<String, Integer> d2 = When.defer();
        Deferred<String, Integer> d3 = When.defer();
        List<Promise<String, Integer>> input = Arrays.asList(d1.getPromise(), d2.getPromise(), d3.getPromise());

        when.reducePromises(input, plus2, "").then(
                new Runnable<Promise<String, Integer>, String>() {
                    @Override
                    public Promise<String, Integer> run(String value) {
                        assertEquals("123", value);
                        return null;
                    }
                },
                fail2.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        d3.getResolver().resolve("3");
        d1.getResolver().resolve("1");
        d2.getResolver().resolve("2");

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_accept_a_promise_for_an_array() {

        Done<String, Integer> done = new Done<>();
        When<String, Integer> when = new When<>();

        When<List<String>, Integer> w1 = new When<>();
        List<String> input = Arrays.asList("1", "2", "3");
        Promise<List<String>, Integer> promise = w1.resolve(input);

        when.reducePromise(promise, plus2, "").then(
                new Runnable<Promise<String, Integer>, String>() {
                    @Override
                    public Promise<String, Integer> run(String value) {
                        assertEquals("123", value);
                        return null;
                    }
                },
                fail2.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReduce_should_accept_a_promise_for_an_array_and_reject() {

        Done<String, Integer> done = new Done<>();
        When<String, Integer> when = new When<>();

        Deferred<List<String>, Integer> d1 = When.defer();

        when.reducePromise(d1.getPromise(), plus2, "").then(
                fail2.onSuccess,
                new Runnable<Promise<String, Integer>, Reason<String>>() {
                    @Override
                    public Promise<String, Integer> run(Reason<String> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        d1.getResolver().reject(new Reason<List<String>>(null, new RuntimeException()));

        done.assertSuccess();

    }

// Not relevant in Java with strong typing
//            'should resolve to initialValue when input promise does not resolve to an array': function(done) {
//        when.reduce(resolved(123), plus, 1).then(
//                function(result) {
//            assert.equals(result, 1);
//        },
//        fail
//        ).always(done);
//    },
//
//            'should provide correct basis value': function(done) {
//        function insertIntoArray(arr, val, i) {
//            arr[i] = val;
//            return arr;
//        }
//
//        when.reduce([later(1), later(2), later(3)], insertIntoArray, []).then(
//                function(result) {
//            assert.equals(result, [1,2,3]);
//        },
//        fail
//        ).always(done);
//    }
//
}
