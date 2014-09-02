package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import com.englishtown.promises.impl.AsyncExecutor;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Created by adriangonzalez on 8/31/14.
 */
public class AsyncTest extends AbstractIntegrationTest {

    private Done<Integer> done = new Done<>();
    private CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void setUp() throws Exception {
        executor = new AsyncExecutor();
        super.setUp();
    }

    @Test
    public void testAsync_resolve() throws Exception {

        when.resolve(1)
                .<Integer>then(
                        (x) -> {
                            assertEquals(1, x.intValue());
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                )
                .then(done.onFulfilled, done.onRejected)
                .ensure(latch::countDown);

        assertFalse(done.fulfilled());

        latch.await();
        done.assertFulfilled();

    }

    @Test
    public void testAsync_reject() throws Exception {

        Throwable t = new RuntimeException();

        when.reject(t)
                .<Integer>then(
                        (x) -> {
                            fail();
                            return null;
                        },
                        t1 -> {
                            assertEquals(t, t1);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            return null;
                        }
                )
                .then(done.onFulfilled, done.onRejected)
                .ensure(latch::countDown);

        assertFalse(done.fulfilled());

        latch.await();
        done.assertFulfilled();

    }

}
