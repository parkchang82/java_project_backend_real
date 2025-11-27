package com.example.demo.controller;

import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users") // 주소 규칙
public class UserController {

    @Autowired
    private UserService userService;

    // 프로필 이미지 업로드
    // 요청 주소: POST /api/users/{id}/profile-image
    @PostMapping("/{id}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = userService.updateProfileImage(id, file);
            return ResponseEntity.ok(imageUrl); // 성공 시 이미지 주소 반환
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("업로드 실패: " + e.getMessage());
        } 
    }
    
}