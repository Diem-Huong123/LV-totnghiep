package com.luanvan.student_service.service;

import com.luanvan.student_service.dto.request.StudentRequest;
import com.luanvan.student_service.dto.response.StudentResponse;
import com.luanvan.student_service.entity.Student;
import com.luanvan.student_service.exception.AppException;
import com.luanvan.student_service.exception.ErrorCode;
import com.luanvan.student_service.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class StudentService {

    StudentRepository studentRepository;

    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public StudentResponse getStudentByMssv(String mssv) {
        Student student = studentRepository.findByMssv(mssv)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        return convertToResponse(student);
    }

    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.existsByMssv(request.getMssv())) {
            throw new AppException(ErrorCode.STUDENT_ALREADY_EXISTS);
        }

        Student student = convertToEntity(request);
        studentRepository.save(student);
        return convertToResponse(student);
    }

    public StudentResponse updateStudent(String mssv, StudentRequest request) {
        Student student = studentRepository.findByMssv(mssv)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        student.setFullName(request.getFullName());
        student.setEmail(request.getEmail());
        student.setDob(request.getDob());
        student.setGender(request.getGender());
        student.setMaLop(request.getMaLop());
        student.setMaNganh(request.getMaNganh());
        student.setKhoaHoc(request.getKhoaHoc());
        student.setMaKhoa(request.getMaKhoa());

        studentRepository.save(student);
        return convertToResponse(student);
    }

    public void deleteStudent(String mssv) {
        Student student = studentRepository.findByMssv(mssv)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));
        studentRepository.delete(student);
    }

    private Student convertToEntity(StudentRequest request) {
        return Student.builder()
                .mssv(request.getMssv())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .dob(request.getDob())
                .gender(request.getGender())
                .maLop(request.getMaLop())
                .maNganh(request.getMaNganh())
                .khoaHoc(request.getKhoaHoc())
                .maKhoa(request.getMaKhoa())
                .build();
    }

    private StudentResponse convertToResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .mssv(student.getMssv())
                .fullName(student.getFullName())
                .email(student.getEmail())
                .dob(student.getDob())
                .gender(student.getGender())
                .maLop(student.getMaLop())
                .maNganh(student.getMaNganh())
                .khoaHoc(student.getKhoaHoc())
                .maKhoa(student.getMaKhoa())
                .build();
    }
    public boolean existsByMssv(String mssv) {
        return studentRepository.existsByMssv(mssv);
    }

    public void importStudentsFromFile(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Student> students = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                try {
                    String mssv = getCellValueAsString(row.getCell(0));
                    String fullName = getCellValueAsString(row.getCell(1));
                    String email = getCellValueAsString(row.getCell(2));
                    LocalDate dob = getCellValueAsDate(row.getCell(3));
                    String gender = getCellValueAsString(row.getCell(4));
                    String maLop = getCellValueAsString(row.getCell(5));
                    String maNganh = getCellValueAsString(row.getCell(6));
                    String khoaHoc = getCellValueAsString(row.getCell(7));
                    String maKhoa = getCellValueAsString(row.getCell(8));

                    if (!studentRepository.existsByMssv(mssv)) {
                        // Dùng StudentRequest thay vì tạo trực tiếp Student
                        StudentRequest request = new StudentRequest(mssv, fullName, email, dob, gender, maLop, maNganh, khoaHoc, maKhoa);

                        // Chuyển StudentRequest thành Student
                        Student student = convertToEntity(request);
                        students.add(student);
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi tại dòng: " + row.getRowNum() + " - " + e.getMessage());
                }
            }

            if (!students.isEmpty()) {
                studentRepository.saveAll(students);
            }
            System.out.println("Dữ liệu từ file:");
            for (Student student : students) {
                System.out.println(student);
            }


        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file: " + e.getMessage());
        }
    }

    // Kiểm tra và đọc giá trị kiểu String (tránh lỗi nếu ô bị null)
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue()); // Tránh MSSV bị chuyển thành số có dấu phẩy
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    // Kiểm tra và đọc giá trị ngày tháng
    private LocalDate getCellValueAsDate(Cell cell) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else {
                String dateStr = cell.getStringCellValue().trim();
                DateTimeFormatter formatter;

                if (dateStr.contains("/")) {
                    formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
                } else if (dateStr.contains("-")) {
                    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                } else {
                    throw new IllegalArgumentException("Định dạng ngày không hợp lệ: " + dateStr);
                }
                return LocalDate.parse(dateStr, formatter);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc ngày tháng tại ô: " + cell.getAddress() + " - " + e.getMessage());
        }
    }

    public List<StudentResponse> getStudentsByMaKhoa(String maKhoa) {
        List<Student> students = studentRepository.findByMaKhoa(maKhoa);
        return students.stream()
                .map(this::convertToResponse) // Sử dụng phương thức chuyển đổi đã có
                .collect(Collectors.toList());
    }
    public List<StudentResponse> getStudentsByMaKhoaKhoaHocNganh(String maKhoa, String khoaHoc, String maNganh) {
        List<Student> students = studentRepository.findByMaKhoaAndKhoaHocAndMaNganh(maKhoa, khoaHoc, maNganh);
        return students.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    public List<String> getAllMaKhoa() {
        return studentRepository.findAllMaKhoaRaw()
                .stream()
                .map(Student::getMaKhoa)
                .distinct()
                .collect(Collectors.toList());
    }
    // Lấy danh sách khóa học theo mã khoa
    public List<String> getKhoasByKhoa(String maKhoa) {
        return studentRepository.findDistinctByMaKhoaAndKhoaHoc(maKhoa)
                .stream().map(Student::getKhoaHoc).distinct().collect(Collectors.toList());
    }

    // Lấy danh sách ngành học theo mã khoa và khóa học
    public List<String> getNganhsByKhoaHoc(String maKhoa, String khoaHoc) {
        return studentRepository.findDistinctByMaKhoaAndKhoaHocAndMaNganh(maKhoa, khoaHoc)
                .stream().map(Student::getMaNganh).distinct().collect(Collectors.toList());
    }

    public long getTotalStudentsByKhoaHoc(String khoaHoc) {
        return studentRepository.countByKhoaHoc(khoaHoc);
    }
    public long getTotalStudents() {
        return studentRepository.count();
    }

}
