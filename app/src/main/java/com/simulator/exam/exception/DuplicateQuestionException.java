package com.simulator.exam.exception;

public class DuplicateQuestionException extends RuntimeException {

    public DuplicateQuestionException(final String message) {
        super(message);
    }
}
