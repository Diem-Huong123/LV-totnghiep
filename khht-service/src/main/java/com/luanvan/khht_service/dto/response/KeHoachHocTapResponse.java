package com.luanvan.khht_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeHoachHocTapResponse {
    private String id;
    private String maSinhVien;
    private String maHocKy;
    private List<HocPhanResponse> maHocPhans;
}