package com.englishtown.promises.exceptions;

/**
 * Exception thrown when cyclic promises detected
 */
public class CycleException extends RuntimeException {

    public CycleException(String message) {
        super(message);
    }

}
