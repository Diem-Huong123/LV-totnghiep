package com.luanvan.student_service.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Document(collection = "students")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {

    @Id
    String id;

    @Indexed(unique = true)
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
