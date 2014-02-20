/*
 * The MIT License (MIT)
 * Copyright © 2013 Englishtown <opensource@englishtown.com>
 * http://englishtown.mit-license.org/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
 *
 */
public class MapTest {

    private final Fail<List<? extends Integer>, Integer> fail = new Fail<>();

    private final Runnable<Integer, Integer> mapper = new Runnable<Integer, Integer>() {
        @Override
        public Integer run(Integer value) {
            return (value == null ? null : value * 2);
        }
    };

    @Test
    public void testMap_should_map_input_values_array() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        final List<Integer> input = Arrays.asList(1, 2, 3);
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.mapValues(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
            @Override
            public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                assertEquals(3, value.size());
                Integer[] expected = {2, 4, 6};
                assertArrayEquals(expected, value.toArray(new Integer[3]));
                return null;
            }
        }, fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testMap_should_map_input_values_array_with_a_null() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        final List<Integer> input = Arrays.asList(1, null, 3);
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();

        when.mapValues(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
            @Override
            public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                assertEquals(3, value.size());
                Integer[] expected = {2, null, 6};
                assertArrayEquals(expected, value.toArray(new Integer[3]));
                return null;
            }
        }, fail.onFail).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testMap_should_map_input_promises_array() {

        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        final List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.map(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
            @Override
            public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                assertEquals(3, value.size());
                Integer[] expected = {2, 4, 6};
                assertArrayEquals(expected, value.toArray(new Integer[3]));
                return null;
            }
        }, fail.onFail).then(done.onSuccess, done.onFail);

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

        Done<List<? extends Integer>, Integer> done = new Done<>();
        List<Integer> input = Arrays.asList(1, 2, 3);
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        final List<Resolver<Integer, Integer>> resolvers = new ArrayList<>(3);
        final List<Integer> values = new ArrayList<>(3);

        Runnable<ProgressPromise<Integer, Integer>, Integer> deferredMapper = new Runnable<ProgressPromise<Integer, Integer>,
                                Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                DeferredProgress<Integer, Integer> d = when.defer();
                resolvers.add(d.getResolver());
                values.add(mapper.run(value));
                return d.getPromise();
            }
        };

        when.mapValues(input, deferredMapper).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                        Integer[] expected = {2, 4, 6};
                        assertArrayEquals(expected, value.toArray(new Integer[3]));
                        return null;
                    }
                }).then(done.onSuccess, done.onFail);

        for (int i = 0; i < resolvers.size(); i++) {
            resolvers.get(i).resolve(values.get(i));
        }

        done.assertSuccess();

    }

    @Test
    public void testMap_should_accept_a_promise_for_an_array() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        WhenProgress<List<? extends Integer>, Integer> w1 = new WhenProgress<>();
        ProgressPromise<List<? extends Integer>, Integer> input = w1.resolve(Arrays.asList(1, 2, 3));

        when.mapPromise(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                        Integer[] expected = {2, 4, 6};
                        assertArrayEquals(expected, value.toArray(new Integer[3]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_resolve_to_empty_array_when_input_is_empty_array() {

        List<Integer> input = new ArrayList<>();
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        when.mapValues(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                        assertNotNull(value);
                        assertEquals(0, value.size());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_reject_when_input_array_is_null() {

        List<Integer> input = null;
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        when.mapValues(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, Value<List<? extends Integer>>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(Value<List<? extends Integer>> value) {
                        assertNotNull(value.error);
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_map_input_promises_when_mapper_returns_a_promise() {

        Done<List<? extends Integer>, Integer> done = new Done<>();
        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.resolve(2), when.resolve(3));

        when.map(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, List<? extends Integer>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(List<? extends Integer> value) {
                        Integer[] expected = {2, 4, 6};
                        assertArrayEquals(expected, value.toArray(new Integer[3]));
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

    @Test
    public void testMap_should_reject_when_input_contains_rejection() {


        final WhenProgress<Integer, Integer> when = new WhenProgress<>();
        Done<List<? extends Integer>, Integer> done = new Done<>();

        final List<ProgressPromise<Integer, Integer>> input = Arrays.asList(when.resolve(1), when.reject(2), when.resolve(3));

        when.map(input, new Runnable<ProgressPromise<Integer, Integer>, Integer>() {
            @Override
            public ProgressPromise<Integer, Integer> run(Integer value) {
                return when.resolve(mapper.run(value));
            }
        }).then(
                fail.onSuccess,
                new Runnable<ProgressPromise<List<? extends Integer>, Integer>, Value<List<? extends Integer>>>() {
                    @Override
                    public ProgressPromise<List<? extends Integer>, Integer> run(Value<List<? extends Integer>> value) {
                        assertEquals(1, value.value.size());
                        assertEquals(2, value.value.get(0).intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();
    }

}
