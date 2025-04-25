package com.luanvan.courses_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.Normalizer;
import java.util.regex.Pattern;

@Document(collection = "courses")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    private String id;
    private String mahocphan;
    private String tenhocphan;
    private int sotinchi;
    private String mota;
//    private String motaType;
    private String fileUrl; // Hỗ trợ lưu link file
//    private String file; // Hỗ trợ upload file
    private String loaiHocPhan;

    // Thêm trường tên học phần không dấu
    private String tenhocphanKhongDau;

    // Hàm bỏ dấu tiếng Việt
    public static String removeVietnameseAccents(String str) {
        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{M}");
        return pattern.matcher(normalized).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    // Cập nhật cả tên không dấu khi set
//    public void setTenhocphan(String tenhocphan) {
//        this.tenhocphan = tenhocphan;
//        this.tenhocphanKhongDau = removeVietnameseAccents(tenhocphan);
//    }

}
