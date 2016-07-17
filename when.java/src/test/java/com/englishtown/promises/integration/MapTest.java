package com.englishtown.promises.integration;

import com.englishtown.promises.BiFail;
import com.englishtown.promises.Deferred;
import com.englishtown.promises.Done;
import com.englishtown.promises.Promise;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Integration tests for when.map()
 */
public class MapTest extends AbstractIntegrationTest {

    private final Done<Integer> done = new Done<>();
    private final BiFail<List<Integer>, Integer> fail = new BiFail<>();

    private final Function<Integer, Promise<Integer>> mapper = (val) -> resolved(val * 2);

//    function deferredMapper(val) {
//        return when(mapper(val)).delay(Math.random()*10);
//    }

    private Function<Integer, Integer> identity = (x) -> x;

//    @Test
//    public void testMap_should_map_input_values_array() throws Exception {
//
//            List<Integer> input = [1, 2, 3];
//            when.map(input, mapper).then(
//                    function(results) {
//                assert.equals(results, [2,4,6]);
//            },
//            fail
//            ).ensure(done);
//
//    }

    @Test
    public void testMap_should_map_input_promises_array() throws Exception {

        List<Promise<Integer>> input = Arrays.asList(resolved(1), resolved(2), resolved(3));

        when.map(input, mapper)
                .then(
                        results -> {
                            assertThat(results, is(Arrays.asList(2, 4, 6)));
                            return null;
                        },
                        fail.onRejected
                ).then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();
    }

    @Test
    public void testMap_should_map_input_when_mapper_returns_a_promise() throws Exception {

        Deferred<Integer> d = when.defer();
        List<Promise<Integer>> input = Arrays.asList(resolved(1), d.getPromise(), resolved(3));

        when.map(input, mapper)
                .then(
                        results -> {
                            assertThat(results, is(Arrays.asList(2, 4, 6)));
                            return null;
                        },
                        fail.onRejected
                ).then(done.onFulfilled, done.onRejected);

        d.resolve(2);
        done.assertFulfilled();

    }

    @Test
    public void testMap_should_reject_when_input_contains_rejection() throws Exception {

        Throwable t = new RuntimeException();
        List<Promise<Integer>> input = Arrays.asList(resolved(1), rejected(t), resolved(3));

        when.map(input, mapper)
                .then(
                        fail.onFulfilled,
                        x -> {
                            assertEquals(t, x);
                            return null;
                        })
                .then(done.onFulfilled, done.onRejected);

        done.assertFulfilled();

    }

    /*

    @Test
    public void testMap_() throws Exception {

    }

buster.testCase('when.map', {

	'should map mixed input array': function(done) {
		var input = [1, resolved(2), 3];
		when.map(input, mapper).then(
			function(results) {
				assert.equals(results, [2,4,6]);
			},
			fail
		).ensure(done);
	},

	'should accept a promise for an array': function(done) {
		when.map(resolved([1, resolved(2), 3]), mapper).then(
			function(result) {
				assert.equals(result, [2,4,6]);
			},
			fail
		).ensure(done);
	},

	'should resolve to empty array when input promise does not resolve to an array': function(done) {
		when.map(resolved(123), mapper).then(
			function(result) {
				assert.equals(result, []);
			},
			fail
		).ensure(done);
	},

	'should map input promises when mapper returns a promise': function(done) {
		var input = [resolved(1),resolved(2),resolved(3)];
		when.map(input, mapper).then(
			function(results) {
				assert.equals(results, [2,4,6]);
			},
			fail
		).ensure(done);
	},

	'should propagate progress': function() {
		// Thanks @depeele for this test
		var input = [_resolver(1), _resolver(2), _resolver(3)];
		var ncall = 0;

		return when.map(input, identity).then(
			function() {
				assert.equals(ncall, 6);
			},
			fail,
			function() {
				ncall++;
			}
		);

		function _resolver(id) {
			return when.promise(function(resolve, reject, notify) {
				var loop = 0;
				var timer = setInterval(function () {
					notify(id);
					loop++;
					if (loop === 2) {
						clearInterval(timer);
						resolve(id);
					}
				}, 1);
			});
		}
	}
});

     */
}
