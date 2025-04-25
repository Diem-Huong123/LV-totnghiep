package com.luanvan.courses_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
public class CourseResponse {
    private String id;
    private String mahocphan;
    private String tenhocphan;
    private int sotinchi;
    private String mota;
//    private String motaType;
    private String fileUrl; // Hỗ trợ lưu link file
//    private String file; // Hỗ trợ upload file
private String loaiHocPhan;
}
