package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.Reporter;
import com.englishtown.promises.State;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.internal.Continuation;
import com.englishtown.promises.internal.PromiseHelper;
import com.englishtown.promises.internal.TrustedPromise;

import static com.englishtown.promises.HandlerState.REJECTED;

/**
 * Handler for a rejected promise
 */
public class RejectedHandler<T> extends Handler<T> {

    private final Throwable value;
    private final Reporter reporter;

    //    private long id;
    private boolean handled;
    private boolean reported;

//    private static final AtomicLong idCounter = new AtomicLong();

    public RejectedHandler(Throwable x, PromiseHelper helper) {
        super(helper);
        this.reporter = helper.getReporter();
        this._state = REJECTED;
        helper.getContext().createContext(this);

//        TODO: id?
//        this.id = idCounter.incrementAndGet();
        this.value = x;

        this.handled = false;
        this.reported = false;

        this._report(null);

    }

    @Override
    public State<T> inspect() {
        return new State<>(_state, this.value);
    }

    @Override
    public void when(Continuation<T, ?> cont) {
        Thenable<?> x;

        if (cont.rejected != null) {
            this._unreport();
            helper.getContext().enterContext(this);
            x = helper.tryCatchReject(cont.rejected, this.value);
            helper.getContext().exitContext();
        } else {
            x = new TrustedPromise<>(this, helper);
        }

        //noinspection unchecked
        ((Continuation<T, Object>) cont).resolve.accept((Thenable<Object>) x);
    }

    @Override
    protected void _report(Object context) {
        helper.getScheduler().afterQueue(this::reportUnhandled);
    }

    @Override
    protected void _unreport() {
        this.handled = true;
        helper.getScheduler().afterQueue(this::reportHandled);
    }

    @Override
    public void _fatal(Object context) {
        reporter.onFatalRejection(this, context);
    }

    private void reportUnhandled() {
        if (!handled) {
            reported = true;
            reporter.onPotentiallyUnhandledRejection(this, context);
        }
    }

    private void reportHandled() {
        if (reported) {
            reporter.onPotentiallyUnhandledRejectionHandled(this);
        }
    }

//    public long getId() {
//        return this.id;
//    }

    public Throwable getValue() {
        return value;
    }

    public boolean handled() {
        return this.handled;
    }

}
