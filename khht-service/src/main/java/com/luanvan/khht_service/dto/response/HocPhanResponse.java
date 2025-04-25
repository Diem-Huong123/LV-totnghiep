package com.luanvan.khht_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HocPhanResponse {
    private String mahocphan;
    private String tenhocphan;
    private int sotinchi;

}