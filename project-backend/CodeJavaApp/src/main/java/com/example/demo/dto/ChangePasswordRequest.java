package com.example.demo.dto;

public class ChangePasswordRequest {
	private String email;
    private String currentPassword;
    private String newPassword;
    
    // Getter만 정의 (Setter는 필요 없음)
    public String getEmail() { return email; }
    public String getCurrentPassword() { return currentPassword; }
    public String getNewPassword() { return newPassword; }
}
