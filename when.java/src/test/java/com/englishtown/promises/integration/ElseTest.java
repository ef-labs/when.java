package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * promise.else() integration tests
 */
public class ElseTest extends AbstractIntegrationTest {

    private Sentinel input = new Sentinel();
    private Sentinel sentinel = new Sentinel();
    private Done<Object> done = new Done<>();

    @Test
    public void testElse_should_resolve_normally_if_previous_promise_doesnt_fail() throws Exception {

        when.resolve(input)
                .orElse(resolved(sentinel))
                .then((val) -> {
                    assertEquals(input, val);
                    return null;
                })
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

    @Test
    public void testElse_should_resolve_with_else_value_if_previous_promise_fails() throws Exception {

        when.reject(new RuntimeException())
                .orElse(resolved(sentinel))
                .then((val) -> {
                    assertEquals(sentinel, val);
                    return null;
                })
                .ensure(done::fulfill);

        done.assertFulfilled();
    }

}
