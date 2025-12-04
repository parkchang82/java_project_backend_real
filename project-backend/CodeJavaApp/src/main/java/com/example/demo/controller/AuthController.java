package com.example.demo.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder; // ⭐️ [수정] BCrypt -> PasswordEncoder (인터페이스)
import org.springframework.web.bind.annotation.*;

import com.example.demo.JwtUtil;
import com.example.demo.domain.User;
import com.example.demo.dto.ChangePasswordRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.UserSignUpRequestDto;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api")
public class AuthController {

    // ⭐️ [수정] 필드 주입(@Autowired) 대신 생성자 주입 방식으로 변경
    private final UserRepository repo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder; // ⭐️ [수정] BCrypt -> PasswordEncoder

    /**
     * ⭐️ [수정] 생성자 주입
     * SecurityConfig의 @Bean으로 등록된 '공식' PasswordEncoder를 주입받습니다.
     * 이제 회원가입/로그인/비밀번호 변경 모두 동일한 암호화기를 사용합니다.
     */
    @Autowired
    public AuthController(UserRepository repo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // ⭐️ [삭제] @Autowired 필드 및 자체 암호화기 생성 코드 삭제
    // @Autowired
    // private UserRepository repo;
    // @Autowired
    // private JwtUtil jwtUtil;
    // private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    // DTO 클래스는 사용자 코드 그대로 유지
    public static class LoginRequest {
        private String email;
        private String password;
        
        public String getEmail() { return email; }
        public String getPassword() { return password; }
    }

    /**
     * 🚪 회원가입 REST API
     * (사용자 코드 그대로 유지 - 이제 주입된 passwordEncoder를 사용합니다)
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserSignUpRequestDto requestDto) { // ⭐️ DTO로 받도록 수정!
    
        // 1. 이메일 중복 확인
        if (repo.findByEmail(requestDto.getEmail()).isPresent()) { // .isPresent()를 쓰는 게 좋습니다
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "이미 존재하는 이메일입니다."));
        }
        
        // 2. DTO -> Entity 변환 및 패스워드 암호화
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setName(requestDto.getName());
        user.setGender(requestDto.getGender());
        user.setDate(requestDto.getDate()); // 생년월일
        
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        user.setPassword(encodedPassword);
        
        // profileImage는 null이 허용된다고 가정하고 설정하지 않습니다.
        
        // 3. 저장
        repo.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", true, "message", "회원가입이 완료되었습니다."));
    }

    
    /**
     * 🔑 로그인 REST API: JWT 토큰 발급
     * (사용자 코드 그대로 유지 - 이제 주입된 passwordEncoder를 사용합니다)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String email = request.getEmail();
        String rawPassword = request.getPassword();
        
        User user = repo.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("이메일로 사용자를 찾을 수 없습니다."));

        // ⭐️ 'passwordEncoder'가 이제 '공식 암호화기'이므로 matches()가 성공합니다.
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "아이디 또는 비밀번호 오류입니다."));
        }

        // 인증 성공 시: JWT 토큰 생성
        String accessToken = jwtUtil.generateToken(user.getEmail());
        
        return ResponseEntity.ok(Map.of(
            "success", true, 
            "message", "로그인 성공",
            "accessToken", accessToken
        ));
    }
    

    /**
     * 👤 프로필 정보 조회 API
     * (사용자 코드 그대로 유지)
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        
        String authenticatedEmail = userDetails.getUsername(); 
        User user = repo.findByEmail(authenticatedEmail)
            .orElseThrow(() -> new IllegalArgumentException("인증된 이메일로 사용자를 찾을 수 없습니다."));

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "사용자 정보를 찾을 수 없습니다."));
        }
        
        // ⭐️ [핵심 수정] HashMap을 사용하여 ID와 프로필 이미지 추가 (null 허용)
        Map<String, Object> profileData = new java.util.HashMap<>();
        profileData.put("id", user.getId());             // 👈 프론트에서 필요했던 ID (undefined 해결)
        profileData.put("email", user.getEmail());
        profileData.put("name", user.getName());
        profileData.put("birthDate", user.getDate());
        profileData.put("gender", user.getGender());
        profileData.put("profileImage", user.getProfileImage()); // 👈 이미지 경로 추가
        
        return ResponseEntity.ok(profileData);
    }



    /**
     * 🔒 비밀번호 변경 API
     * (사용자 코드 그대로 유지 - 이제 주입된 passwordEncoder를 사용합니다)
     */
    @Transactional
    @PostMapping("/changepassword")
    public ResponseEntity<?> changePassword(
        @RequestBody ChangePasswordRequest request,
        Authentication authentication
    ) {
        // ⭐️ [수정] NullPointerException 방지
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String authenticatedEmail = authentication.getName();
        User user = repo.findByEmail(authenticatedEmail)
            .orElseThrow(() -> new IllegalArgumentException("업데이트할 사용자를 찾을 수 없습니다."));
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "사용자 정보를 찾을 수 없습니다."));
        }
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "비밀번호 변경에 실패했습니다. 현재 비밀번호를 다시 확인해주세요."));
        }
        
        String newEncodedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newEncodedPassword);
        
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("success", true, "message", "비밀번호가 성공적으로 변경되었습니다."));
    }


    /**
     * 🚪 안전한 로그아웃 API
     * (사용자 코드 그대로 유지)
     */
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(
        @RequestHeader(name = "Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7); 
        
        // (블랙리스트 로직)
        
        return ResponseEntity.ok(Map.of("success", true, "message", "로그아웃 요청이 처리되었습니다."));
    }
}
