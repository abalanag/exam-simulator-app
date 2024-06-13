package com.simulator.exam.exception;

public class LocalFileNotFoundException extends RuntimeException {

    private static final String MESSAGE = "File %s was not found";

    /**
     * Exception thrown when a local file can't be found
     *
     * @param fileName the name o the file and path
     * @param cause    the previous exception cause
     */
    public LocalFileNotFoundException(final String fileName, final Throwable cause) {
        super(String.format(MESSAGE, fileName), cause);
    }
}
