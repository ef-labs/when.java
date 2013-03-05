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

/**
 * Extends the standard {@link Promise} with additional functionality
 *
 * @param <TResolve>  the type passed to fulfillment or rejection handlers
 * @param <TProgress> the type passed to progress handlers
 */
public interface PromiseExt<TResolve, TProgress> extends Promise<TResolve, TProgress> {

    /**
     * Register a callback that will be called when a promise is
     * fulfilled or rejected.
     * <p/>
     * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected, onProgress)
     *
     * @param onFulfilledOrRejected a callback for when a promise is fulfilled or rejected
     * @return a new {@link Promise}
     */
    Promise<TResolve, TProgress> always(
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected);

    /**
     * Register a callback that will be called when a promise is
     * fulfilled or rejected.  Also register a progress handler.
     * <p/>
     * Shortcut for .then(onFulfilledOrRejected, onFulfilledOrRejected, onProgress)
     *
     * @param onFulfilledOrRejected a callback for when a promise is fulfilled or rejected
     * @param onProgress            a callback for progress notifications
     * @return a new {@link Promise}
     */
    Promise<TResolve, TProgress> always(
            final Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilledOrRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress);

    /**
     * Register a rejection handler.  Shortcut for .then(null, onRejected)
     *
     * @param onRejected rejection handler
     * @return a new {@link Promise}
     */
    Promise<TResolve, TProgress> otherwise(Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected);

    /**
     * Shortcut for .then(function() { return resolve(value); })
     *
     * @param value the value to be returned
     * @return an already-fulfilled {@link Promise}
     */
    Promise<TResolve, TProgress> yield(TResolve value);

    /**
     * Shortcut for .then(function() { return resolve(value); })
     *
     * @param promise the promise to be returned
     * @return an {@link Promise} that fulfill with its value or reject with its reason.
     */
    Promise<TResolve, TProgress> yield(Thenable<TResolve, TProgress> promise);

}
