package com.luanvan.nhomhocphan_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    HOC_PHAN_NOT_FOUND(400, HttpStatus.BAD_REQUEST,"Học phần không tồn tại"),
    HOCKI_NOT_FOUND(400, HttpStatus.BAD_REQUEST, "Học kì không tồn tại"),
    INVALID_STUDENT_COUNT(400, HttpStatus.BAD_REQUEST, "Số lượng sinh viên còn lại không hợp lệ"),
    FILE_PROCESSING_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi xử lý file"),
    NHOM_HOC_NOT_FOUND(404, HttpStatus.NOT_FOUND, "Nhóm học không tồn tại"),
    INVALID_REQUEST(400, HttpStatus.BAD_REQUEST, "Yêu cầu không hợp lệ"),
    NHOM_HOC_FULL(400, HttpStatus.BAD_REQUEST, "Nhóm học đã đầy, không thể đăng ký");

    private final int code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}