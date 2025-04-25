package com.luanvan.khht_service.repository;
import com.luanvan.khht_service.entity.HocKi;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HocKiRepository extends MongoRepository<HocKi, String> {
    @Query("{ 'mahocki': { $regex: ?0, $options: 'i' } }")
    Optional<HocKi> findByMahocki(String mahocki);
    List<HocKi> findByNamhoc(String namHoc);
    List<HocKi> findByTenhocki(String tenHocKi);
    List<HocKi> findByNamhocAndTenhocki(String namHoc, String tenHocKi);
}