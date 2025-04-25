package com.luanvan.nhomhocphan_service.service;

import com.luanvan.nhomhocphan_service.dto.request.NhomHocRequest;
import com.luanvan.nhomhocphan_service.dto.request.UpdateSoLuongSVConLaiRequest;
import com.luanvan.nhomhocphan_service.dto.response.HocKiResponse;
import com.luanvan.nhomhocphan_service.dto.response.NhomHocResponse;
import com.luanvan.nhomhocphan_service.entity.NhomHoc;
import com.luanvan.nhomhocphan_service.exception.AppException;
import com.luanvan.nhomhocphan_service.exception.ErrorCode;
import com.luanvan.nhomhocphan_service.repository.NhomHocRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NhomHocService {

    @Autowired
    private NhomHocRepository nhomHocRepository;


    public List<NhomHocResponse> getAllNhomHoc() {
        return nhomHocRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public NhomHocResponse getNhomHocByMaNhomHoc(String maNhomHoc) {
        NhomHoc nhomHoc = nhomHocRepository.findByMaNhomHoc(maNhomHoc)
                .orElseThrow(() -> new AppException(ErrorCode.NHOM_HOC_NOT_FOUND));
        return convertToResponse(nhomHoc);
    }

//    @Transactional
//    public NhomHocResponse createNhomHoc(NhomHocRequest request) {
//        if (request.getMaNhomHoc() == null || request.getMaHocPhan() == null) {
//            throw new AppException(ErrorCode.INVALID_REQUEST);
//        }
//
//        NhomHoc nhomHoc = new NhomHoc();
//        nhomHoc.setMaNhomHoc(request.getMaNhomHoc());
//        nhomHoc.setMaHocPhan(request.getMaHocPhan());
//        nhomHoc.setMaHocKi(request.getMaHocKi());
//        nhomHoc.setPhongHoc(request.getPhongHoc());
//        nhomHoc.setSoLuongSVToiDa(request.getSoLuongSVToiDa());
//        nhomHoc.setSoLuongSVConLai(request.getSoLuongSVConLai());
//        nhomHoc.setSoTiet(request.getSoTiet());
//        nhomHoc.setThu(request.getThu());
//        nhomHoc.setTietBatDau(request.getTietBatDau());
//
//        nhomHoc = nhomHocRepository.save(nhomHoc);
//        return convertToResponse(nhomHoc);
//    }

//    @Transactional
//    public NhomHocResponse createNhomHoc(NhomHocRequest request) {
//        if (request.getMaNhomHoc() == null || request.getMaHocPhan() == null || request.getMaHocKi() == null) {
//            throw new AppException(ErrorCode.INVALID_REQUEST);
//        }
//
//        // 📌 Kiểm tra xem học kỳ có tồn tại không
//        getHocKiByMaHocKi(request.getMaHocKi());
//
//        // 📌 Kiểm tra xem học phần có tồn tại không
//        if (!checkHocPhanExists(request.getMaHocPhan())) {
//            throw new AppException(ErrorCode.HOC_PHAN_NOT_FOUND);
//        }
//
//        NhomHoc nhomHoc = new NhomHoc();
//        nhomHoc.setMaNhomHoc(request.getMaNhomHoc());
//        nhomHoc.setMaHocPhan(request.getMaHocPhan());
//        nhomHoc.setMaHocKi(request.getMaHocKi());
//        nhomHoc.setPhongHoc(request.getPhongHoc());
//        nhomHoc.setSoLuongSVToiDa(request.getSoLuongSVToiDa());
//        nhomHoc.setSoLuongSVConLai(request.getSoLuongSVConLai());
//        nhomHoc.setSoTiet(request.getSoTiet());
//        nhomHoc.setThu(request.getThu());
//        nhomHoc.setTietBatDau(request.getTietBatDau());
//
//        nhomHoc = nhomHocRepository.save(nhomHoc);
//        return convertToResponse(nhomHoc);
//    }
    @Transactional
    public NhomHocResponse createNhomHoc(NhomHocRequest request) {
        if (request.getMaHocPhan() == null || request.getMaHocKi() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Kiểm tra xem học kỳ có tồn tại
        getHocKiByMaHocKi(request.getMaHocKi());

        //Kiểm tra xem học phần có tồn tại
        if (!checkHocPhanExists(request.getMaHocPhan())) {
            throw new AppException(ErrorCode.HOC_PHAN_NOT_FOUND);
        }

        // Tìm tất cả nhóm học hiện có của học phần này
        List<NhomHoc> existingNhomHoc = nhomHocRepository.findByMaHocPhan(request.getMaHocPhan());

        // Xác định số thứ tự nhóm học cao nhất
        int maxIndex = existingNhomHoc.stream()
                .mapToInt(nhom -> {
                    String suffix = nhom.getMaNhomHoc().replace(request.getMaHocPhan(), ""); // Lấy phần số cuối
                    try {
                        return Integer.parseInt(suffix);
                    } catch (NumberFormatException e) {
                        return 0; // Nếu không hợp lệ, bỏ qua
                    }
                })
                .max()
                .orElse(0);

        // Tạo mã nhóm học mới với số thứ tự tăng dần
        String newMaNhomHoc = request.getMaHocPhan() + String.format("%02d", maxIndex + 1);

        // Lưu nhóm học mới
        NhomHoc nhomHoc = new NhomHoc();
        nhomHoc.setMaNhomHoc(newMaNhomHoc);
        nhomHoc.setMaHocPhan(request.getMaHocPhan());
        nhomHoc.setMaHocKi(request.getMaHocKi());
        nhomHoc.setPhongHoc(request.getPhongHoc());
        nhomHoc.setSoLuongSVToiDa(request.getSoLuongSVToiDa());
        nhomHoc.setSoLuongSVConLai(request.getSoLuongSVToiDa());
        nhomHoc.setSoTiet(request.getSoTiet());
        nhomHoc.setThu(request.getThu());
        nhomHoc.setTietBatDau(request.getTietBatDau());

        nhomHoc = nhomHocRepository.save(nhomHoc);
        return convertToResponse(nhomHoc);
    }


    @Transactional
    public NhomHocResponse updateNhomHocByMaNhomHoc(String maNhomHoc, NhomHocRequest request) {
        NhomHoc nhomHoc = nhomHocRepository.findByMaNhomHoc(maNhomHoc)
                .orElseThrow(() -> new AppException(ErrorCode.NHOM_HOC_NOT_FOUND));

        nhomHoc.setPhongHoc(request.getPhongHoc());
        nhomHoc.setSoLuongSVToiDa(request.getSoLuongSVToiDa());
        nhomHoc.setSoLuongSVConLai(request.getSoLuongSVConLai());
        nhomHoc.setSoTiet(request.getSoTiet());
        nhomHoc.setThu(request.getThu());
        nhomHoc.setTietBatDau(request.getTietBatDau());

        nhomHoc = nhomHocRepository.save(nhomHoc);
        return convertToResponse(nhomHoc);
    }

//    private NhomHocResponse convertToResponse(NhomHoc nhomHoc) {
//        return new NhomHocResponse(
//                nhomHoc.getId(),
//                nhomHoc.getMaNhomHoc(),
//                nhomHoc.getMaHocPhan(),
//                nhomHoc.getMaHocKi(),
//                nhomHoc.getPhongHoc(),
//                nhomHoc.getSoLuongSVToiDa(),
//                nhomHoc.getSoLuongSVConLai(),
//                nhomHoc.getSoTiet(),
//                nhomHoc.getThu(),
//                nhomHoc.getTietBatDau()
//        );
//    }

    private NhomHocResponse convertToResponse(NhomHoc nhomHoc) {
        HocKiResponse hocKiResponse = getHocKiByMaHocKi(nhomHoc.getMaHocKi());

        return new NhomHocResponse(
                nhomHoc.getId(),
                nhomHoc.getMaNhomHoc(),
                nhomHoc.getMaHocPhan(),
                nhomHoc.getMaHocKi(),
                hocKiResponse.getNamhoc(),
                hocKiResponse.getTenhocki(),
                nhomHoc.getPhongHoc(),
                nhomHoc.getSoLuongSVToiDa(),
                nhomHoc.getSoLuongSVConLai(),
                nhomHoc.getSoTiet(),
                nhomHoc.getThu(),
                nhomHoc.getTietBatDau()
        );
    }

    @Transactional
    public void deleteNhomHocByMaNhomHoc(String maNhomHoc) {
        if (!nhomHocRepository.existsByMaNhomHoc(maNhomHoc)) {
            throw new AppException(ErrorCode.NHOM_HOC_NOT_FOUND);
        }
        nhomHocRepository.deleteByMaNhomHoc(maNhomHoc);
    }

    public NhomHocService(NhomHocRepository nhomHocRepository) {
        this.nhomHocRepository = nhomHocRepository;
    }

    public List<NhomHoc> getNhomHocByMaHocPhan(String maHocPhan) {
        return nhomHocRepository.findByMaHocPhan(maHocPhan);
    }

//    @Transactional
//    public void importNhomHocFromExcel(MultipartFile file) {
//        try (InputStream inputStream = file.getInputStream();
//             Workbook workbook = new XSSFWorkbook(inputStream)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            List<NhomHoc> nhomHocList = new ArrayList<>();
//
//            for (Row row : sheet) {
//                if (row.getRowNum() == 0) continue; // Bỏ qua tiêu đề
//
//                NhomHoc nhomHoc = new NhomHoc();
//                nhomHoc.setMaNhomHoc(row.getCell(0).getStringCellValue());
//                nhomHoc.setMaHocPhan(row.getCell(1).getStringCellValue());
//                nhomHoc.setMaHocKi(row.getCell(2).getStringCellValue());
//                nhomHoc.setPhongHoc(row.getCell(3).getStringCellValue());
//                nhomHoc.setSoLuongSVToiDa((int) row.getCell(4).getNumericCellValue());
//                nhomHoc.setSoLuongSVConLai((int) row.getCell(5).getNumericCellValue());
//                nhomHoc.setSoTiet((int) row.getCell(6).getNumericCellValue());
//                nhomHoc.setThu((int) row.getCell(7).getNumericCellValue());
//                nhomHoc.setTietBatDau((int) row.getCell(8).getNumericCellValue());
//
//                nhomHocList.add(nhomHoc);
//            }
//
//            nhomHocRepository.saveAll(nhomHocList);
//        } catch (IOException e) {
//            throw new AppException(ErrorCode.FILE_PROCESSING_ERROR);
//        }
//    }


    @Transactional
    public void importNhomHocFromExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<NhomHoc> nhomHocList = new ArrayList<>();

            Map<String, Integer> maHocPhanMaxIndexMap = new HashMap<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    String maHocPhan = row.getCell(0).getStringCellValue().trim();
                    String maHocKi = row.getCell(1).getStringCellValue().trim();
                    String phongHoc = row.getCell(2).getStringCellValue().trim();
                    int soLuongSVToiDa = (int) row.getCell(3).getNumericCellValue();
                    int soTiet = (int) row.getCell(4).getNumericCellValue();
                    int thu = (int) row.getCell(5).getNumericCellValue();
                    int tietBatDau = (int) row.getCell(6).getNumericCellValue();

                    // Kiểm tra mã học kỳ có tồn tại
                    getHocKiByMaHocKi(maHocKi);

                    // Kiểm tra mã học phần có tồn tại
                    if (!checkHocPhanExists(maHocPhan)) {
                        throw new AppException(ErrorCode.HOC_PHAN_NOT_FOUND);
                    }

                    // Lấy hoặc tính số thứ tự hiện tại của nhóm học cho mã học phần này
                    int currentMaxIndex;
                    if (maHocPhanMaxIndexMap.containsKey(maHocPhan)) {
                        currentMaxIndex = maHocPhanMaxIndexMap.get(maHocPhan);
                    } else {
                        List<NhomHoc> existingNhomHoc = nhomHocRepository.findByMaHocPhan(maHocPhan);
                        currentMaxIndex = existingNhomHoc.stream()
                                .mapToInt(nhom -> {
                                    String suffix = nhom.getMaNhomHoc().replace(maHocPhan, "");
                                    try {
                                        return Integer.parseInt(suffix);
                                    } catch (NumberFormatException e) {
                                        return 0;
                                    }
                                })
                                .max()
                                .orElse(0);
                    }

                    // Tăng số thứ tự để tạo mã mới
                    currentMaxIndex++;
                    maHocPhanMaxIndexMap.put(maHocPhan, currentMaxIndex);

                    // Tạo mã nhóm học mới
                    String newMaNhomHoc = maHocPhan + String.format("%02d", currentMaxIndex);

                    // Tạo đối tượng NhomHoc
                    NhomHoc nhomHoc = new NhomHoc();
                    nhomHoc.setMaNhomHoc(newMaNhomHoc);
                    nhomHoc.setMaHocPhan(maHocPhan);
                    nhomHoc.setMaHocKi(maHocKi);
                    nhomHoc.setPhongHoc(phongHoc);
                    nhomHoc.setSoLuongSVToiDa(soLuongSVToiDa);
                    nhomHoc.setSoLuongSVConLai(soLuongSVToiDa);
                    nhomHoc.setSoTiet(soTiet);
                    nhomHoc.setThu(thu);
                    nhomHoc.setTietBatDau(tietBatDau);

                    nhomHocList.add(nhomHoc);
                } catch (Exception e) {
                    System.err.println("Lỗi tại dòng " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            // Lưu danh sách nhóm học hợp lệ vào database
            nhomHocRepository.saveAll(nhomHocList);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }



    @Transactional
    public NhomHocResponse updateSoLuongSVConLai(String maNhomHoc, UpdateSoLuongSVConLaiRequest request) {
        NhomHoc nhomHoc = nhomHocRepository.findByMaNhomHoc(maNhomHoc)
                .orElseThrow(() -> new AppException(ErrorCode.NHOM_HOC_NOT_FOUND));

        int soLuongMoi = nhomHoc.getSoLuongSVConLai() + request.getSoLuongSVConLai();

        if (soLuongMoi < 0 || soLuongMoi > nhomHoc.getSoLuongSVToiDa()) {
            throw new AppException(ErrorCode.INVALID_STUDENT_COUNT);
        }

        nhomHoc.setSoLuongSVConLai(soLuongMoi);
        nhomHocRepository.save(nhomHoc);

        return convertToResponse(nhomHoc);
    }

    public List<NhomHocResponse> getNhomHocByMaHocKi(String maHocKi) {
        return nhomHocRepository.findByMaHocKi(maHocKi).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    @Autowired
    private RestTemplate restTemplate;

//    private final String hocKyServiceUrl = "http://kong-gateway:8000/khht/api/hocki/{mahocki}";
    private final String hocKyServiceUrl = "http://localhost:8000/khht/api/hocki/{mahocki}";
//    private final String hocKyServiceUrl = "http://172.30.120.20:8000/khht/api/hocki/{mahocki}";
    public HocKiResponse getHocKiByMaHocKi(String mahocki) {
        try {
            return restTemplate.getForObject(hocKyServiceUrl, HocKiResponse.class, mahocki);
        } catch (Exception e) {
            throw new AppException(ErrorCode.HOCKI_NOT_FOUND);
        }
    }

    private boolean checkHocPhanExists(String maHocPhan) {
        String courseServiceUrl = "http://localhost:8000/course/api/courses/{maHocPhan}"; // Cập nhật URL đúng của course-service
//        String courseServiceUrl = "http://172.30.120.20:8000/course/api/courses/{maHocPhan}";
        try {
            restTemplate.getForObject(courseServiceUrl, String.class, maHocPhan);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public List<NhomHocResponse> getNhomHocByNamHoc(String namHoc) {
        List<NhomHoc> nhomHocList = nhomHocRepository.findByNamHoc(namHoc);

        return nhomHocList.stream().map(this::convertToResponse).toList();
    }
    public List<NhomHocResponse> getNhomHocByHocKiAndHocPhan(String maHocKi, String maHocPhan) {
        List<NhomHoc> nhomHocList = nhomHocRepository.findByMaHocKiAndMaHocPhan(maHocKi, maHocPhan);
        return nhomHocList.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    public long getTotalNhomHocInHocKi(String maHocKi) {
        return nhomHocRepository.countByMaHocKi(maHocKi);
    }

    @Transactional
    public void checkAndLockNhomHocForHocKi(String maHocKi) {
        List<NhomHoc> nhomHocList = nhomHocRepository.findByMaHocKi(maHocKi);

        for (NhomHoc nhomHoc : nhomHocList) {
            int soLuongDaDangKy = nhomHoc.getSoLuongSVToiDa() - nhomHoc.getSoLuongSVConLai();

            if (soLuongDaDangKy < 16) {
                nhomHoc.setLocked(true);
            } else {
                nhomHoc.setLocked(false);
            }

            nhomHocRepository.save(nhomHoc);
        }
    }

//    private boolean getKhoaCourse(String maHocPhan) {
//        String courseServiceUrl = "http://172.30.120.20:8082/course/api/courses/{maHocPhan}"; // Cập nhật URL đúng của course-service
//
//        try {
//            restTemplate.getForObject(courseServiceUrl, String.class, maHocPhan);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public List<Map<String, Object>> thongKeNhomHoc(String maHocKi) {
        List<NhomHoc> nhomHocList = nhomHocRepository.findByMaHocKi(maHocKi);
        List<Map<String, Object>> result = new ArrayList<>();

        for (NhomHoc nhom : nhomHocList) {
            int soLuongToiDa = nhom.getSoLuongSVToiDa();
            int soLuongDaDangKy = soLuongToiDa - nhom.getSoLuongSVConLai();

            Map<String, Object> nhomHocData = new HashMap<>();
            nhomHocData.put("maNhomHoc", nhom.getMaNhomHoc());
            nhomHocData.put("soLuongDaDangKy", soLuongDaDangKy);
            nhomHocData.put("soLuongToiDa", soLuongToiDa);
            nhomHocData.put("datTieuChuan", soLuongDaDangKy >= 16);

            result.add(nhomHocData);
        }
        return result;
    }
    public List<Map<String, Object>> thongKeNhomHocMahocKi(String maHocKi, String maHocPhan) {
        List<NhomHoc> nhomHocList = nhomHocRepository.findByMaHocKiAndMaHocPhan(maHocKi, maHocPhan);
        List<Map<String, Object>> result = new ArrayList<>();

        for (NhomHoc nhom : nhomHocList) {
            int soLuongToiDa = nhom.getSoLuongSVToiDa();
            int soLuongDaDangKy = soLuongToiDa - nhom.getSoLuongSVConLai();

            Map<String, Object> nhomHocData = new HashMap<>();
            nhomHocData.put("maNhomHoc", nhom.getMaNhomHoc());
            nhomHocData.put("soLuongDaDangKy", soLuongDaDangKy);
            nhomHocData.put("soLuongToiDa", soLuongToiDa);
            nhomHocData.put("datTieuChuan", soLuongDaDangKy >= 16);

            result.add(nhomHocData);
        }

        return result;
    }
    public List<String> getMaHocPhanByMaHocKi(String maHocKi) {
        List<NhomHoc> ds = nhomHocRepository.findAllByMaHocKi(maHocKi);
        return ds.stream()
                .map(NhomHoc::getMaHocPhan)
                .distinct()
                .collect(Collectors.toList());
    }


}
