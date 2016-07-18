package com.englishtown.promises.integration;

import com.englishtown.promises.Done;
import com.englishtown.promises.Thenable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Integration tests for when.sequence()
 */
public class SequenceTest extends AbstractIntegrationTest {

    private Done<Integer> done = new Done<>();

    private Function<Void, Thenable<Integer>> createTask(Integer y) {
        return (x) -> {
            return resolved(y);
        };
    }

    private Function<String, Thenable<Integer>> expectArgs(String expected) {
        return (x) -> {
            assertEquals(expected, x);
            return null;
        };
    }

    @Test
    public void testSequence_should_execute_tasks_in_order() throws Exception {

        List<Function<Void, Thenable<Integer>>> input = Arrays.asList(createTask(1), createTask(2), createTask(3));

        when.sequence(input, null).<Integer>then(
                result -> {
                    assertThat(result, is(Arrays.asList(1, 2, 3)));
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testSequence_should_resolve_to_empty_array_when_no_tasks_supplied() throws Exception {

        when.sequence(new ArrayList<>(), resolved(1)).<Integer>then(
                result -> {
                    assertNotNull(result);
                    assertTrue(result.isEmpty());
                    return null;
                }
        ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testSequence_should_pass_arg_to_all_tasks() throws Exception {

        String expected = "123";

        List<Function<String, Thenable<Integer>>> tasks = Arrays.asList(expectArgs(expected), expectArgs(expected), expectArgs(expected));

        when.sequence(tasks, resolved(expected))
                .<Integer>then(result -> null)
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
