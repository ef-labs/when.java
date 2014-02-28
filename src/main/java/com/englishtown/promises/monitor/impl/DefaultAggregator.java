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
import com.englishtown.promises.monitor.Reporter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Default {@link com.englishtown.promises.monitor.Aggregator} implementation
 */
public class DefaultAggregator implements Aggregator {

    private final Reporter reporter;
    private Map<Long, PromiseStatus> promises;
    private AtomicLong nextKey;

    public DefaultAggregator(Reporter reporter) {
        this.reporter = reporter;
        reset();
    }

    @Override
    public PromiseStatus promiseStatus() {
        return new PromiseStatusImpl(this);
    }

    private class PromiseStatusImpl implements PromiseStatus {
        private long key;
        private long timestamp;
        private Aggregator parent;
        private Throwable createdAt;
        private Throwable reason;
        private Throwable rejectedAt;

        public PromiseStatusImpl(Aggregator parent) {
//        if(!(this instanceof PromiseStatus)) {
//            return new PromiseStatus(parent);
//        }

            Error stackHolder;

            try {
                throw new Error();
            } catch (Error e) {
                stackHolder = e;
            }

            // TODO: Arithmetic overflow
            this.key = nextKey.incrementAndGet();
            promises.put(this.key, this);

            this.parent = parent;
            this.timestamp = System.currentTimeMillis();
            this.createdAt = stackHolder;

        }

        @Override
        public long getKey() {
            return key;
        }

        @Override
        public long getTimestamp() {
            return timestamp;
        }

        @Override
        public Throwable getCreatedAt() {
            return createdAt;
        }

        @Override
        public Throwable getReason() {
            return reason;
        }

        @Override
        public Throwable getRejectedAt() {
            return rejectedAt;
        }

        @Override
        public PromiseStatus observed() {
            PromiseStatus status = promises.remove(key);
            if (status != null) {
                report();
            }
            return new PromiseStatusImpl(parent);
        }

        @Override
        public void fulfilled() {
            PromiseStatus status = promises.remove(key);
            if (status != null) {
                report();
            }
        }

        @Override
        public void rejected(Throwable reason) {
            PromiseStatus status = promises.remove(key);
            if (status != null) {
                Throwable stackHolder;

                try {
                    throw new Error(reason);
                } catch (Throwable e) {
                    stackHolder = e;
                }

                this.reason = reason;
                this.rejectedAt = stackHolder;
                report();

            }
        }

    }

    // TODO:
//    return publish({ publish: publish });

//    public void publish(target) {
//        target.PromiseStatus = PromiseStatus;
//        target.reportUnhandled = report;
//        target.resetUnhandled = reset;
//        return target;
//    }

    @Override
    public void report() {
        reporter.report(promises);
    }

    @Override
    public void reset() {
        nextKey = new AtomicLong();
        promises = new HashMap<>(); // Should be WeakMap
    }

}
