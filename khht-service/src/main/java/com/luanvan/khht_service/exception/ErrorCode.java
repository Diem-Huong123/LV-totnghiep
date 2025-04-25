package com.luanvan.khht_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Lỗi liên quan đến Học Kỳ
    HOCKI_NOT_FOUND(HttpStatus.NOT_FOUND, "Học kỳ không tồn tại"),
    HOCKI_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Học kỳ đã tồn tại"),

    // Lỗi liên quan đến Kế Hoạch Học Tập
    KE_HOACH_NOT_FOUND(HttpStatus.NOT_FOUND, "Kế hoạch học tập không tồn tại"),
    KE_HOACH_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Kế hoạch học tập đã tồn tại"),

    // Lỗi liên quan đến Học Phần
    HOC_PHAN_NOT_FOUND(HttpStatus.NOT_FOUND, "Học phần không tồn tại"),
    HOC_PHAN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "Học phần đã tồn tại"),

    // Lỗi chung
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "Dữ liệu không hợp lệ"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống");



    private final HttpStatus status;
    private final String message;
}
