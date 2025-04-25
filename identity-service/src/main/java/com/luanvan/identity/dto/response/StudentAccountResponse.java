package com.luanvan.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentAccountResponse {
    private String mssv;
    private String password;
}