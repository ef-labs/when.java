package com.englishtown.promises.impl;

import com.englishtown.promises.Reporter;
import com.englishtown.promises.internal.handlers.RejectedHandler;

/**
 * No operation {@link com.englishtown.promises.Reporter} implementation
 */
public class NOPReporter implements Reporter {

    @Override
    public void onPotentiallyUnhandledRejection(RejectedHandler<?> rejection, Object context) {
    }

    @Override
    public void onPotentiallyUnhandledRejectionHandled(RejectedHandler<?> rejection) {
    }

    @Override
    public void onFatalRejection(RejectedHandler<?> rejection, Object context) {
    }

}
