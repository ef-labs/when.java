package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import com.englishtown.promises.Promise;
import com.englishtown.promises.State;
import com.englishtown.promises.internal.ValueHolder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.englishtown.promises.HandlerState.FULFILLED;
import static com.englishtown.promises.HandlerState.REJECTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Integration tests for when.settle()
 */
public class SettleTest extends AbstractIntegrationTest {

    private Done<Integer> done = new Done<>();

    private <T> void assertFulfilled(State<T> s, T value) {
        assertEquals(FULFILLED, s.getState());
        assertEquals(s.getValue(), value);
    }

    private void assertRejected(State<?> s, Throwable reason) {
        assertEquals(REJECTED, s.getState());
        assertEquals(s.getReason(), reason);
    }

    @Test
    public void testSettle_should_settle_empty_array() throws Exception {

        when.<Integer>settle(new ArrayList<>())
                .<Integer>then(
                        settled -> {
                            assertEquals(0, settled.size());
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testSettle_should_settle_promises() throws Exception {

        Throwable t = new RuntimeException();
        List<Promise<Integer>> array = Arrays.asList(resolved(0), resolved(2), rejected(t));

        when.settle(array)
                .<Integer>then(
                        settled -> {
                            assertFulfilled(settled.get(0), 0);
                            assertFulfilled(settled.get(1), 2);
                            assertRejected(settled.get(2), t);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testSettle_returned_promise_should_fulfill_once_all_inputs_settle() throws Exception {

        ValueHolder<Consumer<Integer>> resolve = new ValueHolder<>(null);
        ValueHolder<Consumer<Throwable>> reject = new ValueHolder<>(null);

        Promise<Integer> p1 = when.promise((res, rej) -> resolve.value = res);
        Promise<Integer> p2 = when.promise((res, rej) -> reject.value = rej);


        List<Promise<Integer>> array = Arrays.asList(resolved(0), p1, p2);

        Throwable t = new RuntimeException();

        when.settle(array)
                .<Integer>then(
                        settled -> {
                            assertFulfilled(settled.get(0), 0);
                            assertFulfilled(settled.get(1), 2);
                            assertRejected(settled.get(2), t);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        assertFalse(done.getFulfilled());

        reject.value.accept(t);
        resolve.value.accept(2);

        done.assertFulfilled();
    }

}
