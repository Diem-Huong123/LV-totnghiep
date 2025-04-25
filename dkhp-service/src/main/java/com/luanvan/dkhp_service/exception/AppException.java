package com.luanvan.dkhp_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final HttpStatus status;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
    }

    public AppException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.status = errorCode.getStatus();
    }
}
