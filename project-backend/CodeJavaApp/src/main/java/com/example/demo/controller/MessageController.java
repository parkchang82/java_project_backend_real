package com.example.demo.controller;

import com.example.demo.CustomUserDetails; // ⭐️ [추가]
import com.example.demo.dto.MessageResponseDto;
import com.example.demo.dto.MessageSendRequestDto;
import com.example.demo.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ⭐️ [추가]
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ❌ getCurrentUserId() 메서드는 이제 삭제합니다! (하드코딩 제거)

    /**
     * 1. 쪽지 보내기
     */
    @PostMapping
    public ResponseEntity<?> sendMessage(
        @AuthenticationPrincipal CustomUserDetails userDetails, // ⭐️ 실제 로그인 유저 정보 주입
        @RequestBody MessageSendRequestDto requestDto
    ) {
        // ⭐️ 로그인 안 된 경우 예외 처리
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 하드코딩된 1L 대신, 실제 유저의 ID를 꺼내서 사용
        Long currentUserId = userDetails.getUser().getId();
        
        MessageResponseDto responseDto = messageService.sendMessage(currentUserId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 2. 특정 사용자와의 대화 내역 불러오기
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<?> getConversation(
        @AuthenticationPrincipal CustomUserDetails userDetails, // ⭐️ 추가
        @PathVariable Long otherUserId
    ) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long currentUserId = userDetails.getUser().getId();
        
        List<MessageResponseDto> conversation = messageService.getConversation(currentUserId, otherUserId);
        return ResponseEntity.ok(conversation);
    }

    /**
     * 3. 내 전체 대화 목록 불러오기
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getMyConversations(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Long currentUserId = userDetails.getUser().getId();
        
        List<MessageResponseDto> conversations = messageService.getMyConversations(currentUserId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * 4. 안 읽은 메시지 총 개수
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(@AuthenticationPrincipal CustomUserDetails userDetails) {
        // 로그인 안 되어 있으면 0개 반환
        if (userDetails == null) return ResponseEntity.ok(Map.of("count", 0));

        Long currentUserId = userDetails.getUser().getId();
        
        int count = messageService.getUnreadMessageCount(currentUserId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
