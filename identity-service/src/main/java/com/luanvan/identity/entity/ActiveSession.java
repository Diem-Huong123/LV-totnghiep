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
@Document(collection = "active_sessions")
public class ActiveSession {
    @Id
    private String mssv;
    private String role;
    private Instant loginTime;
}
