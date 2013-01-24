package com.englishtown.promises;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/24/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class WhenTest {
    @Test
    public void testWhen_Value_AllCallbacks() throws Exception {

        final Value<Integer> val = new Value<>();
        When<Integer, String> when = new When<>();
        Promise<Integer, String> promise;

        Runnable<Promise<Integer, String>, Integer> onFulfilled = new Runnable<Promise<Integer, String>, Integer>() {
            @Override
            public Promise<Integer, String> run(Integer value) {
                val.value = value;
                return null;
            }
        };

        promise = when.when(1, onFulfilled);

        assertNotNull(promise);
        assertNotNull(val.value);
        assertEquals(1, val.value.intValue());

    }

//    @Test
//    public void testWhen() throws Exception {
//
//    }
//
//    @Test
//    public void testWhen() throws Exception {
//
//    }
//
//    @Test
//    public void testWhen() throws Exception {
//
//    }
//
//    @Test
//    public void testWhen() throws Exception {
//
//    }
//
//    @Test
//    public void testWhen() throws Exception {
//
//    }

    @Test
    public void testResolve_Promise() throws Exception {

    }

    @Test
    public void testResolve_Value() throws Exception {

    }

    @Test
    public void testReject() throws Exception {

    }

    @Test
    public void testDefer() throws Exception {

        When<Integer, Integer> when = new When<>();
        When<Integer, Integer>.DeferredImpl deferred = when.defer();
        final Value<Integer> val = new Value<>();
        int expected = 10;

        Runnable<Promise<Integer, Integer>, Integer> onFulfilled = new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                val.value = value;
                return null;
            }
        };

        deferred.promise.then(onFulfilled, null, null);
        deferred.resolver.resolve.run(expected);

        assertEquals(expected, val.value.intValue());

    }

    @Test
    public void testChain() throws Exception {

    }
}
