package com.luanvan.dkhp_service.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DangKyHocPhanRequest {
    private String mssv;
    private String maNhomHoc;
    private String maHocKy;
    private String maHocPhan;
}
