package com.englishtown.promises;

/**
 * The current state of a promise
 */
public class State<T> {

    private final HandlerState state;
    private T value;
    private Throwable reason;

    public State(HandlerState state) {
        this.state = state;
    }

    public State(HandlerState state, T value) {
        this(state);
        this.value = value;
    }

    public State(HandlerState state, Throwable reason) {
        this(state);
        this.reason = reason;
    }

    public HandlerState getState() {
        return this.state;
    }

    public T getValue() {
        return this.value;
    }

    public Throwable getReason() {
        return this.reason;
    }
}
