package com.luanvan.nhomhocphan_service.repository;

import com.luanvan.nhomhocphan_service.entity.NhomHoc;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NhomHocRepository extends MongoRepository<NhomHoc, String> {
    List<NhomHoc> findByMaHocPhan(String maHocPhan);
    void deleteByMaNhomHoc(String maNhomHoc);
    Optional<NhomHoc> findByMaNhomHoc(String maNhomHoc);
    boolean existsByMaNhomHoc(String maNhomHoc);
    List<NhomHoc> findByMaHocKi(String maHocKi);
    List<NhomHoc> findByNamHoc(String namHoc);
    List<NhomHoc> findByMaHocKiAndMaHocPhan(String maHocKi, String maHocPhan);
    long countByMaHocKi(String maHocKi);
    @Query(value = "{ 'maHocKi' : ?0 }", fields = "{ 'maHocPhan' : 1 }")
    List<NhomHoc> findAllByMaHocKi(String maHocKi);


}