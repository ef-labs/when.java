package com.englishtown.promises.integration;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Done;
import com.englishtown.promises.Fail;
import com.englishtown.promises.Promise;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Integration tests for when.reduce()
 */
public class ReduceTest extends AbstractIntegrationTest {

    private Done<Integer> done = new Done<>();
    private Fail<Integer> fail = new Fail<>();

    private Promise<Integer> plus(Integer sum, Integer val) {
        sum = (sum == null ? 0 : sum);
        val = (val == null ? 0 : val);
        return when.resolve(sum + val);
    }

    private Promise<String> concat(String sum, String val) {
        return when.resolve(sum + val);
    }

    @Test
    public void testReduce_should_reduce_promised_values_without_initial_value() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.reduce(input, this::plus)
                .<Integer>then(
                        result -> {
                            assertNotNull(result);
                            assertEquals(6, result.intValue());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testReduce_should_reduce_promised_values_with_initial_promise() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.reduce(input, this::plus, when.resolve(1))
                .<Integer>then(
                        result -> {
                            assertEquals(7, result.intValue());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testReduce_should_reduce_empty_input_with_initial_promise() throws Exception {

        List<Promise<Integer>> input = new ArrayList<>();

        when.reduce(input, this::plus, resolved(1))
                .<Integer>then(
                        result -> {
                            assertEquals(1, result.intValue());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testReduce_should_reject_when_input_contains_rejection() throws Exception {

        Throwable t = new RuntimeException();

        List<Promise<Integer>> input = Arrays.asList(when.resolve(1), when.reject(t), when.resolve(3));
        when.reduce(input, this::plus, when.resolve(1))
                .then(
                        fail.onFulfilled,
                        result -> {
                            assertEquals(t, result);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testReduce_should_reject_with_TypeError_when_input_is_empty_and_no_initial_value_or_promise_provided() throws Exception {

        List<Promise<Integer>> input = new ArrayList<>();

        when.reduce(input, this::plus)
                .then(
                        fail.onFulfilled,
                        e -> {
                            assertNotNull(e);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testReduce_should_allow_sparse_array_input_without_initial() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(null, null, resolved(1), null, resolved(1), resolved(1));

        when.reduce(input, this::plus)
                .<Integer>then(
                        result -> {
                            assertEquals(3, result.intValue());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testReduce_should_allow_sparse_array_input_with_initial() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(null, null, resolved(1), null, resolved(1), resolved(1));

        when.reduce(input, this::plus, resolved(1))
                .<Integer>then(
                        result -> {
                            assertEquals(4, result.intValue());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testReduce_should_reduce_in_input_order() throws Exception {

        Deferred<String> d1 = when.defer();
        Deferred<String> d2 = when.defer();
        Deferred<String> d3 = when.defer();

        List<Promise<String>> input = Arrays.asList(d1.getPromise(), d2.getPromise(), d3.getPromise());

        when.reduce(input, this::concat, resolved(""))
                .<Integer>then(
                        result -> {
                            assertEquals("123", result);
                            return null;
                        }
                ).then(done.onFulfilled, done.onRejected);

        d2.resolve("2");
        d1.resolve("1");
        d3.resolve("3");

        done.assertFulfilled();
    }

}
