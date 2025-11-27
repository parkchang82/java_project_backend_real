package com.example.demo;

import com.example.demo.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // ⭐️ 403 오류 해결의 핵심 ⭐️
    // 이 사용자가 어떤 권한을 가졌는지 Spring Security에 알려줍니다.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 모든 사용자에게 "ROLE_USER" 권한을 부여합니다.
        // (만약 User 객체에 role 필드가 있다면 user.getRole()을 사용할 수 있습니다.)
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        // DB에서 가져온 사용자의 암호화된 비밀번호
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Spring Security에서 'username'은 고유 식별자입니다.
        // 여기서는 이메일을 사용합니다.
        return user.getEmail();
    }

    // --- 이하 계정 상태 관련 메소드들 ---
    // (특별한 비즈니스 로직이 없다면 모두 true로 반환해도 됩니다.)

    @Override
    public boolean isAccountNonExpired() {
        // 계정이 만료되었는지?
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정이 잠겼는지?
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 비밀번호가 만료되었는지?
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 계정이 활성화되었는지?
        return true;
    }
    
    // (선택) 원본 User 객체가 필요할 경우를 위한 헬퍼 메소드
    public User getUser() {
        return user;
    }
}