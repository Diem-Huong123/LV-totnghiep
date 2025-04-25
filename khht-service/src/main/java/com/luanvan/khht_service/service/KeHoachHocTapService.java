package com.luanvan.khht_service.service;

import com.luanvan.khht_service.dto.request.KeHoachHocTapRequest;
import com.luanvan.khht_service.dto.response.HocPhanResponse;
import com.luanvan.khht_service.dto.response.KeHoachHocTapResponse;
import com.luanvan.khht_service.entity.KeHoachHocTap;
import com.luanvan.khht_service.exception.AppException;
import com.luanvan.khht_service.exception.ErrorCode;
import com.luanvan.khht_service.repository.KeHoachHocTapRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeHoachHocTapService {

    private final KeHoachHocTapRepository keHoachHocTapRepository;
    private final CourseServiceClient courseServiceClient;

    private KeHoachHocTapResponse convertToResponse(KeHoachHocTap keHoachHocTap) {
        List<HocPhanResponse> hocPhans = keHoachHocTap.getMaHocPhans().stream()
                .map(courseServiceClient::getHocPhanByMaHocPhan)
                .collect(Collectors.toList());

        return new KeHoachHocTapResponse(
                keHoachHocTap.getId(),
                keHoachHocTap.getMaSinhVien(),
                keHoachHocTap.getMaHocKy(),
                hocPhans
        );
    }

    /**
     * API: Lấy danh sách kế hoạch học tập theo MSSV
     */
    public List<KeHoachHocTapResponse> getKeHoachByMaSinhVien(String maSinhVien) {
        return keHoachHocTapRepository.findByMaSinhVien(maSinhVien)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<KeHoachHocTapResponse> getAllKeHoachHocTap() {
        return keHoachHocTapRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * API: Tạo kế hoạch học tập mới
     */
    public KeHoachHocTapResponse createKeHoachHocTap(KeHoachHocTapRequest request) {
        KeHoachHocTap keHoachHocTap = new KeHoachHocTap();
        keHoachHocTap.setMaSinhVien(request.getMaSinhVien());
        keHoachHocTap.setMaHocKy(request.getMaHocKy());
        keHoachHocTap.setMaHocPhans(List.of()); // Ban đầu chưa có học phần

        keHoachHocTap = keHoachHocTapRepository.save(keHoachHocTap);
        return convertToResponse(keHoachHocTap);
    }

    /**
     * API: Lấy kế hoạch học tập theo MSSV và mã học kỳ
     */
    public KeHoachHocTapResponse getKeHoachHocTapByMaSinhVienAndMaHocKy(String maSinhVien, String maHocKy) {
        KeHoachHocTap keHoachHocTap = keHoachHocTapRepository
                .findByMaSinhVienAndMaHocKy(maSinhVien, maHocKy)
                .orElseThrow(() -> new AppException(ErrorCode.KE_HOACH_NOT_FOUND));

        return convertToResponse(keHoachHocTap);
    }

    public List<KeHoachHocTapResponse> getKeHoachHocTapChiTietByMaSinhVien(String maSinhVien) {
        return keHoachHocTapRepository.findByMaSinhVien(maSinhVien)
                .stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    /**
     * API: Thêm học phần vào kế hoạch học tập
     */
    public KeHoachHocTapResponse addHocPhanToKeHoach(String maSinhVien, String maHocKy, String maHocPhan) {
        KeHoachHocTap keHoachHocTap = keHoachHocTapRepository
                .findByMaSinhVienAndMaHocKy(maSinhVien, maHocKy)
                .orElseThrow(() -> new AppException(ErrorCode.KE_HOACH_NOT_FOUND));

        HocPhanResponse hocPhan = courseServiceClient.getHocPhanByMaHocPhan(maHocPhan);
        if (hocPhan == null) {
            throw new AppException(ErrorCode.HOC_PHAN_NOT_FOUND);
        }

        if (!keHoachHocTap.getMaHocPhans().contains(maHocPhan)) {
            keHoachHocTap.getMaHocPhans().add(maHocPhan);
            keHoachHocTap = keHoachHocTapRepository.save(keHoachHocTap);
        }
        System.out.println("HocPhanResponse: " + hocPhan);
        return convertToResponse(keHoachHocTap);
    }

    /**
     * API: Xóa học phần khỏi kế hoạch học tập
     */
    public KeHoachHocTapResponse removeHocPhanFromKeHoach(String maSinhVien, String maHocKy, String maHocPhan) {
        KeHoachHocTap keHoachHocTap = keHoachHocTapRepository
                .findByMaSinhVienAndMaHocKy(maSinhVien, maHocKy)
                .orElseThrow(() -> new AppException(ErrorCode.KE_HOACH_NOT_FOUND));

        if (!keHoachHocTap.getMaHocPhans().contains(maHocPhan)) {
            throw new AppException(ErrorCode.HOC_PHAN_NOT_FOUND);
        }

        keHoachHocTap.getMaHocPhans().remove(maHocPhan);
        keHoachHocTap = keHoachHocTapRepository.save(keHoachHocTap);
        return convertToResponse(keHoachHocTap);
    }

    /**
     * API: Lấy danh sách mã học phần theo mã học kỳ
     */
    public List<String> getMaHocPhanByHocKy(String maHocKy) {
        return keHoachHocTapRepository.findByMaHocKy(maHocKy)
                .stream()
                .flatMap(keHoach -> keHoach.getMaHocPhans().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public long getTotalKeHoachHocTapByHocKy(String maHocKy) {
        return keHoachHocTapRepository.countByMaHocKy(maHocKy);
    }

//    public void uploadKeHoachHocTap(MultipartFile file) {
//        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
//            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
//
//            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ qua dòng tiêu đề
//                Row row = sheet.getRow(i);
//                if (row == null) continue;
//
//                String maSinhVien = row.getCell(0).getStringCellValue();
//                String maHocKy = row.getCell(1).getStringCellValue();
//                String maHocPhan = row.getCell(2).getStringCellValue();
//
//                // Tìm hoặc tạo KHHT
//                KeHoachHocTap keHoach = keHoachHocTapRepository
//                        .findByMaSinhVienAndMaHocKy(maSinhVien, maHocKy)
//                        .orElseGet(() -> {
//                            KeHoachHocTap newKhht = new KeHoachHocTap();
//                            newKhht.setMaSinhVien(maSinhVien);
//                            newKhht.setMaHocKy(maHocKy);
//                            newKhht.setMaHocPhans(new ArrayList<>());
//                            return newKhht;
//                        });
//
//                if (!keHoach.getMaHocPhans().contains(maHocPhan)) {
//                    keHoach.getMaHocPhans().add(maHocPhan);
//                }
//
//                keHoachHocTapRepository.save(keHoach);
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException("Lỗi khi xử lý file Excel: " + e.getMessage(), e);
//        }
//    }

    public void deleteKeHoachHocTap(String maSinhVien, String maHocKy) {
        KeHoachHocTap keHoach = keHoachHocTapRepository
                .findByMaSinhVienAndMaHocKy(maSinhVien, maHocKy)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kế hoạch học tập."));

        keHoachHocTapRepository.delete(keHoach);
    }

    public void uploadKeHoachHocTap(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Bỏ qua dòng tiêu đề
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null || row.getCell(1) == null) continue;

                String maSinhVien = row.getCell(0).getStringCellValue().trim();
                String maHocKy = row.getCell(1).getStringCellValue().trim();

                // Kiểm tra KHHT đã tồn tại chưa
                boolean exists = keHoachHocTapRepository
                        .findByMaSinhVienAndMaHocKy(maSinhVien, maHocKy)
                        .isPresent();

                if (!exists) {
                    KeHoachHocTap newKhht = new KeHoachHocTap();
                    newKhht.setMaSinhVien(maSinhVien);
                    newKhht.setMaHocKy(maHocKy);
                    newKhht.setMaHocPhans(new ArrayList<>()); // KHHT trống
                    keHoachHocTapRepository.save(newKhht);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xử lý file Excel: " + e.getMessage(), e);
        }
    }


}
