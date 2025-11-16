package com.example.TTMS.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.example.TTMS.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ErrorHandler {

    private static final String INDEX_PREFIX = "index: ";

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> handleException(ResponseStatusException exc) {
        ApiResponse<Object> error = ApiResponse.error("OPERATIONAL", exc.getReason());
        log.error("Operational Exception occured - {}", error.getTimestamp(), exc);

        return new ResponseEntity<>(error, exc.getStatusCode());
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> handleException(Exception exc) {
        ApiResponse<Object> error = ApiResponse.error("FATAL", exc.getLocalizedMessage());
        log.error("Fatal Exception occured - {}", error.getTimestamp(), exc);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException exc) {
        Map<String, String> errors = new HashMap<>();
        exc.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiResponse<Object> error = ApiResponse.fielderror("OPERATIONAL",
                "Validation Errors", errors);
        log.error("Validation error - {}", error.getTimestamp(), exc);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleException(DuplicateKeyException exc) {
        String message = exc.getMessage();
        // String collectionName = extractCollectionName(message);
        String fieldName = extractFieldName(message);

        Map<String, String> errors = new HashMap<>();
        String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        errors.put(fieldName, capitalizedFieldName + " " + "already exists");

        ApiResponse<Object> error = new ApiResponse<>(false);
        error.setMessage(fieldName + " already exists");
        error.setErrors(errors);
        error.setErrorType("OPERATIONAL");
        log.error("Operational Exception occurred - {}", error.getTimestamp(), exc);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ApiResponse<Object>> handleFieldErrorExceptions(FieldException exc) {
        Map<String, String> errors = new HashMap<>();

        exc.getErrors().forEach((k, v) -> errors.put(k, v));

        ApiResponse<Object> error = ApiResponse.fielderror("OPERATIONAL", exc.getLocalizedMessage(), errors);
        log.error("Validation error - {}", error.getTimestamp(), exc);

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // private String extractCollectionName(String message) {
    //     return extractValue(message, COLLECTION_PREFIX);
    // }

    private String extractFieldName(String message) {
        String indexName = extractValue(message, INDEX_PREFIX);
        int underscoreIndex = indexName.lastIndexOf("_");

        return underscoreIndex != -1 ? indexName.substring(0, underscoreIndex) : indexName;
    }

    private String extractValue(String message, String prefix) {
        int startIndex = message.indexOf(prefix) + prefix.length();
        int endIndex = message.indexOf(" ", startIndex);

        return endIndex != -1 ? message.substring(startIndex, endIndex).trim() : message.substring(startIndex).trim();
    }
}
