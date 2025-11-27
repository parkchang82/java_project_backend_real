package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 User 엔티티를 찾는 메서드
    User findByEmail(String email);
}
