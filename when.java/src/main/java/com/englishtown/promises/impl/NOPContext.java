package com.englishtown.promises.impl;

import com.englishtown.promises.Context;
import com.englishtown.promises.internal.handlers.Handler;

/**
 * No operation {@link com.englishtown.promises.Context} implementation
 */
public class NOPContext implements Context {

    @Override
    public void createContext(Handler<?> handler, Object parentContext) {
    }

    @Override
    public void enterContext(Handler<?> handler) {
    }

    @Override
    public void exitContext() {
    }

}
