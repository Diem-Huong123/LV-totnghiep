package com.luanvan.dkhp_service.service;

import com.luanvan.dkhp_service.dto.request.DangKyHocPhanListRequest;
import com.luanvan.dkhp_service.dto.request.DangKyHocPhanRequest;
import com.luanvan.dkhp_service.dto.request.UpdateSoLuongSVConLaiRequest;
import com.luanvan.dkhp_service.dto.response.DangKyHocPhanResponse;
import com.luanvan.dkhp_service.dto.response.NhomHocResponse;
import com.luanvan.dkhp_service.entity.DangKyHocPhan;
import com.luanvan.dkhp_service.exception.AppException;
import com.luanvan.dkhp_service.exception.ErrorCode;
import com.luanvan.dkhp_service.repository.DangKyHocPhanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DangKyHocPhanService {

    @Autowired
    private DangKyHocPhanRepository dangKyHocPhanRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;

    private final String NHOM_HOC_SERVICE_URL = "http://localhost:8000/nhomdkhp/nhomhoc/";
    private final String COURSE_SERVICE_URL = "http://localhost:8000/course/api/courses/";


//    @Value("${nhom.hoc.service.url}")
//    private String NHOM_HOC_SERVICE_URL;
//
//    @Value("${course.service.url}")
//    private String COURSE_SERVICE_URL;

    // Các phương thức khác trong service

    public void exampleMethod() {
        System.out.println("NHOM_HOC_SERVICE_URL: " + NHOM_HOC_SERVICE_URL);
        System.out.println("COURSE_SERVICE_URL: " + COURSE_SERVICE_URL);
    }

    public List<DangKyHocPhanResponse> getAllDangKyHocPhan() {
        return dangKyHocPhanRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DangKyHocPhanResponse> getDangKyHocPhanByMssv(String mssv) {
        return dangKyHocPhanRepository.findByMssv(mssv).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DangKyHocPhanResponse> getDangKyHocPhanByMssvAndHocKy(String mssv, String maHocKy) {
        return dangKyHocPhanRepository.findByMssvAndMaHocKy(mssv, maHocKy).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public String getNhomHocDaDangKy(String mssv, String maHocKy, String maHocPhan) {
        List<DangKyHocPhan> danhSachDangKy = dangKyHocPhanRepository.findByMssvAndMaHocKy(mssv, maHocKy);

        for (DangKyHocPhan dangKy : danhSachDangKy) {
            NhomHocResponse nhomHoc = restTemplate.getForObject(
                    NHOM_HOC_SERVICE_URL + dangKy.getMaNhomHoc(),
                    NhomHocResponse.class
            );

            if (nhomHoc != null && nhomHoc.getMaHocPhan().equals(maHocPhan)) {
                return dangKy.getMaNhomHoc();
            }
        }
        return null;
    }
    //Dang ki hoc phan
    @Transactional
    public List<DangKyHocPhanResponse> registerMultipleHocPhan(DangKyHocPhanListRequest request) {
        if (request.getMssv() == null || request.getMaHocKy() == null || request.getDanhSachMaNhomHoc() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        List<DangKyHocPhanResponse> responses = new ArrayList<>();

        for (String maNhomHoc : request.getDanhSachMaNhomHoc()) {
            try {
                // 🔥 Lấy thông tin nhóm học để lấy `maHocPhan`
                NhomHocResponse nhomHoc = restTemplate.getForObject(
                        NHOM_HOC_SERVICE_URL + maNhomHoc,
                        NhomHocResponse.class
                );

                if (nhomHoc == null) {
                    throw new AppException(ErrorCode.NHOM_HOC_NOT_FOUND);
                }

                DangKyHocPhanRequest dangKyHocPhanRequest = new DangKyHocPhanRequest();
                dangKyHocPhanRequest.setMssv(request.getMssv());
                dangKyHocPhanRequest.setMaHocKy(request.getMaHocKy());
                dangKyHocPhanRequest.setMaNhomHoc(maNhomHoc);
                dangKyHocPhanRequest.setMaHocPhan(nhomHoc.getMaHocPhan());

                DangKyHocPhanResponse response = registerHocPhan(dangKyHocPhanRequest);
                responses.add(response);
            } catch (AppException e) {
                // Log lỗi nhưng không dừng hẳn quá trình đăng ký
            }
        }
        return responses;
    }


    @Transactional
    public DangKyHocPhanResponse registerHocPhan(DangKyHocPhanRequest request) {
        if (request.getMssv() == null || request.getMaNhomHoc() == null || request.getMaHocKy() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Kiểm tra xem sinh viên đã đăng ký nhóm học nào của học phần này chưa
        Query query = new Query();
        query.addCriteria(Criteria.where("mssv").is(request.getMssv()))
                .addCriteria(Criteria.where("maHocKy").is(request.getMaHocKy()));

        List<DangKyHocPhan> danhSachDangKy = mongoTemplate.find(query, DangKyHocPhan.class);
        for (DangKyHocPhan dangKy : danhSachDangKy) {
            NhomHocResponse nhomHoc = restTemplate.getForObject(
                    NHOM_HOC_SERVICE_URL + dangKy.getMaNhomHoc(),
                    NhomHocResponse.class
            );

            if (nhomHoc != null && nhomHoc.getMaHocPhan().equals(request.getMaHocPhan())) {
                throw new AppException(ErrorCode.ALREADY_REGISTERED);
            }
        }

        // Kiểm tra nhóm học có tồn tại không và còn chỗ không
        NhomHocResponse nhomHoc;
        try {
            nhomHoc = restTemplate.getForObject(
                    NHOM_HOC_SERVICE_URL + request.getMaNhomHoc(),
                    NhomHocResponse.class
            );
        } catch (Exception e) {
            throw new AppException(ErrorCode.NHOM_HOC_NOT_FOUND);
        }

        if (nhomHoc == null || nhomHoc.getSoLuongSVConLai() <= 0) {
            throw new AppException(ErrorCode.NHOM_HOC_NOT_FOUND);
        }

        //CẬP NHẬT SỐ LƯỢNG SV TRƯỚC KHI LƯU ĐĂNG KÝ
        UpdateSoLuongSVConLaiRequest updateRequest = new UpdateSoLuongSVConLaiRequest(-1);
        try {
            restTemplate.put(NHOM_HOC_SERVICE_URL + "slsvconlai/" + request.getMaNhomHoc(), updateRequest);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UPDATE_FAILED, "Không thể cập nhật số lượng sinh viên còn lại.");
        }

        // Tạo đối tượng đăng ký và lưu vào MongoDB
        DangKyHocPhan dangKyHocPhan = DangKyHocPhan.builder()
                .mssv(request.getMssv())
                .maNhomHoc(request.getMaNhomHoc())
                .maHocKy(request.getMaHocKy())
                .build();

        dangKyHocPhan = dangKyHocPhanRepository.save(dangKyHocPhan);
        return convertToResponse(dangKyHocPhan);
    }

//    @Transactional
//    public DangKyHocPhanResponse registerHocPhan(DangKyHocPhanRequest request) {
//        if (request.getMssv() == null || request.getMaNhomHoc() == null || request.getMaHocKy() == null || request.getMaHocPhan() == null) {
//            throw new AppException(ErrorCode.INVALID_REQUEST);
//        }
//
//        String nhomHocDaDangKy = getNhomHocDaDangKy(request.getMssv(), request.getMaHocKy(), request.getMaHocPhan());
//        if (nhomHocDaDangKy != null) {
//            throw new AppException(ErrorCode.ALREADY_REGISTERED);
//        }
//
//        NhomHocResponse nhomHoc;
//        try {
//            nhomHoc = restTemplate.getForObject(
//                    NHOM_HOC_SERVICE_URL + request.getMaNhomHoc(),
//                    NhomHocResponse.class
//            );
//        } catch (Exception e) {
//            throw new AppException(ErrorCode.NHOM_HOC_NOT_FOUND);
//        }
//
//        if (nhomHoc == null || nhomHoc.getSoLuongSVConLai() <= 0) {
//            throw new AppException(ErrorCode.NHOM_HOC_NOT_FOUND);
//        }
//
//        boolean isExist = dangKyHocPhanRepository.existsByMssvAndMaNhomHocAndMaHocKy(
//                request.getMssv(), request.getMaNhomHoc(), request.getMaHocKy());
//        if (isExist) {
//            throw new AppException(ErrorCode.ALREADY_REGISTERED);
//        }
//
//        DangKyHocPhan dangKyHocPhan = DangKyHocPhan.builder()
//                .mssv(request.getMssv())
//                .maNhomHoc(request.getMaNhomHoc())
//                .maHocKy(request.getMaHocKy())
//                .build();
//
//        dangKyHocPhan = dangKyHocPhanRepository.save(dangKyHocPhan);
//        return convertToResponse(dangKyHocPhan);
//    }

    @Transactional
    public void deleteDangKyHocPhan(String id) {
        DangKyHocPhan existingRecord = dangKyHocPhanRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RECORD_NOT_FOUND));

        dangKyHocPhanRepository.delete(existingRecord);
    }

    @Transactional
    public void deleteDangKyHocPhan(String mssv, String maHocKy, String maNhomHoc) {
        Optional<DangKyHocPhan> dangKyHocPhan = dangKyHocPhanRepository
                .findByMssvAndMaHocKyAndMaNhomHoc(mssv, maHocKy, maNhomHoc);

        if (dangKyHocPhan.isEmpty()) {
            throw new AppException(ErrorCode.RECORD_NOT_FOUND);
        }
        //Gọi API cập nhật số lượng sinh viên còn lại (+1)
        UpdateSoLuongSVConLaiRequest updateRequest = new UpdateSoLuongSVConLaiRequest(1);
        try {
            restTemplate.put(NHOM_HOC_SERVICE_URL + "slsvconlai/" + maNhomHoc, updateRequest);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UPDATE_FAILED, "Không thể cập nhật số lượng sinh viên còn lại.");
        }

        dangKyHocPhanRepository.delete(dangKyHocPhan.get());
    }

    private DangKyHocPhanResponse convertToResponse(DangKyHocPhan dangKyHocPhan) {
        NhomHocResponse nhomHoc = restTemplate.getForObject(
                NHOM_HOC_SERVICE_URL + dangKyHocPhan.getMaNhomHoc(),
                NhomHocResponse.class
        );

        String tenHocPhan = (nhomHoc != null) ? restTemplate.getForObject(
                COURSE_SERVICE_URL + nhomHoc.getMaHocPhan() + "/tenhocphan",
                String.class
        ) : "Không xác định";

        return new DangKyHocPhanResponse(
                dangKyHocPhan.getMssv(),
                dangKyHocPhan.getMaNhomHoc(),
                tenHocPhan,
                nhomHoc != null ? nhomHoc.getPhongHoc() : "N/A",
                nhomHoc != null ? nhomHoc.getSoTiet() : 0,
                nhomHoc != null ? nhomHoc.getThu() : 0,
                nhomHoc != null ? nhomHoc.getTietBatDau() : 0,
                dangKyHocPhan.getMaHocKy(),
                nhomHoc != null ? nhomHoc.getMaHocPhan() : "N/A"
        );
    }

    public List<DangKyHocPhanResponse> getThoiKhoaBieu(String mssv, String maHocKy) {
        return dangKyHocPhanRepository.findByMssvAndMaHocKy(mssv, maHocKy).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Phương thức để lấy tổng số sinh viên đã đăng ký học phần theo mã học kỳ
    public long getTotalDangKyHocPhanByHocKy(String maHocKy) {
        return dangKyHocPhanRepository.countByMaHocKy(maHocKy);
    }

}
