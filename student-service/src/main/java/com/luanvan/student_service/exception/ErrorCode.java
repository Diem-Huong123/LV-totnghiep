package com.luanvan.student_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Sinh viên không tồn tại"),
    STUDENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "Sinh viên đã tồn tại"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi máy chủ nội bộ");

    private final HttpStatus status;
    private final String message;
}
