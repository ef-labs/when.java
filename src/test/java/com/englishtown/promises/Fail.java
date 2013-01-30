package com.englishtown.promises;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: adriangonzalez
 * Date: 1/30/13
 * Time: 5:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class Fail<TResolve, TProgress> {


    public Runnable<Promise<TResolve, TProgress>, TResolve> onSuccess = new SuccessCallback();
    public Runnable<Promise<TResolve, TProgress>, Reason<TResolve>> onFail = new FailCallback();

    private class SuccessCallback implements Runnable<Promise<TResolve, TProgress>, TResolve> {
        @Override
        public Promise<TResolve, TProgress> run(TResolve value) {
            fail();
            return null;
        }
    }

    private class FailCallback implements Runnable<Promise<TResolve, TProgress>,
            Reason<TResolve>> {
        @Override
        public Promise<TResolve, TProgress> run(Reason<TResolve> value) {
            fail();
            return null;
        }
    }

}
