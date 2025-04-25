package com.luanvan.khht_service.service;

import com.luanvan.khht_service.dto.response.HocPhanResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CourseServiceClient {

    private final RestTemplate restTemplate;

    @Value("${course-service.url}")
    private String baseUrl;

    public CourseServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public HocPhanResponse getHocPhanByMaHocPhan(String maHocPhan) {
        String url = baseUrl + "/courses/" + maHocPhan;
        try {
            return restTemplate.getForObject(url, HocPhanResponse.class);
        } catch (Exception e) {
            return null;
        }
    }
}
