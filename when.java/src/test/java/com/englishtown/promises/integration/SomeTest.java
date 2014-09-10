package com.englishtown.promises.integration;

import com.englishtown.promises.BiFail;
import com.englishtown.promises.Done;
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
 * Integration tests for when.some()
 */
public class SomeTest extends AbstractIntegrationTest {

    private Done<Integer> done = new Done<>();
    private BiFail<List<Integer>, Integer> fail = new BiFail<>();

    private boolean contains(List<Integer> array, Integer value) {
        for (int i = array.size() - 1; i >= 0; i--) {
            Integer v = array.get(i);
            if (value == null) {
                if (v == null) {
                    return true;
                }
            } else if (value.equals(array.get(i))) {
                return true;
            }
        }

        return false;
    }

    private boolean isSubset(List<Integer> subset, List<Integer> superset) {

        int subsetLen = subset.size();

        if (subsetLen > superset.size()) {
            return false;
        }

        for (int i = 0; i < subsetLen; i++) {
            if (!contains(superset, subset.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Test
    public void testSome_should_resolve_empty_input() throws Exception {

        when.some(new ArrayList<>(), 1).<Integer>then(
                result -> {
                    assertThat(result, is(new ArrayList<>()));
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testSome_should_resolve_promises_array() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.some(input, 2).<Integer>then(
                results -> {
                    assertEquals(2, results.size());
                    assertTrue(isSubset(results, Arrays.asList(1, 2, 3)));
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testSome_should_resolve_sparse_array_input() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(null, resolved(1), null, resolved(2), resolved(3));

        when.some(input, 2).<Integer>then(
                results -> {
                    assertTrue(isSubset(results, Arrays.asList(null, 1, null, 2, 3)));
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testSome_should_reject_with_all_rejected_input_values_if_resolving_howMany_becomes_impossible() throws Exception {

        Throwable t1 = new RuntimeException();
        Throwable t2 = new RuntimeException();

        List<Promise<Integer>> input = Arrays.asList(when.resolve(1), when.reject(t1), when.reject(t2));

        when.some(input, 2).then(
                fail.onFulfilled,
                failed -> {
                    assertThat(failed, instanceOf(RejectException.class));
                    RejectException re = (RejectException) failed;
                    assertThat(re.getInnerExceptions(), is(Arrays.asList(t1, t2)));
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    @Test
    public void testSome_should_resolve_to_empty_array_when_n_is_zero() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(resolved(1), resolved(2), resolved(3));

        when.some(input, 0).<Integer>then(
                result -> {
                    assertThat(result, is(new ArrayList<>()));
                    return null;
                }).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

}
