package com.luanvan.dkhp_service.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class DangKyHocPhanListRequest {
    private String mssv; // Mã số sinh viên
    private String maHocKy; // Mã học kỳ
    private List<String> danhSachMaNhomHoc; // Danh sách mã nhóm học cần đăng ký
}
