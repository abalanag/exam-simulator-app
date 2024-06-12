package com.simulator.exam.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.exception.DuplicateAnswerException;
import com.simulator.exam.exception.DuplicateQuestionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class QuestionExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger("application.logger");

    @ExceptionHandler(DuplicateQuestionException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateQuestionException(final DuplicateQuestionException ex) {
        LOGGER.log(Level.WARNING, "Exception encountered during questions persistence", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicateAnswerException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateQuestionException(final DuplicateAnswerException ex) {
        LOGGER.log(Level.WARNING, "Exception encountered during answer persistence", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.CONFLICT.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}