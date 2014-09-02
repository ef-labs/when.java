package com.englishtown.promises;

import com.englishtown.promises.internal.handlers.RejectedHandler;

/**
 * Created by adriangonzalez on 8/27/14.
 */
public interface Reporter {

    void onPotentiallyUnhandledRejection(RejectedHandler<?> rejection, Object context);

    void onPotentiallyUnhandledRejectionHandled(RejectedHandler<?> rejection);

    void onFatalRejection(RejectedHandler<?> rejection, Object context);

}
