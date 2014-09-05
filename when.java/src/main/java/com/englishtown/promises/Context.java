package com.englishtown.promises;

import com.englishtown.promises.internal.handlers.Handler;

/**
 * Promise context methods
 */
public interface Context {

    default void createContext(Handler<?> handler) {
        createContext(handler, null);
    }

    void createContext(Handler<?> handler, Object parentContext);

    void enterContext(Handler<?> handler);

    void exitContext();

}
