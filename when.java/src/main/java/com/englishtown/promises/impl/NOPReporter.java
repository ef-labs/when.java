package com.englishtown.promises.impl;

import com.englishtown.promises.Reporter;
import com.englishtown.promises.internal.handlers.RejectedHandler;

/**
 * Created by adriangonzalez on 8/27/14.
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
