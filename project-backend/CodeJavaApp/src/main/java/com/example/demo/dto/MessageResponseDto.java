package com.example.demo.dto;

import com.example.demo.domain.Message;
import com.example.demo.domain.User;
// lombok import들을 모두 삭제!

import java.time.LocalDateTime;

// @Data, @NoArgsConstructor 삭제!
public class MessageResponseDto {

    private Long messageId;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private boolean isRead;
    private String senderName;
    private String receiverName;

    // 1. 기본 생성자
    public MessageResponseDto() {
    }

    /**
     * 엔티티를 DTO로 변환하는 static 팩토리 메서드
     * (이건 Service에서 사용)
     */
    public static MessageResponseDto fromEntity(Message message) {
        MessageResponseDto dto = new MessageResponseDto();
        
        // DTO의 Setter 메서드를 사용하여 값 설정
        dto.setMessageId(message.getMessageId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setRead(message.getIsRead());
        
        User sender = message.getSender();
        User receiver = message.getReceiver();
        
        dto.setSenderId(sender.getId());
        dto.setSenderName(sender.getName());
        dto.setReceiverId(receiver.getId());
        dto.setReceiverName(receiver.getName());
        
        return dto;
    }

    // 2. Getter / Setter
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}