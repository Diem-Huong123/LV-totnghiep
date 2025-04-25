package com.luanvan.khht_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "hocki")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class HocKi {
    @Id
    private String id;
    private String mahocki;    // HK1-2024-2025
    private String tenhocki;   // Học kỳ 1
    private String namhoc;     // 2024-2025
    private LocalDate startDate;
    private LocalDate endDate;
}
