package com.luanvan.khht_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<Object> handleAppException(AppException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", ex.getErrorCode().getStatus().value());
        response.put("error", ex.getErrorCode().name());
        response.put("message", ex.getErrorCode().getMessage());
        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value());
        response.put("error", "INTERNAL_SERVER_ERROR");
        response.put("message", "Lỗi hệ thống: " + ex.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }
}
