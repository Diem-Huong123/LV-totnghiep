package com.luanvan.courses_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseUpdateRequest {
    private String tenhocphan;
    private int sotinchi;
    private String mota;
    private String motaType;
    private String fileUrl; // Hỗ trợ lưu link file
//    private MultipartFile file; // Hỗ trợ upload file
}
