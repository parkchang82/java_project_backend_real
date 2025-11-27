package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🚨 1. application.properties 또는 application.yml에 정의되어야 합니다. 
    // 최소 256비트(32글자) 이상의 랜덤한 문자열을 사용해야 보안에 안전합니다.
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime; // 토큰 유효 시간 (밀리초 단위)

    private Key getSigningKey() {
        // Base64 인코딩된 문자열을 Key 객체로 변환합니다.
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * 🔑 JWT 토큰을 생성합니다. (AuthController에서 사용)
     * @param subject 토큰에 담을 주체 (로그인 유저의 이메일)
     * @return 생성된 JWT 문자열
     */
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(subject) // 토큰 주체 (로그인 시 사용자의 고유 ID)
                .setIssuedAt(now) // 생성 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명 (비밀키 사용)
                .compact();
    }

    /**
     * 👤 토큰에서 주체(Username/Email)를 추출합니다. (Security Filter에서 사용)
     * @param token JWT 토큰
     * @return 주체 정보 (이메일)
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * ✅ 토큰의 유효성을 검증합니다. (Security Filter에서 사용)
     * @param token JWT 토큰
     * @return 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (Exception e) {
            // 토큰 만료, 잘못된 서명 등 오류 발생 시 false 반환
            return false;
        }
    }

    // 토큰에서 클레임(Claim)을 추출하는 내부 메서드
    private Claims getClaimsFromToken(String token) {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
        return claimsJws.getBody();
    }
}