

//
//
//package com.luanvan.identity.controller;
//
//import com.luanvan.identity.dto.request.LoginRequest;
//import com.luanvan.identity.dto.response.AuthResponse;
//import com.luanvan.identity.service.AuthService;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//
//    private final AuthService authService;
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
//        AuthResponse authResponse = authService.authenticate(request, response);
//        return ResponseEntity.ok(authResponse);
//    }
//
//    @PostMapping("/refreshtoken")
//    public ResponseEntity<AuthResponse> refreshToken(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            return ResponseEntity.badRequest().body(null);
//        }
//
//        String refreshToken = null;
//        for (Cookie cookie : cookies) {
//            if ("refreshToken".equals(cookie.getName())) {
//                refreshToken = cookie.getValue();
//                break;
//            }
//        }
//
//        if (refreshToken == null) {
//            return ResponseEntity.badRequest().body(null);
//        }
//
//        AuthResponse response = authService.refreshToken(refreshToken);
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletResponse response) {
//        Cookie cookie = new Cookie("refreshToken", null);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
//        cookie.setPath("/");
//        cookie.setMaxAge(0);
//        response.addCookie(cookie);
//
//        return ResponseEntity.ok("Đăng xuất thành công");
//    }
//}
//









package com.luanvan.identity.controller;

import com.luanvan.identity.dto.request.LoginRequest;
import com.luanvan.identity.dto.request.SignupRequest;
import com.luanvan.identity.dto.response.AuthResponse;
import com.luanvan.identity.dto.response.StudentAccountResponse;
import com.luanvan.identity.entity.ActiveSession;
import com.luanvan.identity.entity.LoginHistory;
import com.luanvan.identity.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//http://localhost:8081/auth
//@CrossOrigin(origins = "http://192.168.0.103:8089", allowCredentials = "true")
//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //Đăng ký tài khoản mới
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok("Tạo tài khoản thành công!");
    }
    // API lấy danh sách tài khoản
    @GetMapping("/accounts")
    public List<StudentAccountResponse> getAllAccounts() {
        return authService.getAllAccounts();
    }

    // API cập nhật tài khoản
    @PutMapping("/accounts/{mssv}")
    public ResponseEntity<String> updateAccount(
            @PathVariable String mssv,
            @RequestBody SignupRequest request) {
        authService.updateAccount(mssv, request.getPassword());
        return ResponseEntity.ok("Cập nhật tài khoản thành công!");
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);

    }
//@PostMapping("/login")
//public AuthResponse login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
//    String clientIp = httpRequest.getHeader("X-Forwarded-For");
//    if (clientIp == null || clientIp.isEmpty()) {
//        clientIp = httpRequest.getRemoteAddr();
//    }
//
//    System.out.println("==> Sinh viên gọi API: " + request.getMssv() + " từ IP: " + clientIp);
//    return authService.authenticate(request);
//}


    @GetMapping("/verify")
    public String verifyToken() {
        return "Token hợp lệ!";
    }

    @PostMapping("/upload")
    public ResponseEntity<String> importStudents(@RequestParam("file") MultipartFile file) {
        try {
            authService.importStudentsFromExcel(file);
            return ResponseEntity.ok("Import danh sách sinh viên thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi import: " + e.getMessage());
        }
    }

//    @PostMapping("/refreshtoken")
//    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String refreshToken) {
//        AuthResponse response = authService.refreshToken(refreshToken);
//        return ResponseEntity.ok(response);
//    }

    // Đăng xuất theo MSSV
    @PostMapping("/logout")
    public ResponseEntity<String> logoutByMssv(@RequestParam String mssv) {
        authService.logoutByMssv(mssv);
        return ResponseEntity.ok("User " + mssv + " logged out.");
    }

    // Danh sách người đang đăng nhập
    @GetMapping("/active")
    public ResponseEntity<List<ActiveSession>> getActiveUsers() {
        return ResponseEntity.ok(authService.getActiveUsers());
    }

    // Lịch sử đăng nhập của 1 user
    @GetMapping("/history")
    public ResponseEntity<List<LoginHistory>> getLoginHistory(@RequestParam String mssv) {
        return ResponseEntity.ok(authService.getLoginHistory(mssv));
    }

}