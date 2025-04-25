package com.luanvan.student_service.repository;
import com.luanvan.student_service.entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByMssv(String mssv); // Tìm theo MSSV
    boolean existsByMssv(String mssv);
    void deleteByMssv(String mssv);
    List<Student> findByMaKhoa(String maKhoa);
    List<Student> findByMaKhoaAndKhoaHocAndMaNganh(String maKhoa, String khoaHoc, String maNganh);
    @Query(value = "{}", fields = "{'maKhoa' : 1}")
    List<Student> findAllMaKhoaRaw();

    @Query(value = "{'maKhoa': ?0}", fields = "{'khoaHoc': 1}")
    List<Student> findDistinctByMaKhoaAndKhoaHoc(String maKhoa);


    @Query(value = "{'maKhoa': ?0, 'khoaHoc': ?1}", fields = "{'maNganh': 1}")
    List<Student> findDistinctByMaKhoaAndKhoaHocAndMaNganh(String maKhoa, String khoaHoc);

    long countByKhoaHoc(String khoaHoc);
    long count();
}