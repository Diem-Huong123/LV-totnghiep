package com.luanvan.nhomhocphan_service.controller;

import com.luanvan.nhomhocphan_service.dto.request.NhomHocRequest;
import com.luanvan.nhomhocphan_service.dto.request.UpdateSoLuongSVConLaiRequest;
import com.luanvan.nhomhocphan_service.dto.response.NhomHocResponse;
import com.luanvan.nhomhocphan_service.entity.NhomHoc;
import com.luanvan.nhomhocphan_service.service.NhomHocService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/nhomhoc")
public class NhomHocController {

    @Autowired
    private NhomHocService nhomHocService;

    //
    @GetMapping
    public ResponseEntity<List<NhomHocResponse>> getAllNhomHoc() {
        return ResponseEntity.ok(nhomHocService.getAllNhomHoc());
    }

    //
    @PostMapping
    public ResponseEntity<NhomHocResponse> createNhomHoc(@RequestBody NhomHocRequest request) {
        return ResponseEntity.ok(nhomHocService.createNhomHoc(request));
    }//ok

    @PutMapping("/update/{maNhomHoc}")
    public NhomHocResponse updateNhomHoc(@PathVariable String maNhomHoc, @RequestBody NhomHocRequest request) {
        return nhomHocService.updateNhomHocByMaNhomHoc(maNhomHoc, request);
    }

    @DeleteMapping("/manhomhoc/{maNhomHoc}")
    public String deleteNhomHocByMaNhomHoc(@PathVariable String maNhomHoc) {
        nhomHocService.deleteNhomHocByMaNhomHoc(maNhomHoc);
        return "Xóa nhóm học thành công!";
    }


    //
    @GetMapping("/mahocphan/{maHocPhan}")
    public List<NhomHoc> getNhomHocByMaHocPhan(@PathVariable String maHocPhan) {
        return nhomHocService.getNhomHocByMaHocPhan(maHocPhan);
    }

    //
    @GetMapping("/{maNhomHoc}")
    public ResponseEntity<NhomHocResponse> getNhomHocByMaNhomHoc(@PathVariable String maNhomHoc) {
        return ResponseEntity.ok(nhomHocService.getNhomHocByMaNhomHoc(maNhomHoc));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> importNhomHoc(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File không được để trống!");
        }
        nhomHocService.importNhomHocFromExcel(file);
        return ResponseEntity.ok("Import danh sách nhóm học phần thành công!");
    }

    @PutMapping("/slsvconlai/{maNhomHoc}")
    public ResponseEntity<NhomHocResponse> updateSoLuongSVConLai(
            @PathVariable String maNhomHoc,
            @Valid @RequestBody UpdateSoLuongSVConLaiRequest request) {

        NhomHocResponse response = nhomHocService.updateSoLuongSVConLai(maNhomHoc, request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/mahocki/{maHocKi}")
    public ResponseEntity<List<NhomHocResponse>> getNhomHocByMaHocKi(@PathVariable String maHocKi) {
        return ResponseEntity.ok(nhomHocService.getNhomHocByMaHocKi(maHocKi));
    }

    @GetMapping("/namhoc/{namHoc}")
    public ResponseEntity<List<NhomHocResponse>> getNhomHocByNamHoc(
            @PathVariable String namHoc) {
        return ResponseEntity.ok(nhomHocService.getNhomHocByNamHoc(namHoc));
    }

    @GetMapping("/nhomhoc/{maHocKi}/{maHocPhan}")
    public ResponseEntity<List<NhomHocResponse>> getNhomHocByHocKiAndHocPhan(
            @PathVariable String maHocKi,
            @PathVariable String maHocPhan) {

        List<NhomHocResponse> nhomHocList = nhomHocService.getNhomHocByHocKiAndHocPhan(maHocKi, maHocPhan);
        return ResponseEntity.ok(nhomHocList);
    }

    @GetMapping("/total/{maHocKi}")
    public long getTotalNhomHocInHocKi(@PathVariable String maHocKi) {
        return nhomHocService.getTotalNhomHocInHocKi(maHocKi);
    }

    @PostMapping("/end-hocki/{maHocKi}")
    public ResponseEntity<String> endHocKi(@PathVariable String maHocKi) {
        nhomHocService.checkAndLockNhomHocForHocKi(maHocKi);
        return ResponseEntity.ok("Đã kiểm tra và cập nhật trạng thái nhóm học cho học kỳ " + maHocKi);
    }

    @GetMapping("/thongke/{maHocKi}")
    public ResponseEntity<List<Map<String, Object>>> thongKeNhomHoc(@PathVariable String maHocKi) {
        List<Map<String, Object>> nhomHocThongKe = nhomHocService.thongKeNhomHoc(maHocKi);
        return ResponseEntity.ok(nhomHocThongKe);
    }
    @GetMapping("/thongke/{maHocKi}/{maHocPhan}")
    public ResponseEntity<List<Map<String, Object>>> thongKeNhomHoc(@PathVariable String maHocKi, @PathVariable String maHocPhan) {
        // Gọi service để lấy thống kê theo cả mã học kỳ và mã học phần
        List<Map<String, Object>> nhomHocThongKe = nhomHocService.thongKeNhomHocMahocKi(maHocKi, maHocPhan);

        // Nếu không có dữ liệu, trả về mã 204
        if (nhomHocThongKe.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Trả về danh sách thống kê
        return ResponseEntity.ok(nhomHocThongKe);
    }
    @GetMapping("/danhsachmahocphan")
    public ResponseEntity<List<String>> getMaHocPhanTheoHocKi(@RequestParam String maHocKi) {
        List<String> dsMaHocPhan = nhomHocService.getMaHocPhanByMaHocKi(maHocKi);
        return ResponseEntity.ok(dsMaHocPhan);
    }

}
