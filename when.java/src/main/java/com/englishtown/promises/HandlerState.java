package com.englishtown.promises;

/**
 * Promise handler states
 */
public enum HandlerState {
    REJECTED(-1),
    PENDING(0),
    FULFILLED(1);

    private final int value;

    private HandlerState(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

