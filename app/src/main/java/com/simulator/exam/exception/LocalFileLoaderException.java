package com.simulator.exam.exception;

public class LocalFileLoaderException extends RuntimeException {

    private static final String MESSAGE = "Unknown error while processing local file %s, module %s";

    /**
     * Exception thrown when an issue appeared during the deserialization of the object
     *
     * @param cause      the previous exception cause
     * @param file       the file name and path
     * @param moduleName the module name
     */
    public LocalFileLoaderException(final String file, final String moduleName, final Throwable cause) {
        super(String.format(MESSAGE, file, moduleName), cause);
    }
}
