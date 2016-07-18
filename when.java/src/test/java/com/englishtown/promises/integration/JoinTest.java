package com.englishtown.promises.integration;

import com.englishtown.promises.BiFail;
import com.englishtown.promises.Done;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for when.join()
 */
public class JoinTest extends AbstractIntegrationTest {

    private final Done<Integer> done = new Done<>();
    private final BiFail<List<Integer>, Integer> fail = new BiFail<>();

    @Test
    @SuppressWarnings("unchecked")
    public void testJoin_should_resolve_empty_input() throws Exception {

        when.<Integer>join().<Integer>then(
                result -> {
                    assertThat(result, is(Collections.emptyList()));
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

//    'should join values': function(done) {
//        when.join(1, 2, 3).then(
//                function(results) {
//            assert.equals(results, [1, 2, 3]);
//        },
//        fail
//        ).ensure(done);
//    },

    @Test
    @SuppressWarnings("unchecked")
    public void testJoin_should_join_promises_array() throws Exception {

        when.join(resolved(1), resolved(2), resolved(3)).<Integer>then(
                results -> {
                    assertThat(results, is(Arrays.asList(1, 2, 3)));
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

//    'should join mixed array': function(done) {
//        when.join(resolved(1), 2, resolved(3), 4).then(
//                function(results) {
//            assert.equals(results, [1, 2, 3, 4]);
//        },
//        fail
//        ).ensure(done);
//    },

    @Test
    @SuppressWarnings("unchecked")
    public void testJoin_should_reject_if_any_input_promise_rejects() throws Exception {

        Throwable t = new RuntimeException();

        when.join(resolved(1), rejected(t), resolved(3)).then(
                fail.onFulfilled,
                failed -> {
                    assertEquals(failed, t);
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
