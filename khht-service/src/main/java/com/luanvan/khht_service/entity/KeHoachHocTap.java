package com.luanvan.khht_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "kehoachhoctap")
public class KeHoachHocTap {
    @Id
    private String id;
    private String maSinhVien;
    private String maHocKy;
    private List<String> maHocPhans;
}
