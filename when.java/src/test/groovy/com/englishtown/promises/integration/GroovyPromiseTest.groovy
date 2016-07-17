package com.englishtown.promises.integration

import com.englishtown.promises.Deferred
import com.englishtown.promises.Done
import com.englishtown.promises.When
import com.englishtown.promises.WhenFactory

/**
 * Groovy unit tests
 */
class GroovyPromiseTest extends GroovyTestCase {

    When when;

    void setUp() {
        super.setUp()
        when = WhenFactory.createSync()
    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    void testPromise_then_return_value() {

        Deferred<Integer> d = when.defer()
        Done<Integer> done = new Done<>()

        def onFulfilled = { val -> 2 * val }

        when.resolve(d.getPromise())
                .then(onFulfilled)
                .then(onFulfilled)
                .then({ val -> when.resolve(2 * val) })
                .then(onFulfilled)
                .then(done.onFulfilled, done.onRejected)

        assertFalse(done.getFulfilled());

        d.resolve(1);
        done.assertFulfilled();
        assertEquals(16, done.getValue());

    }

    @SuppressWarnings("GroovyAssignabilityCheck")
    void testPromise_then_reject_return_value() {

        Deferred<Integer> d = when.defer()
        Done<Integer> done = new Done<>()

        when.resolve(d.getPromise())
                .otherwise({ t -> 10 })
                .then(done.onFulfilled, done.onRejected)

        assertFalse(done.getFulfilled());

        d.reject(new RuntimeException());
        done.assertFulfilled();
        assertEquals(10, done.getValue());

    }

}
