package com.simulator.exam.exception;

public class DuplicateAnswerException extends RuntimeException {

    /**
     * Exception thrown when two similar answers are found
     *
     * @param message the exception message
     * @param option the duplicated option
     * @param question the question for which the answer is applied
     * @param throwable the exception
     */
    public DuplicateAnswerException(final String message, final String option, final String question, final Throwable throwable) {
        super(String.format(message, option, question), throwable);
    }
}
