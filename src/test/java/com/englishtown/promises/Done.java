package com.englishtown.promises;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/30/13
 * Time: 2:41 AM
 * To change this template use File | Settings | File Templates.
 */
public class Done<TResolve, TProgress> {

    public boolean success;
    public boolean failed;

    public Runnable<Promise<TResolve, TProgress>, TResolve> onSuccess = new SuccessCallback();
    public Runnable<Promise<TResolve, TProgress>, Value<TResolve>> onFail = new FailCallback();

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
