package com.englishtown.promises.exceptions;

/**
 * Created by adriangonzalez on 8/20/14.
 */
public class CycleException extends RuntimeException {

    public CycleException(String message) {
        super(message);
    }

}
