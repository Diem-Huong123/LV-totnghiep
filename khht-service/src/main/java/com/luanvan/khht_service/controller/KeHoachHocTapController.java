package com.luanvan.khht_service.controller;

import com.luanvan.khht_service.dto.request.KeHoachHocTapRequest;
import com.luanvan.khht_service.dto.response.KeHoachHocTapResponse;
import com.luanvan.khht_service.service.KeHoachHocTapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/kehoachhoctap")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KeHoachHocTapController {

    private final KeHoachHocTapService keHoachHocTapService;

    //
    // Tạo kế hoạch học tập//
    @PostMapping
    public ResponseEntity<KeHoachHocTapResponse> createKeHoachHocTap(@RequestBody KeHoachHocTapRequest request) {
        return ResponseEntity.ok(keHoachHocTapService.createKeHoachHocTap(request));
    }

    // Lấy kế hoạch học tập theo mã sinh viên//
    @GetMapping("/{maSinhVien}")
    public ResponseEntity<List<KeHoachHocTapResponse>> getKeHoachByMaSinhVien(@PathVariable String maSinhVien) {
        return ResponseEntity.ok(keHoachHocTapService.getKeHoachByMaSinhVien(maSinhVien));
    }

    //
    @GetMapping
    public ResponseEntity<List<KeHoachHocTapResponse>> getAllKeHoachHocTap() {
        return ResponseEntity.ok(keHoachHocTapService.getAllKeHoachHocTap());
    }

    // Lấy kế hoạch học tập theo sinh viên và học kỳ//
    @GetMapping("/{maSinhVien}/{maHocKy}")
    public ResponseEntity<KeHoachHocTapResponse> getKeHoachHocTapByMaSinhVienAndMaHocKy(
            @PathVariable String maSinhVien,
            @PathVariable String maHocKy) {
        return ResponseEntity.ok(keHoachHocTapService.getKeHoachHocTapByMaSinhVienAndMaHocKy(maSinhVien, maHocKy));
    }

    // Lấy danh sách mã học phần theo mã học kỳ//
    @GetMapping("/hocphan/{maHocKy}")
    public ResponseEntity<List<String>> getMaHocPhanByHocKy(@PathVariable String maHocKy) {
        return ResponseEntity.ok(keHoachHocTapService.getMaHocPhanByHocKy(maHocKy));
    }

    // Lấy kế hoạch học tập chi tiết theo mã sinh viên//
    @GetMapping("/chi-tiet/{maSinhVien}")
    public ResponseEntity<List<KeHoachHocTapResponse>> getKeHoachChiTiet(@PathVariable String maSinhVien) {
        return ResponseEntity.ok(keHoachHocTapService.getKeHoachHocTapChiTietByMaSinhVien(maSinhVien));
    }


    // Thêm học phần vào kế hoạch theo mã sinh viên và học kỳ
    @PutMapping("/{maSinhVien}/{maHocKy}/{maHocPhan}")
    public ResponseEntity<KeHoachHocTapResponse> addHocPhanToKeHoachBySinhVien(
            @PathVariable String maSinhVien,
            @PathVariable String maHocKy,
            @PathVariable String maHocPhan) {
        return ResponseEntity.ok(keHoachHocTapService.addHocPhanToKeHoach(maSinhVien, maHocKy, maHocPhan));
    }

    // Xóa học phần khỏi kế hoạch theo sinh viên và học kỳ
    @DeleteMapping("/{maSinhVien}/{maHocKy}/{maHocPhan}")
    public ResponseEntity<Void> removeHocPhanFromKeHoach(
            @PathVariable String maSinhVien,
            @PathVariable String maHocKy,
            @PathVariable String maHocPhan) {
        keHoachHocTapService.removeHocPhanFromKeHoach(maSinhVien, maHocKy, maHocPhan);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/total/{maHocKy}")
    public ResponseEntity<Long> getTotalKeHoachHocTapByHocKy(@PathVariable String maHocKy) {
        long total = keHoachHocTapService.getTotalKeHoachHocTapByHocKy(maHocKy);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadKeHoachHocTap(@RequestParam("file") MultipartFile file) {
        try {
            keHoachHocTapService.uploadKeHoachHocTap(file);
            return ResponseEntity.ok("Tải lên thành công!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi xử lý file: " + e.getMessage());
        }
    }

    // Xóa kế hoạch học tập theo mã sinh viên và học kỳ
    @DeleteMapping("/delete/{maSinhVien}/{maHocKy}")
    public ResponseEntity<Void> deleteKeHoachHocTap(
            @PathVariable String maSinhVien,
            @PathVariable String maHocKy) {
        keHoachHocTapService.deleteKeHoachHocTap(maSinhVien, maHocKy);
        return ResponseEntity.ok().build();
    }


}