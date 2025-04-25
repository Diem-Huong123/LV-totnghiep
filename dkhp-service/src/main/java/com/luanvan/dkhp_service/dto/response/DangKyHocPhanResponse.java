package com.luanvan.dkhp_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // Tránh lỗi nếu nhận dữ liệu từ MongoDB có thêm trường lạ
public class DangKyHocPhanResponse {
    private String mssv;
    private String maNhomHoc;
    private String tenHocPhan;
    private String phongHoc;
    private int soTiet;
    private int thu;
    private int tietBatDau;
    private String maHocKy;
    private String maHocPhan;
}
