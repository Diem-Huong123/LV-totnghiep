package com.luanvan.courses_service.repository;

import com.luanvan.courses_service.entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends MongoRepository<Course, String> {

    // Tìm chính xác theo mã học phần, không phân biệt hoa thường
    @Query("{ 'mahocphan': { $regex: '^?0$', $options: 'i' } }")
    Optional<Course> findByMahocphan(String mahocphan);
//
//    // Kiểm tra mã học phần tồn tại không (tối ưu hơn, không cần @Query)
    boolean existsByMahocphan(String mahocphan);

    // Tìm kiếm gần đúng theo tên học phần (không phân biệt chữ hoa thường)
    @Query("{ 'tenhocphan': { $regex: ?0, $options: 'i' } }")
    List<Course> findByTenhocphanContaining(String tenhocphan);

//    // Xóa theo mã học phần (Tìm trước rồi xóa)
//    void deleteByMahocphan(String mahocphan);

    List<Course> findByMahocphanIn(List<String> mahocphan);
    List<Course> findByLoaiHocPhan(String loaiHocPhan);
}
