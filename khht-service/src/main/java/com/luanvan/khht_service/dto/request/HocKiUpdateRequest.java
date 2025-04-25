package com.luanvan.khht_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class HocKiUpdateRequest {
    private String tenhocki;
    private String namhoc;
    private LocalDate startDate;
    private LocalDate endDate;
}
