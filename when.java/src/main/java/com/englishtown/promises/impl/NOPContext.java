package com.englishtown.promises.impl;

import com.englishtown.promises.Context;
import com.englishtown.promises.internal.handlers.Handler;

/**
 * Created by adriangonzalez on 8/27/14.
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
