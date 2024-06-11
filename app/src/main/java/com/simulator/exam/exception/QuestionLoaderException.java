package com.simulator.exam.exception;

public class QuestionLoaderException extends RuntimeException {

    /**
     * Exception thrown if now question have been loaded.
     *
     * @param message the exception message
     */
    public QuestionLoaderException(final String message) {
        super(message);
    }
}
