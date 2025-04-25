package com.luanvan.khht_service.controller;


import com.luanvan.khht_service.dto.request.HocKiRequest;
import com.luanvan.khht_service.dto.request.HocKiUpdateRequest;
import com.luanvan.khht_service.dto.response.HocKiResponse;
import com.luanvan.khht_service.service.HocKiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/hocki")
@RequiredArgsConstructor
public class HocKiController {
    private final HocKiService hocKiService;

    @GetMapping
    public List<HocKiResponse> getAllHocKi() {
        return hocKiService.getAllHocKi();
    } //

    @GetMapping("/{mahocki}")
    public HocKiResponse getHocKiByMahocki(@PathVariable String mahocki) {
        return hocKiService.getHocKiByMahocki(mahocki);
    } //

    @PostMapping
    public HocKiResponse createHocKi(@RequestBody HocKiRequest request) {
        return hocKiService.createHocKi(request);
    }//

    @PutMapping("/update/{mahocki}")
    public HocKiResponse updateHocKi(@PathVariable String mahocki, @RequestBody HocKiUpdateRequest request) {
        return hocKiService.updateHocKi(mahocki, request);
    }//

    @DeleteMapping("/{mahocki}")
    public void deleteHocKi(@PathVariable String mahocki) {
        hocKiService.deleteHocKi(mahocki);
    }//

    @GetMapping("/timkiem")
    public List<HocKiResponse> getHocKiByNamHocAndTenHocKi(
            @RequestParam(required = false) String namHoc,
            @RequestParam(required = false) String tenHocKi) {
        return hocKiService.getHocKiByNamHocAndTenHocKi(namHoc, tenHocKi);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> importHocKiExcel(@RequestParam("file") MultipartFile file) {
        hocKiService.importHocKiFromExcel(file);
        return ResponseEntity.ok("Import thành công");
    }


}
