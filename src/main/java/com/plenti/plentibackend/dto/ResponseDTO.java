package com.plenti.plentibackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard response wrapper for all API responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {
    
    private int code;
    private boolean success;
    private String message;
    private T data;
    
    // Constructor for backward compatibility
    public ResponseDTO(boolean success, String message, T data) {
        this.code = success ? 200 : 400;
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public static <T> ResponseDTO<T> success(T data) {
        return new ResponseDTO<>(200, true, "Success", data);
    }
    
    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(200, true, message, data);
    }
    
    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(400, false, message, null);
    }
    
    public static <T> ResponseDTO<T> error(int code, String message) {
        return new ResponseDTO<>(code, false, message, null);
    }
    
    public static <T> ResponseDTO<T> success(int code, String message, T data) {
        return new ResponseDTO<>(code, true, message, data);
    }
}
