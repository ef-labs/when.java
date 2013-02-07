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
 * To change this template use File | Settings | File Templates.
 */
public class AnyTest {

    private Fail<List<Integer>, Integer> fail = new Fail<>();

    @Test
    public void testAny_should_resolve_to_undefined_with_empty_input_array() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Promise<Integer, Integer>> input = new ArrayList<>();

        when.anyPromises(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                assertNull(value);
                return null;
            }
        }, fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_reject_with_null_input_array() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.any(null,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        fail.onSuccess.run(Arrays.asList(value));
                        return null;
                    }
                },
                fail.onFail
        ).then(
                fail.onSuccess,
                new Runnable<Promise<List<Integer>, Integer>, Value<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Value<List<Integer>> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                }).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_resolve_with_an_input_value() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.any(input,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_resolve_with_a_promised_input_value() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.anyPromises(input,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_resolve_with_a_promised_input_value_if_any_resolve() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.reject(1), when.reject(2), when.resolve(3));

        when.anyPromises(input,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(3, value.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_reject_with_all_rejected_input_values_if_all_inputs_are_rejected() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.reject(1), when.reject(2), when.reject(3));

        when.anyPromises(input,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        fail.onSuccess.run(Arrays.asList(value));
                        return null;
                    }
                },
                new Runnable<Promise<List<Integer>, Integer>, Value<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Value<List<Integer>> result) {
                        Integer[] expected = {1, 2, 3};
                        assertArrayEquals(expected, result.data.toArray());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertFailed();
    }

    @Test
    public void testAny_should_resolve_when_first_input_promise_resolves() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.reject(3));

        when.anyPromises(input,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

// Not relevant for Java with strong typing
//            'should throw if called with something other than a valid input plus callbacks': function() {
//        assert.exception(function() {
//            when.any(1, 2, 3);
//        });
//    },

    @Test
    public void testAny_should_accept_a_promise_for_an_array() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        List<Integer> expected = Arrays.asList(1, 2, 3);
        When<List<Integer>, Integer> w1 = new When<>();
        Promise<List<Integer>, Integer> input = w1.resolve(expected);

        when.anyPromise(input,
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer result) {
                        assertEquals(1, result.intValue());
                        return null;
                    }
                },
                fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testAny_should_allow_zero_handlers() {


        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();
        List<Integer> input = Arrays.asList(1, 2, 3);

        when.any(input, null).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        assertEquals(1, value.size());
                        assertEquals(1, value.get(0).intValue());
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
