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

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 2/4/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResolveTest {

    private Fail<Integer, Integer> fail = new Fail<>();

    @Test
    public void testResolve_should_resolve_an_immediate_value() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        when.resolve(expected).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(expected, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testResolve_should_resolve_a_resolved_promise() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        Deferred<Integer, Integer> d = when.defer();
        d.getResolver().resolve(expected);

        when.resolvePromise(d.getPromise()).then(
                new Runnable<Promise<Integer, Integer>, Integer>() {
                    @Override
                    public Promise<Integer, Integer> run(Integer value) {
                        assertEquals(expected, value.intValue());
                        return null;
                    }
                },
                fail.onFail
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

    @Test
    public void testResolve_should_reject_a_rejected_promise() {
        final int expected = 123;

        Done<Integer, Integer> done = new Done<>();
        When<Integer, Integer> when = new When<>();

        Deferred<Integer, Integer> d = when.defer();
        d.getResolver().reject(expected);

        when.resolvePromise(d.getPromise()).then(
                fail.onSuccess,
                new Runnable<Promise<Integer, Integer>, Value<Integer>>() {
                    @Override
                    public Promise<Integer, Integer> run(Value<Integer> value) {
                        assertEquals(expected, value.value.intValue());
                        return null;
                    }
                }
        ).then(done.onSuccess, done.onFail);

        done.assertSuccess();

    }

//            'should use valueOf immediate values': function(done) {
//        // See https://github.com/kriskowal/q/issues/106
//        var fake, expected;
//
//        expected = 1;
//        fake = {
//                valueOf: this.stub().returns(expected)
//        };
//
//        when.resolve(fake).then(
//                function(value) {
//            assert.equals(value, expected);
//        },
//        fail
//        ).always(done);
//    },
//
//            'should use valueOf foreign promises': function(done) {
//        // See https://github.com/kriskowal/q/issues/106
//        var fake, expected;
//
//        expected = 1;
//        fake = {
//                valueOf: function() {
//            return this;
//        },
//        then: function(cb) {
//            return cb(expected);
//        }
//        };
//
//        when.resolve(fake).then(
//                function(value) {
//            assert.equals(value, expected);
//        },
//        fail
//        ).always(done);
//    }

}
