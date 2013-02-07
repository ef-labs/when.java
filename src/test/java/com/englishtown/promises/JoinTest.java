package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 4:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class JoinTest {

    private Fail<List<Integer>, Integer> fail = new Fail<>();

    @Test
    public void testJoin_should_resolve_empty_input_promises() {

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.joinPromises().then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
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

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.join().then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
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

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.join(1, 2, 3).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
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

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.joinPromises(when.resolve(1), when.resolve(2), when.resolve(3)).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> results) {
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

        When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.joinPromises(when.resolve(1), when.reject(2), when.resolve(3)).then(
                fail.onSuccess,
                new Runnable<Promise<List<Integer>, Integer>, Value<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Value<List<Integer>> failed) {
                        assertEquals(2, failed.data.get(0).intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

}
