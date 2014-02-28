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

package com.englishtown.promises.monitor.impl;

import com.englishtown.promises.monitor.Aggregator;
import com.englishtown.promises.monitor.PromiseStatus;

/**
 * Created by adriangonzalez on 2/27/14.
 */
public class NOPAggregator implements Aggregator {

    private static final NOPPromiseStatus promiseStatus = new NOPPromiseStatus();

    @Override
    public PromiseStatus promiseStatus() {
        return promiseStatus;
    }

    @Override
    public void report() {
    }

    @Override
    public void reset() {
    }

    private static class NOPPromiseStatus implements PromiseStatus {

        @Override
        public long getKey() {
            return 0;
        }

        @Override
        public long getTimestamp() {
            return 0;
        }

        @Override
        public Throwable getCreatedAt() {
            return null;
        }

        @Override
        public Throwable getReason() {
            return null;
        }

        @Override
        public Throwable getRejectedAt() {
            return null;
        }

        @Override
        public PromiseStatus observed() {
            return null;
        }

        @Override
        public void fulfilled() {
        }

        @Override
        public void rejected(Throwable reason) {
        }

    }

}
