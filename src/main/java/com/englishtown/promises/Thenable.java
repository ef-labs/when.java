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
 * A Thenable object allows registering callbacks for when a promise resolves, rejects, or receives progress.
 *
 * @param <TResolve>  the type of data received when resolved or rejected
 * @param <TProgress> the type of data received when there's progress
 */
public interface Thenable<TResolve, TProgress> {

    /**
     * Registers callbacks for when a promise resolves, rejects or receives progress
     *
     * @param onFulfilled resolution handler
     * @param onRejected  rejection handler
     * @param onProgress  progress handler
     * @return a new {@link Promise} to allow chaining callback registration
     */
    public Promise<TResolve, TProgress> then(
            Runnable<Promise<TResolve, TProgress>, TResolve> onFulfilled,
            Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onRejected,
            Runnable<Value<TProgress>, Value<TProgress>> onProgress);
}
