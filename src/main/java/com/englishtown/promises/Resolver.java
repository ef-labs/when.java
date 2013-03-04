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
 * Resolves a promise
 *
 * @param <TResolve>  the resolved type
 * @param <TProgress> the progress type
 */
public interface Resolver<TResolve, TProgress> {

    /**
     * Resolves the promise to trigger onResolve handlers
     *
     * @param value the resolved value
     * @return an already-fulfilled {@link Promise} for the resolved value
     */
    Promise<TResolve, TProgress> resolve(TResolve value);

    /**
     * Resolves the promise to trigger onResolve handlers
     *
     * @param value a resolved {@link Promise}
     * @return an already-fulfilled {@link Promise} for the resolved value
     */
    Promise<TResolve, TProgress> resolve(Promise<TResolve, TProgress> value);

    /**
     * Rejects the promise to trigger onReject handlers
     *
     * @param reason the rejection reason
     * @return a rejected {@link Promise}
     */
    Promise<TResolve, TProgress> reject(TResolve reason);

    /**
     * Rejects the promise to trigger onReject handlers
     *
     * @param reason a rejected {@link Promise}
     * @return a rejected {@link Promise}
     */
    Promise<TResolve, TProgress> reject(Value<TResolve> reason);

    /**
     * Triggers the onProgress handlers with progress information
     *
     * @param update the progress information
     * @return either the same progress information or modified progress information.
     */
    Value<TProgress> notify(TProgress update);

    /**
     * Triggers the onProgress handlers with progress information which may contain an exception
     *
     * @param update the progress information
     * @return either the same progress information or modified progress information.
     */
    Value<TProgress> notify(Value<TProgress> update);

}
