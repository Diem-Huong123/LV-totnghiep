package com.luanvan.identity.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "login_history")
public class LoginHistory {
    @Id
    private String id;
    private String mssv;
    private String role;
    private Instant loginTime;
    private Instant logoutTime;
}
