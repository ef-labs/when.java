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
 * A value wrapper that can also hold error information for rejections and progress.
 *
 * @param <T> the type of value
 */
@SuppressWarnings("deprecation")
public class Value<T> {

    /**
     * Constructor for a value without an exception
     *
     * @param value the underlying value
     */
    public Value(T value) {
        this.value = value;
    }

    /**
     * Constructor for a value with an exception
     *
     * @param value the underlying value
     * @param error the associated exception
     */
    public Value(T value, Throwable error) {
        this.value = value;
        this.error = error;
    }

    /**
     * Constructor for a value with an exception
     *
     * @param error the associated exception
     */
    public Value(Throwable error) {
        this.error = error;
    }

    /**
     * The actual value (may be null)
     *
     * @deprecated The getValue() getter should be used instead.
     */
    @Deprecated
    public T value;

    /**
     * The associated exception (may be null)
     *
     * @deprecated The getCause() getter should be used instead.
     */
    @Deprecated()
    public Throwable error;

    /**
     * Returns the actual value (may be null)
     *
     * @return the underlying value
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the actual value (may be null)
     *
     * @return this
     */
    public Value<T> setValue(T value) {
        this.value = value;
        return this;
    }

    /**
     * The associated throwable (may be null)
     *
     * @return the underlying cause
     */
    public Throwable getCause() {
        return error;
    }

    /**
     * The associated throwable (may be null)
     *
     * @return the underlying cause
     */
    public Value<T> setCause(Throwable cause) {
        this.error = cause;
        return this;
    }

}
