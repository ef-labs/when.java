package com.englishtown.promises.integration;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Done;
import com.englishtown.promises.Fail;
import com.englishtown.promises.Promise;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for when.race()
 */
public class RaceTest extends AbstractIntegrationTest {

    private Sentinel sentinel = new Sentinel();
    private Throwable reason = new RuntimeException();
    private Promise<Sentinel> never;
    private Promise<Sentinel> fulfilled;
    private Promise<Sentinel> rejected;
    private Done<Sentinel> done = new Done<>();
    private Fail<Sentinel> fail = new Fail<>();

    @Override
    public void setUp() throws Exception {
        super.setUp();
        never = helper.never();
        fulfilled = when.resolve(sentinel);
        rejected = when.reject(reason);
    }

    @Test
    public void testRace_should_return_empty_race_for_length_0() throws Exception {
        assertEquals(never, when.race(new ArrayList<>()));
    }

    @Test
    public void testRace_should_be_identity_for_length_1_when_fulfilled_via_promise() throws Exception {

        when.race(Arrays.asList(fulfilled))
                .<Sentinel>then(x -> {
                    assertEquals(sentinel, x);
                    return null;
                })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testRace_should_be_identity_for_length_1_when_rejected() throws Exception {

        when.race(Arrays.asList(rejected))
                .then(fail.onFulfilled, x -> {
                    assertEquals(reason, x);
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testRace_should_be_commutative_when_fulfilled() throws Exception {

        when.race(Arrays.asList(fulfilled, never))
                .<Sentinel>then(x -> {
                    return when.race(Arrays.asList(never, fulfilled))
                            .then(y -> {
                                assertEquals(x, y);
                                return null;
                            });
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testRace_should_be_commutative_when_rejected() throws Exception {

        when.race(Arrays.asList(rejected, never))
                .then(fail.onFulfilled, x -> {
                    return when.race(Arrays.asList(never, rejected)).then(fail.onFulfilled, y -> {
                        assertEquals(x, y);
                        return null;
                    });
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testRace_should_fulfill_when_winner_fulfills() throws Exception {

        Deferred<Sentinel> d1 = when.defer();
        Deferred<Sentinel> d2 = when.defer();

        when.race(Arrays.asList(d1.getPromise(), d2.getPromise(), fulfilled))
                .then(x -> {
                    assertEquals(sentinel, x);
                    return null;
                }, fail.onRejected)
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testRace_should_reject_when_winner_rejects() throws Exception {

        Deferred<Sentinel> d1 = when.defer();
        Deferred<Sentinel> d2 = when.defer();

        when.race(Arrays.asList(d1.getPromise(), d2.getPromise(), rejected))
                .then(fail.onFulfilled, x -> {
                    assertEquals(reason, x);
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
