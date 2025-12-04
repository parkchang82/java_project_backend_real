package com.example.demo.dto;

public class UserSignUpRequestDto {
    
    // 프론트엔드가 보낼 것으로 예상되는 필수 필드만 포함
    private String email;
    private String password;
    private String name;
    private String gender; // User 엔티티에 @Column(nullable=false)이므로 필요
    private String date;   // User 엔티티의 생년월일 필드

    // ⭐️ Getter/Setter는 필수입니다!

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
