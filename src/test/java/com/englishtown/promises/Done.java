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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/30/13
 * Time: 2:41 AM
 *
 */
public class Done<TResolve, TProgress> {

    public boolean success;
    public boolean failed;

    public final Runnable<Promise<TResolve, TProgress>, TResolve> onSuccess = new SuccessCallback();
    public final Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onFail = new FailCallback();

    public void assertSuccess() {
        assertTrue(success);
        assertFalse(failed);
    }

    public void assertFailed() {
        assertTrue(failed);
        assertFalse(success);
    }

    private class SuccessCallback implements Runnable<Promise<TResolve, TProgress>, TResolve> {
        @Override
        public Promise<TResolve, TProgress> run(TResolve value) {
            success = true;
            return null;
        }
    }

    private class FailCallback implements Runnable<Promise<TResolve, TProgress>,
            Value<TResolve>> {
        @Override
        public Promise<TResolve, TProgress> run(Value<TResolve> value) {
            failed = true;
            return null;
        }
    }

}
