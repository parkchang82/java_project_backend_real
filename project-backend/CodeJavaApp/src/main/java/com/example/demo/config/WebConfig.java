//package com.example.demo.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    // application.properties에서 설정한 경로(C:/uploads/)를 가져옴
//    @Value("${file.upload-dir}")
//    private String uploadDir;
//
//    // 1. CORS 설정 (기존 내용 유지)
// // 파일: com.example.demo.config.WebConfig.java
//
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            // 로컬 주소와 배포된 프론트엔드 주소 모두 허용하도록 수정
//            .allowedOrigins(
//                "http://localhost:3000", // 로컬 환경
//                "https://java-project-frontend-real.onrender.com" // 배포된 프론트엔드 주소 (HTTPS 포함)
//            )
//            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//            .allowCredentials(true)
//            .maxAge(3600);
//    }
//
//    // 2. 이미지 경로 매핑 설정 (새로 추가된 부분)
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        // 웹 브라우저에서 http://localhost:8080/images/파일명.jpg 로 접속하면
//        // 실제 컴퓨터의 C:/uploads/ 폴더에서 파일을 찾아서 보여줌
//        registry.addResourceHandler("/images/**")
//                .addResourceLocations("file:///" + uploadDir);
//    }
//}