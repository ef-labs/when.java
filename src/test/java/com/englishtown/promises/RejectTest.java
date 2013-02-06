package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 5:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class RejectTest {

    private Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testReject_should_reject_an_immediate_value() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        when.reject(expected).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(expected, value.data.intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

    @Test
    public void testReject_should_reject_a_resolved_promise() {

        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        Deferred<Integer, Integer> d = when.defer();
        d.getResolver().resolve(expected);

        when.reject(d.getPromise()).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(expected, value.data.intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testReject_should_reject_a_rejected_promise() {

        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        Deferred<Integer, Integer> d = when.defer();
        d.getResolver().reject(new Reason<Integer>(expected, null));

        when.reject(d.getPromise()).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(expected, value.data.intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

}
