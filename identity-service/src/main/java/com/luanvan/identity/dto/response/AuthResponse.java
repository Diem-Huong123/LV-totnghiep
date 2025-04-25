package com.luanvan.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
//    private String token;
//    private String role;
    private String accessToken;
//    private String refreshToken;
    private String mssv;
    private String role;
    private String backendIp;
}