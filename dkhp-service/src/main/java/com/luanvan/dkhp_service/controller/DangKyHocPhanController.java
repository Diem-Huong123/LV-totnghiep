package com.luanvan.dkhp_service.controller;

import com.luanvan.dkhp_service.dto.request.DangKyHocPhanListRequest;
import com.luanvan.dkhp_service.dto.request.DangKyHocPhanRequest;
import com.luanvan.dkhp_service.dto.response.DangKyHocPhanResponse;
import com.luanvan.dkhp_service.service.DangKyHocPhanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/dkhp")
public class DangKyHocPhanController {
    @Autowired
    private DangKyHocPhanService dangKyHocPhanService;

    //
    @GetMapping
    public ResponseEntity<List<DangKyHocPhanResponse>> getAllDangKyHocPhan() {
        return ResponseEntity.ok(dangKyHocPhanService.getAllDangKyHocPhan());
    }

    //
    @GetMapping("/mssv/{mssv}")
    public ResponseEntity<List<DangKyHocPhanResponse>> getDangKyHocPhanByMssv(@PathVariable String mssv) {
        return ResponseEntity.ok(dangKyHocPhanService.getDangKyHocPhanByMssv(mssv));
    }

    // Kiểm tra học phần đã đăng ký chưa//
    @GetMapping("/check/{mssv}/{maHocKy}/{maHocPhan}")
    public ResponseEntity<String> checkNhomHocDaDangKy(@PathVariable String mssv, @PathVariable String maHocKy, @PathVariable String maHocPhan) {
        String maNhomHoc = dangKyHocPhanService.getNhomHocDaDangKy(mssv, maHocKy, maHocPhan);
        return ResponseEntity.ok(maNhomHoc != null ? maNhomHoc : "Chưa đăng ký");
    }

    //
    @GetMapping("/{mssv}/{maHocKy}")
    public List<DangKyHocPhanResponse> getDangKyHocPhanByMssvAndHocKy(@PathVariable String mssv, @PathVariable String maHocKy) {
        return dangKyHocPhanService.getDangKyHocPhanByMssvAndHocKy(mssv, maHocKy);
    }

    // Đăng ký học phần //
    @PostMapping
    public ResponseEntity<DangKyHocPhanResponse> registerHocPhan(@RequestBody DangKyHocPhanRequest request) {
        return ResponseEntity.ok(dangKyHocPhanService.registerHocPhan(request));
    }

    //
    @PostMapping("/registerList")
    public ResponseEntity<List<DangKyHocPhanResponse>> registerMultipleHocPhan(
            @RequestBody DangKyHocPhanListRequest request) {
        List<DangKyHocPhanResponse> responses = dangKyHocPhanService.registerMultipleHocPhan(request);
        return ResponseEntity.ok(responses);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteDangKyHocPhan(@PathVariable String id) {
        dangKyHocPhanService.deleteDangKyHocPhan(id);
        return ResponseEntity.ok("Đã hủy đăng ký học phần thành công.");
    }

    @DeleteMapping("/delete/{mssv}/{maHocKy}/{maNhomHoc}")
    public ResponseEntity<String> deleteDangKyHocPhan(
            @PathVariable String mssv,
            @PathVariable String maHocKy,
            @PathVariable String maNhomHoc) {
        dangKyHocPhanService.deleteDangKyHocPhan(mssv, maHocKy, maNhomHoc);
        return ResponseEntity.ok("Đã xóa đăng ký học phần thành công!");
    }

    //
    @GetMapping("/thoikhoabieu/{mssv}/{maHocKy}")
    public ResponseEntity<List<DangKyHocPhanResponse>> getThoiKhoaBieu(@PathVariable String mssv , @PathVariable String maHocKy) {
        return ResponseEntity.ok(dangKyHocPhanService.getThoiKhoaBieu(mssv, maHocKy));
    }

    @GetMapping("/total/{maHocKy}")
    public ResponseEntity<Long> getTotalDangKyHocPhanByHocKy(@PathVariable String maHocKy) {
        long total = dangKyHocPhanService.getTotalDangKyHocPhanByHocKy(maHocKy);
        return ResponseEntity.ok(total);
    }
}
