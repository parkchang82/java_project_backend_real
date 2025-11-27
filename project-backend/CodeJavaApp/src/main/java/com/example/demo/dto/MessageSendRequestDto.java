package com.example.demo.dto;

public class MessageSendRequestDto {

    // private Long receiverId; // 이 줄을
    private String receiverEmail; // 이렇게 변경!
    
    private String content;

    // --- 수동 Getter/Setter ---
    public MessageSendRequestDto() {
    }

    // --- receiverId 관련 get/set을 email로 변경 ---
    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}