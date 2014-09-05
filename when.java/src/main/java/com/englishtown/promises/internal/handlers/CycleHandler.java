package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.exceptions.CycleException;
import com.englishtown.promises.internal.PromiseHelper;

/**
 * Rejection handler for cyclic promises
 */
public class CycleHandler<T> extends RejectedHandler<T> {

    public CycleHandler(PromiseHelper helper) {
        super(new CycleException("Promise cycle"), helper);
    }

}
