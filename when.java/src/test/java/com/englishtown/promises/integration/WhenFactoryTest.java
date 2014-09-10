package com.englishtown.promises.integration;

import com.englishtown.promises.*;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 * Integration tests for {@link com.englishtown.promises.WhenFactory}
 */
public class WhenFactoryTest {

    private Done<Object> done = new Done<>();

    @Test
    public void testCreateSync() throws Exception {

        When when = WhenFactory.createSync();

        Deferred<Integer> d = when.defer();
        Promise<Integer> p = d.getPromise();

        p.then(value -> {
            return when.resolve(2 * value);
        }).then(value2 -> {
            // Do something
            assertEquals(20, value2.intValue());
            return when.resolve(String.valueOf(value2));
        }).then(value3 -> {
            assertEquals("20", value3);
            return null;
        }).then(done.onFulfilled, done.onRejected);

        // Use the resolver to trigger the callback registered above.
        // The callback value will be 10
        d.resolve(10);

        done.assertFulfilled();

    }

    @Test
    public void testCreateAsync() throws Exception {

        When when = WhenFactory.createAsync();
        CountDownLatch latch = new CountDownLatch(1);

        Deferred<Integer> d = when.defer();
        Promise<Integer> p = d.getPromise();

        p.then(value -> {
            return when.resolve(2 * value);
        }).then(value2 -> {
            // Do something
            assertEquals(20, value2.intValue());
            return when.resolve(String.valueOf(value2));
        }).then(value3 -> {
            assertEquals("20", value3);
            latch.countDown();
            return null;
        }).then(done.onFulfilled, done.onRejected);

        // Use the resolver to trigger the callback registered above.
        // The callback value will be 10
        d.resolve(10);

        latch.await();
        done.assertFulfilled();

    }

}
