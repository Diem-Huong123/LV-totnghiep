package com.luanvan.identity.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@Service
public class StudentServiceClient {
    private final RestTemplate restTemplate;

    public StudentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public boolean isStudentExists(String mssv) {
        String url = "http://localhost:8000/student/api/students/exists/" + mssv;
        ResponseEntity<Boolean> response = restTemplate.getForEntity(url, Boolean.class);
        return Boolean.TRUE.equals(response.getBody());
    }
}
