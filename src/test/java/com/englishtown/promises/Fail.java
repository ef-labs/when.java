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

import static org.junit.Assert.fail;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/30/13
 * Time: 5:32 AM
 */
public class Fail<TResolve, TProgress> {


    public final Runnable<ProgressPromise<TResolve, TProgress>, TResolve> onSuccess = new SuccessCallback();
    public final Runnable<ProgressPromise<TResolve, TProgress>, Value<TResolve>> onFail = new FailCallback();

    private class SuccessCallback implements Runnable<ProgressPromise<TResolve, TProgress>, TResolve> {
        @Override
        public ProgressPromise<TResolve, TProgress> run(TResolve value) {
            fail();
            return null;
        }
    }

    private class FailCallback implements Runnable<ProgressPromise<TResolve, TProgress>,
            Value<TResolve>> {
        @Override
        public ProgressPromise<TResolve, TProgress> run(Value<TResolve> value) {
            fail();
            return null;
        }
    }

}
