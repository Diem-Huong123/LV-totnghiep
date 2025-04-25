package com.luanvan.courses_service.controlller;

import com.luanvan.courses_service.dto.request.CourseRequest;
import com.luanvan.courses_service.dto.request.CourseUpdateRequest;
import com.luanvan.courses_service.dto.response.CourseResponse;
import com.luanvan.courses_service.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@RequestBody @Valid CourseRequest request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }


    @GetMapping("/{mahocphan}")
    public ResponseEntity<CourseResponse> getCourseByMahocphan(@PathVariable String mahocphan) {
        return ResponseEntity.ok(courseService.getCourseByMahocphan(mahocphan));
    }

    @GetMapping("/tenhocphan")
    public ResponseEntity<List<CourseResponse>> getCoursesByTenhocphan(@RequestParam String tenhocphan) {
        return ResponseEntity.ok(courseService.getCoursesByTenhocphan(tenhocphan));
    }




//    @PutMapping("/{mahocphan}")
//    public ResponseEntity<CourseResponse> updateCourseByMahocphan(@PathVariable String mahocphan, @ModelAttribute CourseUpdateRequest request) {
//        return ResponseEntity.ok(courseService.updateCourseByMahocphan(mahocphan, request));
//    }

//    @PutMapping("/{mahocphan}")
//    public ResponseEntity<CourseResponse> updateCourse(
//            @PathVariable String mahocphan,
//            @ModelAttribute CourseUpdateRequest request) {
//        return ResponseEntity.ok(courseService.updateCourseByMahocphan(mahocphan, request));
//    }


    @DeleteMapping("/mahocphan/{mahocphan}")
    public ResponseEntity<String> deleteCourseByMahocphan(@PathVariable String mahocphan) {
        courseService.deleteCourseByMahocphan(mahocphan);
        return ResponseEntity.ok("Học phần đã được xóa thành công: " + mahocphan);
    }

    @GetMapping("/{maHocPhan}/tenhocphan")
    public ResponseEntity<String> getTenHocPhan(@PathVariable String maHocPhan) {
        String tenHocPhan = courseService.getTenHocPhan(maHocPhan);
        return ResponseEntity.ok(tenHocPhan);
    }

    @GetMapping("/filter")
    public ResponseEntity<List<CourseResponse>> getCoursesByIds(@RequestParam List<String> ids) {
        return ResponseEntity.ok(courseService.getCoursesByIds(ids));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> importCoursesFromFile(@RequestParam("file") MultipartFile file) {
        courseService.importCoursesFromFile(file);
        return ResponseEntity.ok("Import học phần thành công!");
    }

    @GetMapping("/phanloai/{loaiHocPhan}")
    public ResponseEntity<List<CourseResponse>> getCoursesByLoaiHocPhan(@PathVariable String loaiHocPhan) {
        return ResponseEntity.ok(courseService.getCoursesByLoaiHocPhan(loaiHocPhan));
    }

    @GetMapping("/total")
    public ResponseEntity<Long> getTotalCourses() {
        long totalCourses = courseService.getTotalNumberOfCourses();
        return ResponseEntity.ok(totalCourses);
    }
}
