package com.luanvan.courses_service.service;

//import com.luanvan.courses_service.controlller.FileController;
import com.luanvan.courses_service.dto.request.CourseRequest;
import com.luanvan.courses_service.dto.request.CourseUpdateRequest;
import com.luanvan.courses_service.dto.response.CourseResponse;
import com.luanvan.courses_service.entity.Course;
import com.luanvan.courses_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public CourseResponse getCourseByMahocphan(String mahocphan) {
        Course course = courseRepository.findByMahocphan(mahocphan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học phần: " + mahocphan));
        return mapToResponse(course);
    }
    public List<CourseResponse> getCoursesByTenhocphan(String tenhocphan) {
        return courseRepository.findByTenhocphanContaining(tenhocphan)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }



    public CourseResponse createCourse(CourseRequest request) {
        // Kiểm tra mã học phần đã tồn tại chưa
        if (courseRepository.existsByMahocphan(request.getMahocphan())) {
            throw new RuntimeException("Mã học phần đã tồn tại");
        }

        // Lấy URL file từ request, đảm bảo không bị null
        String fileUrl = request.getFileUrl() != null ? request.getFileUrl() : "";

        // Tạo đối tượng Course
        Course course = Course.builder()
                .mahocphan(request.getMahocphan())
                .tenhocphan(request.getTenhocphan())
                .sotinchi(request.getSotinchi())
                .mota(request.getMota())
                .fileUrl(fileUrl)
                .loaiHocPhan(request.getLoaiHocPhan())
                .build();

        // Lưu vào database và trả về response
        return mapToResponse(courseRepository.save(course));
    }


    public void deleteCourseByMahocphan(String mahocphan) {
        Optional<Course> course = courseRepository.findByMahocphan(mahocphan);
        course.ifPresent(courseRepository::delete);
    }




    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .mahocphan(course.getMahocphan())
                .tenhocphan(course.getTenhocphan())
                .sotinchi(course.getSotinchi())
                .mota(course.getMota())
//                .motaType(course.getMotaType())
                .fileUrl(course.getFileUrl())
                .loaiHocPhan(course.getLoaiHocPhan())
                .build();
    }

    public String getTenHocPhan(String maHocPhan) {
        return courseRepository.findByMahocphan(maHocPhan)
                .map(Course::getTenhocphan)
                .orElse("Không tìm thấy học phần");
    }

    public List<CourseResponse> getCoursesByIds(List<String> ids) {
        List<Course> courses = courseRepository.findByMahocphanIn(ids);
        return courses.stream()
                .map(this::mapToResponse)  // Dùng lại phương thức mapToResponse()
                .collect(Collectors.toList());
    }

    private Course convertToEntity(CourseRequest request) {
        return Course.builder()
                .mahocphan(request.getMahocphan())
                .tenhocphan(request.getTenhocphan())
                .sotinchi(request.getSotinchi())
                .mota(request.getMota())
                .fileUrl(request.getFileUrl())
                .loaiHocPhan(request.getLoaiHocPhan())
                .build();
    }



    //upload bang file excel
    public void importCoursesFromFile(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Course> courses = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                try {
                    String mahocphan = getCellValueAsString(row.getCell(0)).trim();
                    String tenhocphan = getCellValueAsString(row.getCell(1)).trim();
                    int sotinchi = (int) row.getCell(2).getNumericCellValue();
                    String mota = getCellValueAsString(row.getCell(3)).trim();
                    String fileUrl = getCellValueAsString(row.getCell(4)).trim();
                    String loaiHocPhan = getCellValueAsString(row.getCell(5)).trim();
                    // Kiểm tra xem mã học phần đã tồn tại chưa
                    if (courseRepository.existsByMahocphan(mahocphan)) {
                        System.out.println("Bỏ qua mã học phần đã tồn tại: " + mahocphan);
                        continue; // Nếu đã tồn tại thì bỏ qua dòng này
                    }

                    CourseRequest request = new CourseRequest(mahocphan, tenhocphan, sotinchi, mota, fileUrl, loaiHocPhan);

                    Course course = convertToEntity(request);
                    courses.add(course);

                } catch (Exception e) {
                    System.err.println("Lỗi tại dòng " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            // Lưu tất cả các học phần mới
            if (!courses.isEmpty()) {
                courseRepository.saveAll(courses);
                System.out.println("Đã thêm " + courses.size() + " học phần mới.");
            } else {
                System.out.println("Không có học phần mới nào được thêm.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đọc file: " + e.getMessage());
        }
    }



    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public List<CourseResponse> getCoursesByLoaiHocPhan(String loaiHocPhan) {
        return courseRepository.findByLoaiHocPhan(loaiHocPhan)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public long getTotalNumberOfCourses() {
        return courseRepository.count();
    }


}
