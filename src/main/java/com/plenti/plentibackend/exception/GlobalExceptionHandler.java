package com.plenti.plentibackend.exception;

import com.plenti.plentibackend.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for centralized error handling
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlentiException.class)
    public ResponseEntity<ResponseDTO<Object>> handlePlentiException(PlentiException ex) {
        return ResponseEntity
                .badRequest()
                .body(ResponseDTO.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseDTO<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ResponseDTO<Map<String, String>> response = new ResponseDTO<>();
        response.setSuccess(false);
        response.setMessage("Validation failed");
        response.setData(errors);
        
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDTO<Object>> handleGeneralException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseDTO.error("An error occurred: " + ex.getMessage()));
    }
}
