package com.luanvan.khht_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter @Builder
public class HocKiResponse {
    private String id;
    private String mahocki;
    private String tenhocki;
    private String namhoc;
    private LocalDate startDate;
    private LocalDate endDate;
}
