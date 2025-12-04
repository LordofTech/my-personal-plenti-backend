package com.plenti.plentibackend.exception;

/**
 * Custom exception for Plenti application
 */
public class PlentiException extends RuntimeException {
    
    public PlentiException(String message) {
        super(message);
    }
    
    public PlentiException(String message, Throwable cause) {
        super(message, cause);
    }
}
