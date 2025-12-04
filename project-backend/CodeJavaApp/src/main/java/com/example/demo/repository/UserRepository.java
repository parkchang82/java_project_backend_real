package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.domain.User;
import java.util.Optional; // 👈 Optional을 사용하기 위해 임포트합니다.

public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이메일로 User 엔티티를 찾는 메서드를 Optional<User>를 반환하도록 수정
    // 💡 사용자가 없을 경우 Optional.empty()를 반환하여 NullPointerException을 방지합니다.
    Optional<User> findByEmail(String email); // 👈 리턴 타입을 수정했습니다.
}
