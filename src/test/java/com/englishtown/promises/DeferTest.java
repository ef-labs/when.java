package com.englishtown.promises;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/30/13
 * Time: 2:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class DeferTest {

    private final Object sentinel = new Object();

    @Test
    public void testResolve_should_fulfill_with_an_immediate_value() {

        Done<Object, Object> done = new Done<>();
        Deferred<Object, Object> d = When.defer();

        Promise<Object, Object> p = d.getPromise().then(
                new Runnable<Promise<Object, Object>, Object>() {
                    @Override
                    public Promise<Object, Object> run(Object value) {
                        assertEquals(value, sentinel);
                        return null;
                    }
                },
                done.onFail,
                null).then(done.onSuccess, done.onFail, null);

        d.getResolver().resolve(sentinel);
        done.assertSuccess();

    }

    private class FakeResolved<TResolve, TProgress> implements Promise<TResolve, TProgress> {

        private TResolve value;

        public FakeResolved(TResolve value) {
            this.value = value;
        }

        @Override
        public Promise<TResolve, TProgress> then(
                Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
                Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onRejected,
                Runnable<TProgress, TProgress> onProgress) {

            if (onFulfilled != null) {
                return onFulfilled.run(this.value);
            } else {
                return new FakeResolved<>(value);
            }
        }
    }

//    function fakeResolved(val) {
//        return {
//                then: function(callback) {
//            return fakeResolved(callback ? callback(val) : val);
//        }
//        };
//    }

//    function fakeRejected(reason) {
//        return {
//                then: function(callback, errback) {
//            return errback ? fakeResolved(errback(reason)) : fakeRejected(reason);
//        }
//        };
//    }

//    @Test
//    public void testResolve_should_fulfill_with_fulfilled_promised() {
//        Deferred<Object, Object> d = When.defer();
//        Done<Object, Object> done = new Done<>();
//
//        d.getPromise().then(
//                new Runnable<Promise<Object, Object>, Object>() {
//                    @Override
//                    public Promise<Object, Object> run(Object value) {
//                        assertEquals(value, sentinel);
//                        return null;
//                    }
//                },
//                done.onFail,
//                null).then(done.onSuccess, done.onFail, null);
//
//        d.getResolver().resolve(fakeResolved(sentinel));
//    }

//
//    @Test
//    public void testResolve_should_reject_with_rejected_promise() {
//        var d = when.defer();
//
//        d.promise.then(
//                fail,
//                function(val) {
//            assert.same(val, sentinel);
//        }
//        ).always(done);
//
//        d.resolve(fakeRejected(sentinel));
//    }
//
//    @Test
//    public void testResolve_should_return_a_promise_for_the_resolution_value() {
//        var d = when.defer();
//
//        d.resolve(sentinel).then(
//                function(returnedPromiseVal) {
//            d.promise.then(function(val) {
//                assert.same(returnedPromiseVal, val);
//            });
//        },
//        fail
//        ).always(done);
//    }
//
//    @Test
//    public void testResolve_should_return_a_promise_for_a_promised_resolution_value() {
//        var d = when.defer();
//
//        d.resolve(when.resolve(sentinel)).then(
//                function(returnedPromiseVal) {
//            d.promise.then(function(val) {
//                assert.same(returnedPromiseVal, val);
//            });
//        },
//        fail
//        ).always(done);
//    }
//
//    @Test
//    public void testResolve_should_return_a_promise_for_a_promised_rejection_value() {
//        var d = when.defer();
//
//        // Both the returned promise, and the deferred's own promise should
//        // be rejected with the same value
//        d.resolve(when.reject(sentinel)).then(
//                fail,
//                function(returnedPromiseVal) {
//            d.promise.then(
//                    fail,
//                    function(val) {
//                assert.same(returnedPromiseVal, val);
//            }
//            );
//        }
//        ).always(done);
//    }
//
//    @Test
//    public void testResolve_should_invoke_newly_added_callback_when_already_resolved() {
//        var d = when.defer();
//
//        d.resolve(sentinel);
//
//        d.promise.then(
//                function(val) {
//            assert.same(val, sentinel);
//            done();
//        },
//        fail
//        ).always(done);
//    }
//
//}
//
//
//    @Test
//    public void testReject_should_reject() {
//        var d = when.defer();
//
//        d.promise.then(
//                fail,
//                function(val) {
//            assert.same(val, sentinel);
//        }
//        ).always(done);
//
//        d.reject(sentinel);
//    }
//
//    @Test
//    public void testReject_should_return_a_promise_for_the_rejection_value() {
//        var d = when.defer();
//
//// Both the returned promise, and the deferred's own promise should
//// be rejected with the same value
//        d.reject(sentinel).then(
//                fail,
//                function(returnedPromiseVal) {
//            d.promise.then(
//                    fail,
//                    function(val) {
//                assert.same(returnedPromiseVal, val);
//            }
//            );
//        }
//        ).always(done);
//    }
//
//    @Test
//    public void testReject_should_invoke_newly_added_errback_when_already_rejected() {
//        var d = when.defer();
//
//        d.reject(sentinel);
//
//        d.promise.then(
//                fail,
//                function(val) {
//            assert.equals(val, sentinel);
//        }
//        ).always(done);
//    }
//
//    @Test
//    public void testProgress_should_progress() {
//        var d = when.defer();
//
//        d.promise.then(
//                fail,
//                fail,
//                function(val) {
//            assert.same(val, sentinel);
//            done();
//        }
//        );
//
//        d.progress(sentinel);
//    }
//
//    @Test
//    public void testProgress_should_propagate_progress_to_downstream_promises() {
//        var d = when.defer();
//
//        d.promise
//                .then(fail, fail,
//                        function(update) {
//            return update;
//        }
//        )
//        .then(fail, fail,
//                function(update) {
//            assert.same(update, sentinel);
//            done();
//        }
//        );
//
//        d.progress(sentinel);
//    }
//
//    @Test
//    public void testProgress_should_propagate_transformed_progress_to_downstream_promises() {
//        var d = when.defer();
//
//        d.promise
//                .then(fail, fail,
//                        function() {
//            return sentinel;
//        }
//        )
//        .then(fail, fail,
//                function(update) {
//            assert.same(update, sentinel);
//            done();
//        }
//        );
//
//        d.progress(other);
//    }
//
//    @Test
//    public void testProgress_should_propagate_caught_exception_value_as_progress() {
//        var d = when.defer();
//
//        d.promise
//                .then(fail, fail,
//                        function() {
//            throw sentinel;
//        }
//        )
//        .then(fail, fail,
//                function(update) {
//            assert.same(update, sentinel);
//            done();
//        }
//        );
//
//        d.progress(other);
//    }
//
//    @Test
//    public void
//    testProgress_should_forward_progress_events_when_intermediary_callback_tied_to_a_resolved_promise_returns_a_promise() {
//        var d, d2;
//
//        d = when.defer();
//        d2 = when.defer();
//
//// resolve d BEFORE calling attaching progress handler
//        d.resolve();
//
//        d.promise.then(
//                function() {
//            return d2.promise;
//        }
//        ).then(fail, fail,
//                function(update) {
//            assert.same(update, sentinel);
//            done();
//        }
//        );
//
//        d2.progress(sentinel);
//    }
//
//    @Test
//    public void
//    testProgress_should_forward_progress_events_when_intermediary_callback_tied_to_an_unresovled_promise_returns_a_promise() {
//        var d, d2;
//
//        d = when.defer();
//        d2 = when.defer();
//
//        d.promise.then(
//                function() {
//            return d2.promise;
//        }
//        ).then(fail, fail,
//                function(update) {
//            assert.same(update, sentinel);
//            done();
//        }
//        );
//
//// resolve d AFTER calling attaching progress handler
//        d.resolve();
//        d2.progress(sentinel);
//    }
//
//    @Test
//    public void testProgress_should_forward_progress_when_resolved_with_another_promise() {
//        var d, d2;
//
//        d = when.defer();
//        d2 = when.defer();
//
//        d.promise
//                .then(fail, fail,
//                        function() {
//            return sentinel;
//        }
//        )
//        .then(fail, fail,
//                function(update) {
//            assert.same(update, sentinel);
//            done();
//        }
//        );
//
//        d.resolve(d2.promise);
//
//        d2.progress();
//    }


//        'should allow resolve after progress':function(done){
//        var d=when.defer();
//
//var progressed=false;
//d.promise.then(
//        function(){
//        assert(progressed);
//done();
//},
//        fail,
//        function(){
//        progressed=true;
//}
//        );
//
//d.progress();
//d.resolve();
//},
//
//        'should allow reject after progress':function(done){
//        var d=when.defer();
//
//var progressed=false;
//d.promise.then(
//        fail,
//        function(){
//        assert(progressed);
//done();
//},
//        function(){
//        progressed=true;
//}
//        );
//
//d.progress();
//d.reject();
//}
//        },
//
//        'should return a promise for passed-in resolution value when already resolved':function(done){
//        var d=when.defer();
//d.resolve(other);
//
//d.resolve(sentinel).then(function(val){
//        assert.same(val,sentinel);
//}).always(done);
//},
//
//        'should return a promise for passed-in rejection value when already resolved':function(done){
//        var d=when.defer();
//d.resolve(other);
//
//d.reject(sentinel).then(
//        fail,
//        function(val){
//        assert.same(val,sentinel);
//}
//        ).always(done);
//},
//
//        'should return silently on progress when already resolved':function(){
//        var d=when.defer();
//d.resolve();
//
//refute.defined(d.progress());
//},
//
//        'should return a promise for passed-in resolution value when already rejected':function(done){
//        var d=when.defer();
//d.reject(other);
//
//d.resolve(sentinel).then(function(val){
//        assert.same(val,sentinel);
//}).always(done);
//},
//
//        'should return a promise for passed-in rejection value when already rejected':function(done){
//        var d=when.defer();
//d.reject(other);
//
//d.reject(sentinel).then(
//        fail,
//        function(val){
//        assert.same(val,sentinel);
//}
//        ).always(done);
//},
//
//        'should return silently on progress when already rejected':function(){
//        var d=when.defer();
//d.reject();
//
//refute.defined(d.progress());
//}

}
