package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import com.englishtown.promises.Fail;
import com.englishtown.promises.Promise;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Flow integration tests
 */
public class FlowTest extends AbstractIntegrationTest {

    private Sentinel sentinel = new Sentinel();
    private Sentinel other = new Sentinel();
    private RuntimeException sentinelEx = new RuntimeException();
    private RuntimeException otherEx = new RuntimeException();

    private Done<Sentinel> done = new Done<>();
    private Fail<Sentinel> fail = new Fail<>();

    @Test
    public void testFlow_should_catch_rejections() throws Exception {

        rejected(sentinelEx).<Sentinel>otherwise((e) -> {
            assertEquals(e, sentinelEx);
            return null;
        }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testFlow_otherwise_when_predicate_is_provided_and_is_an_Error_type_match_should_only_catch_errors_of_same_type() throws Exception {

        IllegalArgumentException e1 = new IllegalArgumentException();

        rejected(e1)
                .otherwise(ClassCastException.class, fail.onRejected)
                .<Sentinel>otherwise(IllegalArgumentException.class,
                        (e) -> {
                            assertEquals(e1, e);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFlow_otherwise_when_predicate_is_provided_and_is_a_predicate_function_should_only_catch_errors_of_same_type() throws Exception {

        IllegalArgumentException e1 = new IllegalArgumentException();

        rejected(e1)
                .otherwise(
                        (e) -> {
                            return e != e1;
                        },
                        fail.onRejected)
                .<Sentinel>otherwise(
                        (e) -> {
                            return e == e1;
                        },
                        (e) -> {
                            assertEquals(e1, e);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFlow_ensure_should_return_a_promise() throws Exception {

        Promise<Object> p = resolved(null).ensure(null);
        assertNotNull(p);

    }

    @Test
    public void testFlow_ensure_should_ignore_callback_return_value() throws Exception {

        resolved(sentinel)
                .ensure(
                        () -> {
                        }
                )
                .then(
                        (val) -> {
                            assertEquals(sentinel, val);
                            return null;
                        },
                        fail.onRejected
                )
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFlow_ensure_when_fulfilled_should_propagate_rejection_on_throw() throws Exception {

        resolved(other)
                .ensure(
                        () -> {
                            throw sentinelEx;
                        }
                )
                .then(
                        fail.onFulfilled,
                        (val) -> {
                            assertEquals(sentinelEx, val);
                            return null;
                        }
                ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testFlow_ensure_when_rejected_should_propagate_rejection_ignoring_callback_return_value() throws Exception {

        this.<Sentinel>rejected(sentinelEx)
                .ensure(
                        () -> {
                        }
                )
                .then(
                        fail.onFulfilled,
                        (val) -> {
                            assertEquals(sentinelEx, val);
                            return null;
                        }
                ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testFlow_ensure_when_rejected_should_propagate_rejection_on_throw() throws Exception {

        this.<Sentinel>rejected(otherEx)
                .ensure(
                        () -> {
                            throw sentinelEx;
                        }
                )
                .then(
                        fail.onFulfilled,
                        (val) -> {
                            assertEquals(sentinelEx, val);
                            return null;
                        }
                ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
