package com.simulator.exam.exception;

public class ModuleNotFoundException extends RuntimeException {

    private static final String ERROR_MESSAGE = "No question was found for the module %s";

    public ModuleNotFoundException(final String module) {
        super(String.format(ERROR_MESSAGE, module));
    }
}
