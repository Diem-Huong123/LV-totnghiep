package com.luanvan.nhomhocphan_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NhomHocResponse {
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
}