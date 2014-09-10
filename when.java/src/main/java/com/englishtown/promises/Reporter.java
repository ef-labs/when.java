package com.englishtown.promises;

import com.englishtown.promises.internal.handlers.RejectedHandler;

/**
 * Reporter for unhandled rejections
 */
public interface Reporter {

    void onPotentiallyUnhandledRejection(RejectedHandler<?> rejection, Object context);

    void onPotentiallyUnhandledRejectionHandled(RejectedHandler<?> rejection);

    void onFatalRejection(RejectedHandler<?> rejection, Object context);

}
