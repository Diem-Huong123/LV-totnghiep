package com.luanvan.identity.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "identity")
public class StudentAccount {

    @Id
    private String id;

    private String mssv;

    private String password; // Mật khẩu đã mã hóa

    private String role;

}
