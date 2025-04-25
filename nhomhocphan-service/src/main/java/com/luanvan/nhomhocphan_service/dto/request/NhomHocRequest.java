package com.luanvan.nhomhocphan_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NhomHocRequest {
    private String maNhomHoc;
    private String maHocPhan;
    private String maHocKi;
    private String phongHoc;
    private int soLuongSVToiDa;
    private int soLuongSVConLai;
    private int soTiet;
    private int thu;
    private int tietBatDau;
}