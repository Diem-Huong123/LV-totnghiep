package com.luanvan.dkhp_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // Tránh lỗi nếu nhận thêm trường không mong muốn từ MongoDB
public class NhomHocResponse {
    private String maNhomHoc;
    private String maHocPhan;
    private int soLuongSVToiDa;
    private int soLuongSVConLai;
    private String phongHoc;
    private int soTiet;
    private int thu;
    private int tietBatDau;
}
