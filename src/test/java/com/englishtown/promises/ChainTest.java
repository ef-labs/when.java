package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/2/13
 * Time: 6:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChainTest {

    private Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testChain_should_return_a_promise_for_an_input_value() {

        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        //var d, result;

        d = when.defer();

        Promise<Integer, Integer> result = when.chain(1, d.getResolver(), null);

        assertNotNull(result);
        assertNotSame(result, d);
        assertNotSame(result, d.getPromise());

    }

    @Test
    public void testChain_should_return_a_promise_for_an_input_promise() {

        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d1, d2;
        Promise<Integer, Integer> result;

        d1 = when.defer();
        d2 = when.defer();

        result = when.chain(d1.getPromise(), d2.getResolver(), null);

        assertNotNull(result);
        assertNotSame(result, d1);
        assertNotSame(result, d1.getPromise());
        assertNotSame(result, d2);
        assertNotSame(result, d2.getPromise());

    }

    @Test
    public void testChain_should_resolve_resolver_with_input_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null).then(done.onSuccess, done.onFail, null);

        when.chain(1, d.getResolver(), null);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_resolve_resolver_with_input_promise_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null).then(done.onSuccess, done.onFail, null);

        input = when.defer();
        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), null);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_resolve_resolver_with_provided_value_when_input_is_a_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null).then(done.onSuccess, done.onFail, null);

        when.chain(1, d.getResolver(), 2);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_resolve_resolver_with_provided_value_when_input_is_a_promise() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null).then(done.onSuccess, done.onFail, null);

        input = when.defer();
        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), 2);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_reject_resolver_with_input_promise_rejection_reason() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(1, value.data.intValue());
                        return null;
                    }
                },
                null).then(done.onSuccess, done.onFail, null);

        input = when.defer();
        input.getResolver().reject(1);

        when.chain(input.getPromise(), d.getResolver(), null);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_reject_resolver_with_input_promise_rejection_reason_when_optional_value_provided() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d = when.defer();
        Deferred<Integer, Integer> input;

        d.getPromise().then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(1, value.data.intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        input = when.defer();
        input.getResolver().reject(1);

        when.chain(input.getPromise(), d.getResolver(), 2);
        done.assertSuccess();

    }

    @Test
    public void testChain_should_return_a_promise_that_resolves_with_the_input_promise_resolution_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), null).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(1, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

    @Test
    public void testChain_should_return_a_promise_that_resolves_with_the_optional_resolution_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().resolve(1);

        when.chain(input.getPromise(), d.getResolver(), 2).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(2, value.intValue());
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

    @Test
    public void testChain_should_return_a_promise_that_rejects_with_the_input_promise_rejection_value() {

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();
        Deferred<Integer, Integer> d;
        Deferred<Integer, Integer> input;

        input = when.defer();
        d = when.defer();

        input.getResolver().reject(1);

        when.chain(input.getPromise(), d.getResolver(), null).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Reason<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Reason<Integer> value) {
                        assertEquals(1, value.data.intValue());
                        return null;
                    }
                },
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }

}
