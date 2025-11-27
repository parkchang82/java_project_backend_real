package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    // 생성자 주입
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 이미지가 저장될 경로 (application.properties에서 가져옴)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public String updateProfileImage(Long userId, MultipartFile file) throws IOException {
        // 1. 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다. id=" + userId));

        if (!file.isEmpty()) {
            // 2. 파일명 중복 방지 (UUID 사용)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // 3. 폴더가 없으면 생성
            File folder = new File(uploadDir);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // 4. 파일 저장 (지정된 경로 + 파일명)
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);

            // 5. DB에 저장할 경로 설정 (웹에서 접근할 경로)
            // 예: /images/uuid_파일명.jpg
            String dbFilePath = "/images/" + fileName; 
            user.setProfileImage(dbFilePath);
            
            return dbFilePath; // 프론트엔드로 보낼 새 이미지 주소
        }
        return null;
    }
}