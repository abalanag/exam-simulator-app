package com.simulator.exam.exception;

public class QuestionParserException extends RuntimeException {

    public QuestionParserException(final String message) {
        super(message);
    }

    public QuestionParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
