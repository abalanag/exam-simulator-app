package com.simulator.exam.exception;

public class DuplicateQuestionException extends RuntimeException {

    /**
     * Exception thrown when a question is already persisted
     * @param message the exception message
     * @param question the duplicated question
     */
    public DuplicateQuestionException(final String message, final String question) {
        super(String.format(message, question));
    }
}
