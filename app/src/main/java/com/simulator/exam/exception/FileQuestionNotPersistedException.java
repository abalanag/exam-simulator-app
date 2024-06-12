package com.simulator.exam.exception;

public class FileQuestionNotPersistedException extends RuntimeException {
    public FileQuestionNotPersistedException(final String message, final String question) {
        super(String.format(message, question));
    }
}
