package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResolveTest {

    private Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testResolve_should_resolve_an_immediate_value() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        when.resolve(expected).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(expected, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testResolve_should_resolve_a_resolved_promise() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        Deferred<Integer, Integer> d = when.defer();
        d.getResolver().resolve(expected);

        when.resolvePromise(d.getPromise()).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(expected, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testResolve_should_reject_a_rejected_promise() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        Deferred<Integer, Integer> d = when.defer();
        d.getResolver().reject(expected);

        when.resolvePromise(d.getPromise()).then(
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

//            'should use valueOf immediate values': function(done) {
//        // See https://github.com/kriskowal/q/issues/106
//        var fake, expected;
//
//        expected = 1;
//        fake = {
//                valueOf: this.stub().returns(expected)
//        };
//
//        when.resolve(fake).then(
//                function(value) {
//            assert.equals(value, expected);
//        },
//        fail
//        ).always(done);
//    },
//
//            'should use valueOf foreign promises': function(done) {
//        // See https://github.com/kriskowal/q/issues/106
//        var fake, expected;
//
//        expected = 1;
//        fake = {
//                valueOf: function() {
//            return this;
//        },
//        then: function(cb) {
//            return cb(expected);
//        }
//        };
//
//        when.resolve(fake).then(
//                function(value) {
//            assert.equals(value, expected);
//        },
//        fail
//        ).always(done);
//    }

}
