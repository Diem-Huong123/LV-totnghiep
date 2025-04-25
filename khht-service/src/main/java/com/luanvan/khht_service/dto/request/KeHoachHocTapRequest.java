package com.luanvan.khht_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KeHoachHocTapRequest {
    private String maSinhVien;
    private String maHocKy;
    private List<String> maHocPhans;

}