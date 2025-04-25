package com.luanvan.khht_service.repository;

import com.luanvan.khht_service.entity.KeHoachHocTap;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface KeHoachHocTapRepository extends MongoRepository<KeHoachHocTap, String> {
    List<KeHoachHocTap> findByMaSinhVien(String maSinhVien);
    Optional<KeHoachHocTap> findByMaSinhVienAndMaHocKy(String maSinhVien, String maHocKy);
    List<KeHoachHocTap> findByMaHocKy(String maHocKy);
    long countByMaHocKy(String maHocKy);
}