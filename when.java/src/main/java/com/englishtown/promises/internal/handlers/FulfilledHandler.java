package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.State;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.internal.Continuation;
import com.englishtown.promises.internal.PromiseHelper;

import static com.englishtown.promises.HandlerState.FULFILLED;

/**
 * Handler for a fulfilled promise
 */
public class FulfilledHandler<T> extends Handler<T> {

    protected final T value;

    public FulfilledHandler(T x, PromiseHelper helper) {
        super(helper);
        _state = FULFILLED;
        helper.getContext().createContext(this);

        this.value = x;
    }

    @Override
    public State<T> inspect() {
        return new State<>(_state, this.value);
    }

    @Override
    public void when(Continuation<T, ?> cont) {
        Thenable<?> x;

        if (cont.fulfilled != null) {
            helper.getContext().enterContext(this);
            x = helper.tryCatchReject(cont.fulfilled, this.value);
            helper.getContext().exitContext();
        } else {
//            x = this.value; // TODO: More efficient way to handle this than just resolving?
            x = helper.resolve(this.value);
        }

        //noinspection unchecked
        ((Continuation<T, Object>) cont).resolve.accept((Thenable<Object>) x);
    }

    public T getValue() {
        return value;
    }
}
