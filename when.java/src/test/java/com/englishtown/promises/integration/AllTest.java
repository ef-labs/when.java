package com.englishtown.promises.integration;

import com.englishtown.promises.Deferred;
import com.englishtown.promises.Done;
import com.englishtown.promises.Promise;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Integration tests for when.all()
 */
public class AllTest extends AbstractIntegrationTest {

    private final Done<Integer> done = new Done<>();

    @Test
    public void testAll_should_resolve_empty_input() throws Exception {
        List<Promise<Integer>> empty = new ArrayList<>();

        when.all(empty)
                .then(
                        result -> {
                            assertNotNull(result);
                            assertTrue(result.isEmpty());
                            return (Promise<Integer>) null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    /*
    @Test
    public void testAll_should_resolve_values_array () throws Exception {

        List<Integer> input = Arrays.asList(1, 2, 3);
        when.all(input).then(
                function(results) {
            assert.equals(results, input);
        },
        fail
        ).ensure(done);
    }
    */

    @Test
    public void testAll_should_resolve_promises_array() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(resolved(1), resolved(2), resolved(3));

        when.all(input)
                .then(
                        results -> {
                            assertNotNull(results);
                            assertEquals(input.size(), results.size());
                            assertThat(results, is(Arrays.asList(1, 2, 3)));
                            return (Promise<Integer>) null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testAll_should_resolve_sparse_array_input() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(null, resolved(1), null, resolved(1), resolved(1));

        when.all(input)
                .then(
                        results -> {
                            assertNotNull(results);
                            assertThat(results, is(Arrays.asList(null, 1, null, 1, 1)));
                            return (Promise<Integer>) null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testAll_should_reject_if_any_input_promise_rejects() throws Exception {

        Throwable t = new RuntimeException();

        List<Promise<Integer>> input = Arrays.asList(resolved(1), rejected(t), resolved(3));

        when.all(input).<Integer>then(
                null,
                x -> {
                    assertEquals(t, x);
                    return rejected(x);
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertRejected();
    }

    @Test
    public void testAll_deferred_resolve() throws Exception {

        Deferred<Integer> d = when.defer();
        List<Promise<Integer>> input = Arrays.asList(resolved(1), d.getPromise(), resolved(3));

        when.all(input).then(
                results -> {
                    assertNotNull(results);
                    assertThat(results, is(Arrays.asList(1, 4, 3)));
                    return (Promise<Integer>) null;
                }
        ).then(done.onFulfilled, done.onRejected);

        d.resolve(4);
        done.assertFulfilled();

    }

    @Test
    public void testAll_deferred_reject() throws Exception {

        Throwable t = new RuntimeException();
        Deferred<Integer> d = when.defer();
        List<Promise<Integer>> input = Arrays.asList(resolved(1), d.getPromise(), resolved(3));

        when.all(input).<Integer>then(
                null,
                x -> {
                    assertEquals(t, x);
                    return rejected(x);
                }
        ).then(done.onFulfilled, done.onRejected);

        d.reject(t);
        done.assertRejected();

    }

/*

	'should accept a promise for an array': function(done) {
		var expected, input;

		expected = [1, 2, 3];
		input = resolved(expected);

		when.all(input).then(
			function(results) {
				assert.equals(results, expected);
			},
			fail
		).ensure(done);
	},

	'should resolve to empty array when input promise does not resolve to array': function(done) {
		when.all(resolved(1)).then(
			function(result) {
				assert.equals(result, []);
			},
			fail
		).ensure(done);
	}
 */

}
