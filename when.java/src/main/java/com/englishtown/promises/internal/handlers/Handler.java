package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.HandlerState;
import com.englishtown.promises.State;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.internal.Continuation;
import com.englishtown.promises.internal.PromiseHelper;

import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.englishtown.promises.HandlerState.PENDING;

/**
 * Abstract promise handler
 */
public abstract class Handler<T> {

    protected final PromiseHelper helper;
    protected Handler<T> handler;
    public Object context;
    protected HandlerState _state;

    public Handler(PromiseHelper helper) {
        this.helper = helper;
        _state = PENDING;
    }

    public void when(Continuation<T, ?> continuation) {
    }

    public void resolve(T x) {
    }

    public void resolve(Thenable<T> x) {
    }

    public void reject(Throwable cause) {
    }

//    public void _notify(Object x) {} // TODO: added "_" to notify()

    public void _fatal(Object context) {
    }

    protected void _unreport() {
    }

    protected void _report(Object context) {
    }

    public State<T> inspect() {
        return toPendingState();
    }

    /**
     * Creates a pending state snapshot
     *
     * @returns {{state:'pending'}}
     */
    protected State<T> toPendingState() {
        return new State<>(PENDING);
    }

    public HandlerState state() {
        return this._state;
    }

    /**
     * Recursively collapse handler chain to find the handler
     * nearest to the fully resolved value.
     *
     * @returns {object} handler nearest the fully resolved value
     */
    public Handler<T> join() {
        Handler<T> h = this;
        while (h.handler != null) {
            h = h.handler;
        }
        return h;
    }

    public void chain(Consumer<T> fulfilled, Consumer<Throwable> rejected) {

        Continuation<T, ?> cont = new Continuation<>();
        cont.resolve = (x) -> {
        };
        cont.context = null;
        cont.fulfilled = fulfilled == null ? null : (x) -> {
            fulfilled.accept(x);
            return null;
        };
        cont.rejected = rejected == null ? null : (x) -> {
            rejected.accept(x);
            return null;
        };

        this.when(cont);
    }

    public void map(Consumer<T> f, Handler<?> to) {
        this.chain(f, to::reject);
    }

    public void catchError(Consumer<Throwable> f, Handler<T> to) {
        this.chain(to::resolve, f);
    }

    public <U, V> void fold(Handler<V> to, BiFunction<U, T, ? extends Thenable<V>> f, Thenable<U> z) {
        this.join().map((x) -> {
            helper.<U>getHandler(z).map((z1) -> {
                to.resolve(helper.tryCatchReject2(f, z1, x));
            }, to);
        }, to);
    }

}
