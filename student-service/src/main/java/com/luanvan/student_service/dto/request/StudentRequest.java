package com.luanvan.student_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentRequest {
    String mssv;
    String fullName;
    String email;
    LocalDate dob;
    String gender;
    String maLop;
    String maNganh;
    String khoaHoc;
    String maKhoa;
}
