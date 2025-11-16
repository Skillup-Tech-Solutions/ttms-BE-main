package com.example.TTMS.config;

import java.util.Map;

public class FieldException extends RuntimeException {

    private final String message;
    private final Map<String, String> errors;

    public FieldException(String message, Map<String, String> errors) {
        super(message);
        this.message = message;
        this.errors = errors;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public Map<String, String> getErrors() {
        return this.errors;
    }

}
