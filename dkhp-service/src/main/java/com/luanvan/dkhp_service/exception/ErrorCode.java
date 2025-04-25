package com.luanvan.dkhp_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UPDATE_FAILED(HttpStatus.BAD_REQUEST,  "Không thể cập nhật số lượng sinh viên còn lại."),
    NHOM_HOC_FULL(HttpStatus.BAD_REQUEST, "Nhóm học đã đầy, không thể đăng ký"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    NHOM_HOC_NOT_FOUND(HttpStatus.NOT_FOUND, "Nhóm học phần không tồn tại"),
    ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "Học phần đã được đăng ký trong nhóm"),
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "Không tìm thấy bản ghi");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}