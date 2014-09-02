package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import com.englishtown.promises.Fail;
import com.englishtown.promises.Promise;
import com.englishtown.promises.exceptions.RejectException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.*;

/**
 * Integration tests for when.any()
 */
public class AnyTest extends AbstractIntegrationTest {

    private final Done<Integer> done = new Done<>();
    private final Fail<Integer> fail = new Fail<>();


    private boolean contains(List<Integer> list, int item) {
        for (int i = list.size() - 1; i >= 0; --i) {
            if (list.get(i) == item) {
                return true;
            }
        }

        return false;
    }

    @Test
    public void testAny_should_resolve_to_undefined_with_empty_input_array() throws Exception {

        List<Promise<Integer>> input = new ArrayList<>();

        when.any(input).then(
                result -> {
                    assertNull(result);
                    return null;
                },
                fail.onRejected
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    /*
    'should resolve with an input value': function() {
		var input = [1, 2, 3];
		return when.any(input).then(
			function(result) {
				assert(contains(input, result));
			},
			fail
		);
	},
     */

    @Test
    public void testAny_should_resolve_with_a_promised_input_value() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(resolved(1), resolved(2), resolved(3));

        when.any(input).then(
                result -> {
                    assertTrue(contains(Arrays.<Integer>asList(1, 2, 3), result));
                    return null;
                },
                fail.onRejected
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testAny_should_resolve_with_some_rejected_promise_input_values() throws Exception {

        List<Promise<Integer>> input = Arrays.<Promise<Integer>>asList(rejected(null), rejected(null), resolved(3));

        when.any(input).then(
                result -> {
                    assertEquals(3, result.intValue());
                    return null;
                },
                fail.onRejected
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testAny_should_reject_with_all_rejected_input_values_if_all_inputs_are_rejected() throws Exception {

        Throwable t1 = new RuntimeException();
        Throwable t2 = new RuntimeException();
        Throwable t3 = new RuntimeException();

        List<Promise<Integer>> input = Arrays.<Promise<Integer>>asList(rejected(t1), rejected(t2), rejected(t3));

        when.any(input).then(
                fail.onFulfilled,
                result -> {
                    assertThat(result, instanceOf(RejectException.class));
                    RejectException re = (RejectException) result;
                    assertThat(re.getInnerExceptions(), is(Arrays.asList(t1, t2, t3)));
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    /*

	'should accept a promise for an array': function(done) {
		var expected, input;

		expected = [1, 2, 3];
		input = resolved(expected);

		when.any(input).then(
			function(result) {
				refute.equals(expected.indexOf(result), -1);
			},
			fail
		).ensure(done);
	},
     */

    /*
    @Test
    public void testAny_() throws Exception {

    }

buster.testCase('when.any', {

	'should resolve to undefined when input promise does not resolve to array': function(done) {
		when.any(resolved(1)).then(
			function(result) {
				refute.defined(result);
			},
			fail
		).ensure(done);
	}

});


     */
}
