package com.englishtown.promises;

import com.englishtown.promises.internal.handlers.Handler;

/**
 * Created by adriangonzalez on 8/27/14.
 */
public interface Context {

    default void createContext(Handler<?> handler) {
        createContext(handler, null);
    }

    void createContext(Handler<?> handler, Object parentContext);

    void enterContext(Handler<?> handler);

    void exitContext();

}
