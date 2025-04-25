package com.luanvan.student_service.controller;


import com.luanvan.student_service.dto.request.StudentRequest;
import com.luanvan.student_service.dto.response.StudentResponse;
import com.luanvan.student_service.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{mssv}")
    public ResponseEntity<StudentResponse> getStudentByMssv(@PathVariable String mssv) {
        return ResponseEntity.ok(studentService.getStudentByMssv(mssv));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(@RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.createStudent(request));
    }

    @PutMapping("/{mssv}")
    public ResponseEntity<StudentResponse> updateStudent(@PathVariable String mssv, @RequestBody StudentRequest request) {
        return ResponseEntity.ok(studentService.updateStudent(mssv, request));
    }

    @DeleteMapping("/{mssv}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String mssv) {
        studentService.deleteStudent(mssv);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/exists/{mssv}")
    public ResponseEntity<Boolean> checkStudentExists(@PathVariable String mssv) {
        boolean exists = studentService.existsByMssv(mssv);
        return ResponseEntity.ok(exists);
    }

    //upload file
    @PostMapping("/upload")
    public ResponseEntity<String> uploadStudents(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File trống!");
        }

        studentService.importStudentsFromFile(file);
        return ResponseEntity.ok("Upload thành công!");
    }

    @GetMapping("/makhoa/{maKhoa}")
    public ResponseEntity<List<StudentResponse>> getStudentsByMaKhoa(@PathVariable String maKhoa) {
        return ResponseEntity.ok(studentService.getStudentsByMaKhoa(maKhoa));
    }
    // Endpoint lấy sinh viên theo maKhoa, khoaHoc và maNganh
    @GetMapping("/filter")
    public ResponseEntity<List<StudentResponse>> getStudentsByMaKhoaKhoaHocNganh(
            @RequestParam String maKhoa,
            @RequestParam String khoaHoc,
            @RequestParam String maNganh) {
        List<StudentResponse> students = studentService.getStudentsByMaKhoaKhoaHocNganh(maKhoa, khoaHoc, maNganh);
        return ResponseEntity.ok(students);
    }
    @GetMapping("/makhoa")
    public ResponseEntity<List<String>> getAllMaKhoa() {
        return ResponseEntity.ok(studentService.getAllMaKhoa());
    }

    // Lấy danh sách khóa học theo mã khoa
    @GetMapping("/khoahoc/{maKhoa}")
    public ResponseEntity<List<String>> getKhoaHocByMaKhoa(@PathVariable String maKhoa) {
        return ResponseEntity.ok(studentService.getKhoasByKhoa(maKhoa));
    }

    // Lấy danh sách ngành học theo mã khoa và khóa học
    @GetMapping("/nganh/{maKhoa}/{khoaHoc}")
    public ResponseEntity<List<String>> getNganhByMaKhoaKhoaHoc(
            @PathVariable String maKhoa,
            @PathVariable String khoaHoc) {
        return ResponseEntity.ok(studentService.getNganhsByKhoaHoc(maKhoa, khoaHoc));
    }

    @GetMapping("/total/{khoaHoc}")
    public ResponseEntity<Long> getTotalStudentsByKhoaHoc(@PathVariable String khoaHoc) {
        long total = studentService.getTotalStudentsByKhoaHoc(khoaHoc);
        return ResponseEntity.ok(total);
    }
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalStudents() {
        long total = studentService.getTotalStudents();
        return ResponseEntity.ok(total);
    }
}
