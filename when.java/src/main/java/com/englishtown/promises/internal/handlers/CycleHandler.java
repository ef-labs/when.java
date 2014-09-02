package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.exceptions.CycleException;
import com.englishtown.promises.internal.PromiseHelper;

/**
 * Created by adriangonzalez on 8/15/14.
 */
public class CycleHandler<T> extends RejectedHandler<T> {

    public CycleHandler(PromiseHelper helper) {
        super(new CycleException("Promise cycle"), helper);
    }

}
