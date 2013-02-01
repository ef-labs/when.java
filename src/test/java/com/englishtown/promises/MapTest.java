package com.englishtown.promises;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/31/13
 * Time: 5:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class MapTest {

    private Fail<List<Integer>, Integer> fail = new Fail<>();

    Runnable<Integer, Integer> mapper = new Runnable<Integer, Integer>() {
        @Override
        public Integer run(Integer value) {
            return value * 2;
        }
    };

    @Test
    public void testMap_should_map_input_values_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        final List<Integer> input = Arrays.asList(1, 2, 3);
        final When<Integer, Integer> when = new When<>();

        when.map(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
            @Override
            public Promise<List<Integer>, Integer> run(List<Integer> value) {
                assertEquals(3, value.size());
                Integer[] expected = {2, 4, 6};
                assertArrayEquals(expected, value.toArray(new Integer[3]));
                return null;
            }
        }, fail.onFail, null).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();


    }

    @Test
    public void testMap_should_map_input_promises_array() {

        final When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        final List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.mapPromises(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
            @Override
            public Promise<List<Integer>, Integer> run(List<Integer> value) {
                assertEquals(3, value.size());
                Integer[] expected = {2, 4, 6};
                assertArrayEquals(expected, value.toArray(new Integer[3]));
                return null;
            }
        }, fail.onFail, null).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();

    }


    // Mixed type arrays don't make as much sense with Java
//            'should map mixed input array': function(done) {
//        var input = [1, resolved(2), 3];
//        when.map(input, mapper).then(
//                function(results) {
//            assert.equals(results, [2,4,6]);
//        },
//        fail
//        ).always(done);
//    },

    @Test
    public void testMap_should_map_input_when_mapper_returns_a_promise() {

        Done<List<Integer>, Integer> done = new Done<>();
        List<Integer> input = Arrays.asList(1, 2, 3);
        When<Integer, Integer> when = new When<>();
        final List<Resolver<Integer, Integer>> resolvers = new ArrayList<>(3);
        final List<Integer> values = new ArrayList<>(3);

        Runnable<Promise<Integer, Integer>, Integer> deferredMapper = new Runnable<Promise<Integer, Integer>,
                Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                Deferred<Integer, Integer> d = When.defer();
                resolvers.add(d.getResolver());
                values.add(mapper.run(value));
                return d.getPromise();
            }
        };

        when.map(input, deferredMapper).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        Integer[] expected = {2, 4, 6};
                        assertArrayEquals(expected, value.toArray(new Integer[3]));
                        return null;
                    }
                },
                null,
                null).then(done.onSuccess, done.onFail, null);

        for (int i = 0; i < resolvers.size(); i++) {
            resolvers.get(i).resolve(values.get(i));
        }

        done.assertSuccess();

    }

    @Test
    public void testMap_should_accept_a_promise_for_an_array() {

        Done<List<Integer>, Integer> done = new Done<>();
        final When<Integer, Integer> when = new When<>();
        When<List<Integer>, Integer> w1 = new When<>();
        Promise<List<Integer>, Integer> input = w1.resolve(Arrays.asList(1, 2, 3));

        when.mapPromise(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        Integer[] expected = {2, 4, 6};
                        assertArrayEquals(expected, value.toArray(new Integer[3]));
                        return null;
                    }
                },
                fail.onFail,
                null
        ).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_resolve_to_empty_array_when_input_promise_does_not_resolve_to_an_array() {

        List<Integer> input = null;
        final When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        when.map(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        assertNotNull(value);
                        assertEquals(0, value.size());
                        return null;
                    }
                },
                fail.onFail,
                null).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_map_input_promises_when_mapper_returns_a_promise() {

        Done<List<Integer>, Integer> done = new Done<>();
        final When<Integer, Integer> when = new When<>();
        List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.mapPromises(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                new Runnable<Promise<List<Integer>, Integer>, List<Integer>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(List<Integer> value) {
                        Integer[] expected = {2, 4, 6};
                        assertArrayEquals(expected, value.toArray(new Integer[3]));
                        return null;
                    }
                },
                fail.onFail,
                null).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_reject_when_input_contains_rejection() {


        final When<Integer, Integer> when = new When<>();
        Done<List<Integer>, Integer> done = new Done<>();

        final List<Promise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.resolve(3));

        when.mapPromises(input, new Runnable<Promise<Integer, Integer>, Integer>() {
            @Override
            public Promise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                fail.onSuccess,
                new Runnable<Promise<List<Integer>, Integer>, Reason<List<Integer>>>() {
                    @Override
                    public Promise<List<Integer>, Integer> run(Reason<List<Integer>> value) {
                        assertEquals(1, value.data.size());
                        assertTrue(2 == value.data.get(0));
                        return null;
                    }
                }, null).then(done.onSuccess, done.onFail, null);

        done.assertSuccess();
    }

}
