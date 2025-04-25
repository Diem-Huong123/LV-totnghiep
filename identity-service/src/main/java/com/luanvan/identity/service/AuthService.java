package com.luanvan.identity.service;

import com.luanvan.identity.client.StudentServiceClient;
import com.luanvan.identity.dto.request.LoginRequest;
import com.luanvan.identity.dto.request.SignupRequest;
import com.luanvan.identity.dto.response.AuthResponse;
import com.luanvan.identity.dto.response.StudentAccountResponse;
import com.luanvan.identity.entity.ActiveSession;
import com.luanvan.identity.entity.LoginHistory;
import com.luanvan.identity.entity.StudentAccount;
import com.luanvan.identity.exception.AppException;
import com.luanvan.identity.exception.ErrorCode;
import com.luanvan.identity.repository.ActiveSessionRepository;
import com.luanvan.identity.repository.LoginHistoryRepository;
import com.luanvan.identity.repository.StudentAccountRepository;
import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AuthService {

    private final StudentAccountRepository studentAccountRepository;
    private final StudentServiceClient studentServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

//    private final RefreshTokenRepository refreshTokenRepository;

    private final LoginHistoryRepository loginHistoryRepository;
    private final ActiveSessionRepository activeSessionRepository;

    public AuthService(StudentAccountRepository studentAccountRepository,
                       StudentServiceClient studentServiceClient,
                       PasswordEncoder passwordEncoder,
                       JwtEncoder jwtEncoder,
                       LoginHistoryRepository loginHistoryRepository,
                       ActiveSessionRepository activeSessionRepository) { // Thêm tham số này
        this.studentAccountRepository = studentAccountRepository;
        this.studentServiceClient = studentServiceClient;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.loginHistoryRepository = loginHistoryRepository;
        this.activeSessionRepository = activeSessionRepository; // **Bổ sung dòng này**
    }
    @PostConstruct
    public void initAdminAccount() {
        // Kiểm tra xem admin đã tồn tại hay chưa
        if (!studentAccountRepository.existsByMssv("admin")) {
            String encodedPassword = passwordEncoder.encode("admin"); // Mật khẩu mặc định

            StudentAccount adminAccount = StudentAccount.builder()
                    .mssv("admin")
                    .password(encodedPassword)
                    .build();

            studentAccountRepository.save(adminAccount);
            System.out.println("Tài khoản admin đã được tạo tự động.");
        }
    }

    //ban chinh
//    public AuthResponse authenticate(LoginRequest request) {
//        StudentAccount student = studentAccountRepository.findByMssv(request.getMssv())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
//            throw new AppException(ErrorCode.INVALID_PASSWORD);
//        }
//
//        Instant now = Instant.now();
//        String role = student.getMssv().equals("admin") ? "ADMIN" : "STUDENT"; // Phân quyền
//
//        JwtClaimsSet claims = JwtClaimsSet.builder()
//                .subject(student.getMssv())
//                .issuedAt(now)
//                .expiresAt(now.plusSeconds(3600)) // 1 giờ
//                .claim("roles", role)
//                .build();
//
//        String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
//
//        return new AuthResponse(token, role);
//    }

    public AuthResponse login(LoginRequest request) {
        StudentAccount student = studentAccountRepository.findByMssv(request.getMssv())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getPassword(), student.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        String role = student.getMssv().equals("admin") ? "ADMIN" : "STUDENT";

        String accessToken = generateToken(student.getMssv(), role, "ACCESS", 3600);
//        String refreshToken = generateToken(student.getMssv(), role, "REFRESH", 604800);

//        refreshTokenRepository.save(RefreshToken.builder()
//                .mssv(student.getMssv())
//                .token(refreshToken)
//                .expiryDate(Instant.now().plusSeconds(604800))
//                .build());

        // Lưu vào danh sách người dùng đang đăng nhập
        activeSessionRepository.save(ActiveSession.builder()
                .mssv(student.getMssv())
                .role(role)
                .loginTime(Instant.now())
                .build());

        // Ghi vào lịch sử đăng nhập
        loginHistoryRepository.save(LoginHistory.builder()
                .mssv(student.getMssv())
                .role(role)
                .loginTime(Instant.now())
                .build());

        String backendIp = "Unknown";
        try {
            backendIp = InetAddress.getLocalHost().getHostAddress();
            System.out.println("Logging=====!!!!"+backendIp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new AuthResponse(accessToken, student.getMssv(), role, backendIp);
    }



//    public AuthResponse refreshToken(String refreshToken) {
//        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
//                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));
//
//        if (storedToken.getExpiryDate().isBefore(Instant.now())) {
//            refreshTokenRepository.deleteByMssv(storedToken.getMssv());
//            throw new AppException(ErrorCode.EXPIRED_TOKEN);
//        }
//
//        StudentAccount student = studentAccountRepository.findByMssv(storedToken.getMssv())
//                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
//
//        String role = student.getMssv().equals("admin") ? "ADMIN" : "STUDENT"; // Xác định quyền
//        String newAccessToken = generateToken(storedToken.getMssv(), role, "ACCESS", 900);
//
//        return new AuthResponse(newAccessToken, refreshToken, storedToken.getMssv(), role);
//    }
    private String generateToken(String mssv, String role, String type, long expirySeconds) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(mssv)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirySeconds))
                .claim("role", role) // Thêm role vào claims
                .claim("type", type) // Đánh dấu loại token (ACCESS/REFRESH)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void logoutByMssv(String mssv) {
        if (!studentAccountRepository.existsByMssv(mssv)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Xóa khỏi danh sách người dùng đang đăng nhập
        activeSessionRepository.deleteByMssv(mssv);

        // Cập nhật thời gian đăng xuất trong lịch sử đăng nhập
        LoginHistory latestLogin = loginHistoryRepository.findFirstByMssvOrderByLoginTimeDesc(mssv);
        if (latestLogin != null) {
            latestLogin.setLogoutTime(Instant.now());
            loginHistoryRepository.save(latestLogin);
        }
    }


    public void signup(SignupRequest request) {
        // Gọi API để kiểm tra mssv có tồn tại không
        if (!studentServiceClient.isStudentExists(request.getMssv())) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        // Kiểm tra tài khoản đã tồn tại chưa
        if (studentAccountRepository.existsByMssv(request.getMssv())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        // Mã hóa mật khẩu
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Tạo tài khoản sinh viên mới
        StudentAccount newStudent = StudentAccount.builder()
                .mssv(request.getMssv())
                .password(encodedPassword)
                .build();

        studentAccountRepository.save(newStudent);
    }

    public List<StudentAccountResponse> getAllAccounts() {
        List<StudentAccount> accounts = studentAccountRepository.findAll();
        return accounts.stream().map(account -> new StudentAccountResponse(
                account.getMssv(),
                account.getPassword() // Mật khẩu đã mã hóa, không nên gửi ra ngoài
        )).toList();
    }

    public void updateAccount(String mssv, String newPassword) {
        StudentAccount account = studentAccountRepository.findByMssv(mssv)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Mã hóa mật khẩu mới
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.setPassword(encodedPassword);

        studentAccountRepository.save(account);
    }


    public void importStudentsFromExcel(MultipartFile file) {
        List<StudentAccount> studentAccounts = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua dòng tiêu đề

                String mssv = row.getCell(0).getStringCellValue();
                String password = row.getCell(1).getStringCellValue();

                if (!studentServiceClient.isStudentExists(mssv)) {
                    throw new AppException(ErrorCode.USER_NOT_EXISTED);
                }

                if (!studentAccountRepository.existsByMssv(mssv)) {
                    String encodedPassword = passwordEncoder.encode(password);

                    StudentAccount student = StudentAccount.builder()
                            .mssv(mssv)
                            .password(encodedPassword)
                            .build();

                    studentAccounts.add(student);
                }
            }

            // Lưu tất cả sinh viên vào database
            studentAccountRepository.saveAll(studentAccounts);

        } catch (IOException e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<LoginHistory> getLoginHistory(String mssv) {
        return loginHistoryRepository.findByMssvOrderByLoginTimeDesc(mssv);
    }

    public List<ActiveSession> getActiveUsers() {
        return activeSessionRepository.findAll();
    }
}
