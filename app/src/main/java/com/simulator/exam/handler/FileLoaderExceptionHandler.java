package com.simulator.exam.handler;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.simulator.exam.exception.LocalFileLoaderException;
import com.simulator.exam.exception.LocalFileNotFoundException;
import com.simulator.exam.exception.MultipartFileLoaderException;
import com.simulator.exam.exception.QuestionLoaderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.yaml.snakeyaml.constructor.ConstructorException;

@ControllerAdvice
public class FileLoaderExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger("application.logger");

    @ExceptionHandler(LocalFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleFileNotFoundException(final LocalFileNotFoundException ex) {
        LOGGER.log(Level.WARNING, "Exception encountered during local file import", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LocalFileLoaderException.class)
    public ResponseEntity<ErrorResponse> handleLocalFileLoaderException(final LocalFileLoaderException ex) {
        LOGGER.log(Level.WARNING, "Unknown exception encountered during the importing from local file", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MultipartFileLoaderException.class)
    public ResponseEntity<ErrorResponse> handleMultipartFileLoaderException(final MultipartFileLoaderException ex) {
        LOGGER.log(Level.WARNING, "Unknown exception encountered during the importing from multipart file", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(QuestionLoaderException.class)
    public ResponseEntity<ErrorResponse> handleMultipartFileLoaderException(final QuestionLoaderException ex) {
        LOGGER.log(Level.WARNING, "Exception encountered during questions deserialization", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ConstructorException.class)
    public ResponseEntity<ErrorResponse> handleMultipartFileLoaderException(final ConstructorException ex) {
        LOGGER.log(Level.WARNING, "The format of the question is not correct", ex);
        final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
