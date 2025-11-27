package com.example.demo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 이 필터는 HTTP 요청 헤더에서 JWT 토큰을 추출하고 검증하여 Spring Security Context에 인증 정보를 설정합니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // 생성자를 통해 JwtUtil과 UserDetailsService를 주입받습니다.
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. HTTP 헤더에서 'Authorization' 값을 가져옵니다.
        String authorizationHeader = request.getHeader("Authorization");
        String token = null;
        String userEmail = null;

        // 2. 헤더가 'Bearer '로 시작하는지 확인하고 토큰을 추출합니다.
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);
            
            // 3. 토큰이 유효한지 확인하고 사용자 이메일을 가져옵니다.
            if (jwtUtil.validateToken(token)) {
                userEmail = jwtUtil.getUsernameFromToken(token);
            }
        }

        // 4. 이메일이 유효하고 아직 SecurityContext에 인증 정보가 없을 경우 처리
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // 5. UserDetailsService를 사용하여 DB에서 사용자 정보를 로드합니다.
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            
            // 6. 인증 토큰을 생성합니다.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            
            // 7. Security Context에 인증 정보를 설정합니다. (인증 완료)
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 8. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }
}