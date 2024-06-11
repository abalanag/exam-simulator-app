package com.simulator.exam.exception;

public class LocalFileLoaderException extends RuntimeException {

    public LocalFileLoaderException() {
    }

    public LocalFileLoaderException(final String message) {
        super(message);
    }

    public LocalFileLoaderException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
