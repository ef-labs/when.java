package com.englishtown.promises.exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adriangonzalez on 8/20/14.
 */
public class RejectException extends RuntimeException {

    private final List<Throwable> innerExceptions = new ArrayList<>();
    private Object value;

    public RejectException() {
    }

    public RejectException(String message) {
        super(message);
    }

    public RejectException(String message, Throwable cause) {
        super(message, cause);
        addInnerException(cause);
    }

    public RejectException(String message, List<Throwable> innerExceptions) {
        super(message);
        if (innerExceptions != null) {
            this.innerExceptions.addAll(innerExceptions);
        }
    }

    public RejectException addInnerException(Throwable inner) {
        if (inner != null) {
            innerExceptions.add(inner);
        }
        return this;
    }

    public List<Throwable> getInnerExceptions() {
        return innerExceptions;
    }

    public RejectException setValue(Object value) {
        this.value = value;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T) this.value;
    }
}
