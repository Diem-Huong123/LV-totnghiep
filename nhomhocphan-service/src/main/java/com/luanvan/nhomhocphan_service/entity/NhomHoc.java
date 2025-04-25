package com.luanvan.nhomhocphan_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

@Document(collection = "nhomhoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class NhomHoc {
    @Id
    private String id;
    private String maNhomHoc;
    private String maHocPhan;
    private String maHocKi;
    private String namHoc;
    private String tenHocKi;
    private String phongHoc;
    private int soLuongSVToiDa;
    private int soLuongSVConLai;
    private int soTiet;
    private int thu;
    private int tietBatDau;

    private boolean isLocked = false;
}
