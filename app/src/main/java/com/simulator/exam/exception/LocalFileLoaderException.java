package com.simulator.exam.exception;

public class LocalFileLoaderException extends RuntimeException {

    private static final String MESSAGE = "Unknown error while processing local file %s, path %s, module %s";

    /**
     * Exception thrown when a issue appeared during the deserialization of the object
     *
     * @param cause the previous exception cause
     * @param file the file name
     * @param path the file path
     * @param moduleName the module name
     */
    public LocalFileLoaderException(final String file, final String path,
            final String moduleName, final Throwable cause) {
        super(String.format(MESSAGE, file, path, moduleName), cause);
    }
}
