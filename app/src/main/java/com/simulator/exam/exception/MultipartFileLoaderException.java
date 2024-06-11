package com.simulator.exam.exception;

public class MultipartFileLoaderException extends RuntimeException {

    private static final String MESSAGE = "Failed to load questions from multipart file %s";

    /**
     * Exception encountered during the MultiPart deserialization
     *
     * @param file the file name
     * @param cause the previous exception cause
     */
    public MultipartFileLoaderException(final String file, final Throwable cause) {
        super(String.format(MESSAGE, file), cause);
    }

}
