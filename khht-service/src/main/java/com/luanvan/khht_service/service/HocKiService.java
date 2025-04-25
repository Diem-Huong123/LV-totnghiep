package com.luanvan.khht_service.service;

import com.luanvan.khht_service.dto.request.HocKiRequest;
import com.luanvan.khht_service.dto.request.HocKiUpdateRequest;
import com.luanvan.khht_service.dto.response.HocKiResponse;
import com.luanvan.khht_service.entity.HocKi;
import com.luanvan.khht_service.exception.AppException;
import com.luanvan.khht_service.exception.ErrorCode;
import com.luanvan.khht_service.repository.HocKiRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HocKiService {
    private final HocKiRepository hocKiRepository;

    public List<HocKiResponse> getAllHocKi() {
        return hocKiRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public HocKiResponse getHocKiByMahocki(String mahocki) {
        HocKi hocKi = hocKiRepository.findByMahocki(mahocki)
                .orElseThrow(() -> new AppException(ErrorCode.HOCKI_NOT_FOUND));
        return mapToResponse(hocKi);
    }

    public HocKiResponse createHocKi(HocKiRequest request) {
        if (hocKiRepository.findByMahocki(request.getMahocki()).isPresent()) {
            throw new AppException(ErrorCode.HOCKI_ALREADY_EXISTS);
        }

        HocKi hocKi = HocKi.builder()
                .mahocki(request.getMahocki())
                .tenhocki(request.getTenhocki())
                .namhoc(request.getNamhoc())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
        return mapToResponse(hocKiRepository.save(hocKi));
    }

    public HocKiResponse updateHocKi(String mahocki, HocKiUpdateRequest request) {
        HocKi hocKi = hocKiRepository.findByMahocki(mahocki)
                .orElseThrow(() -> new AppException(ErrorCode.HOCKI_NOT_FOUND));

        hocKi.setTenhocki(request.getTenhocki());
        hocKi.setNamhoc(request.getNamhoc());
        hocKi.setStartDate(request.getStartDate());
        hocKi.setEndDate(request.getEndDate());

        return mapToResponse(hocKiRepository.save(hocKi));
    }

    public void deleteHocKi(String mahocki) {
        HocKi hocKi = hocKiRepository.findByMahocki(mahocki)
                .orElseThrow(() -> new AppException(ErrorCode.HOCKI_NOT_FOUND));
        hocKiRepository.delete(hocKi);
    }

    private HocKiResponse mapToResponse(HocKi hocKi) {
        return HocKiResponse.builder()
                .id(hocKi.getId())
                .mahocki(hocKi.getMahocki())
                .tenhocki(hocKi.getTenhocki())
                .namhoc(hocKi.getNamhoc())
                .startDate(hocKi.getStartDate())
                .endDate(hocKi.getEndDate())
                .build();
    }

    public List<HocKiResponse> getHocKiByNamHocAndTenHocKi(String namHoc, String tenHocKi) {
        if (namHoc != null && tenHocKi != null) {
            return hocKiRepository.findByNamhocAndTenhocki(namHoc, tenHocKi)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (namHoc != null) {
            return hocKiRepository.findByNamhoc(namHoc)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (tenHocKi != null) {
            return hocKiRepository.findByTenhocki(tenHocKi)
                    .stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public void importHocKiFromExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // bỏ dòng tiêu đề

                String mahocki = row.getCell(0).getStringCellValue();
                String tenhocki = row.getCell(1).getStringCellValue();
                String namhoc = row.getCell(2).getStringCellValue();
                LocalDate startDate = row.getCell(3).getLocalDateTimeCellValue().toLocalDate();
                LocalDate endDate = row.getCell(4).getLocalDateTimeCellValue().toLocalDate();

                if (!hocKiRepository.findByMahocki(mahocki).isPresent()) {
                    HocKi hocKi = HocKi.builder()
                            .mahocki(mahocki)
                            .tenhocki(tenhocki)
                            .namhoc(namhoc)
                            .startDate(startDate)
                            .endDate(endDate)
                            .build();
                    hocKiRepository.save(hocKi);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }

    }
}
