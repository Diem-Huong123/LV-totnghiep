package com.luanvan.dkhp_service.repository;

import com.luanvan.dkhp_service.entity.DangKyHocPhan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DangKyHocPhanRepository extends MongoRepository<DangKyHocPhan, String> {

    List<DangKyHocPhan> findByMssv(String mssv);

    List<DangKyHocPhan> findByMaNhomHoc(String maNhomHoc);

    List<DangKyHocPhan> findByMssvAndMaHocKy(String mssv, String maHocKy);

    boolean existsByMssvAndMaNhomHocAndMaHocKy(String mssv, String maNhomHoc, String maHocKy);

    Optional<DangKyHocPhan> findByMssvAndMaHocKyAndMaNhomHoc(String mssv, String maHocKy, String maNhomHoc);

    long countByMaHocKy(String maHocKy);
}
