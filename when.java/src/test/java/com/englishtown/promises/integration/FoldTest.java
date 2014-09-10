package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import com.englishtown.promises.Thenable;
import org.junit.Test;

import java.util.function.BiFunction;

import static org.junit.Assert.assertEquals;

/**
 * Promise.fold() integration tests
 */
public class FoldTest extends AbstractIntegrationTest {

    private Sentinel sentinel = new Sentinel();
    private Sentinel other = new Sentinel();
    private RuntimeException sentinelEx = new RuntimeException();

    private Done<Object> done = new Done<>();

    private BiFunction<Integer, Integer, Thenable<Integer>> noop = (x, y) -> null;

    @Test
    public void testFold_should_pass_value_and_arg() throws Exception {

        when.<Object>resolve(other)
                .fold((a, b) -> {
                    assertEquals(sentinel, a);
                    assertEquals(other, b);
                    return null;
                }, resolved(sentinel))
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFold_should_pairwise_combine_two_promises() throws Exception {

        when.resolve(1).fold(
                (x, y) -> {
                    return resolved(x + y);
                }, when.resolve(2))
                .then(
                        (x) -> {
                            assertEquals(3, x.intValue());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFold_should_reject_if_combine_throws() throws Exception {

        when.resolve(1)
                .fold((x, y) -> {
                    throw sentinelEx;
                }, resolved(2))
                .otherwise((e) -> {
                    assertEquals(sentinelEx, e);
                    return null;
                })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFold_should_reject_if_combine_returns_rejection() throws Exception {

        when.resolve(1)
                .fold((x, y) -> when.reject(sentinelEx), resolved(sentinel))
                .otherwise((e) -> {
                    assertEquals(sentinelEx, e);
                    return null;
                })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFold_should_reject_and_not_call_combine_if_promise_rejects() throws Exception {

        when.<Integer>reject(sentinelEx)
                .fold(noop, resolved(2))
                .otherwise((e) -> {
                    assertEquals(sentinelEx, e);
                    return null;
                })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFold_should_reject_and_not_call_combine_if_arg_rejects() throws Exception {

        when.resolve(1)
                .fold(noop, when.reject(sentinelEx))
                .otherwise((e) -> {
                    assertEquals(sentinelEx, e);
                    return null;
                })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
