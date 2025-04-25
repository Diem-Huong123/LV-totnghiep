package com.luanvan.dkhp_service.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "dangkihocphan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DangKyHocPhan {

    @Id
    private String id;

    private String mssv;
    private String maNhomHoc;
    private String maHocKy;
}
