package com.englishtown.promises.internal.handlers;

import com.englishtown.promises.State;
import com.englishtown.promises.Thenable;
import com.englishtown.promises.internal.Continuation;
import com.englishtown.promises.internal.ContinuationTask;
import com.englishtown.promises.internal.PromiseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler that manages a queue of consumers waiting on a pending promise
 */
public class DeferredHandler<T> extends Handler<T> implements Runnable {

    private boolean resolved;
    private List<Continuation<T, ?>> consumers;
    private final Object lock = new Object();

    public DeferredHandler(PromiseHelper helper, Object inheritedContext) {
        super(helper);
        helper.getContext().createContext(this, inheritedContext);

//        this.receiver = receiver;
        this.handler = null;
        this.resolved = false;

    }

    @Override
    public State<T> inspect() {
        return this.resolved ? this.join().inspect() : toPendingState();
    }

    @Override
    public void resolve(T x) {
        resolve0(x);
    }

    @Override
    public void resolve(Thenable<T> x) {
        resolve0(x);
    }

    private void resolve0(Object x) {
        if (!this.resolved) {
            this.become(helper.getHandler(x));
        }
    }

    @Override
    public void reject(Throwable x) {
        if (!this.resolved) {
            this.become(new RejectedHandler<>(x, helper));
        }
    }

    @Override
    public Handler<T> join() {
        if (this.resolved) {
            Handler<T> h = this;
            while (h.handler != null) {
                h = h.handler;
                if (h == this) {
                    return this.handler = new CycleHandler<>(helper);
                }
            }
            return h;
        } else {
            return this;
        }
    }

    @Override
    public void run() {
        List<Continuation<T, ?>> q = this.consumers;

        Handler<T> handler = this.join();
        synchronized (lock) {
            this.consumers = null;
        }

        q.forEach(handler::when);
    }

    public void become(Handler<T> handler) {
        this.resolved = true;
        this.handler = handler;
        if (this.consumers != null) {
            helper.getScheduler().enqueue(this);
        }

        if (this.context != null) {
            handler._report(this.context);
        }
    }

    @Override
    public void when(Continuation<T, ?> continuation) {
        if (this.resolved) {
            helper.getScheduler().enqueue(new ContinuationTask<>(continuation, this.handler));
        } else {
            synchronized (lock) {
                if (this.consumers == null) {
                    this.consumers = new ArrayList<>();
                }
                this.consumers.add(continuation);
            }
        }
    }

//    DeferredHandler.prototype.notify = function (x) {
//        if (!this.resolved) {
//            tasks.enqueue(new ProgressTask(this, x));
//        }
//    };


    @Override
    protected void _report(Object context) {
        if (this.resolved) this.handler.join()._report(context);
    }

    @Override
    protected void _unreport() {
        if (this.resolved) this.handler.join()._unreport();
    }

    @Override
    public void _fatal(Object context) {
        Object c = context == null ? this.context : context;
        if (this.resolved) this.handler.join()._fatal(c);
    }

}
