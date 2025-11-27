package com.example.demo.controller;

import com.example.demo.dto.MessageResponseDto;
import com.example.demo.dto.MessageSendRequestDto;
import com.example.demo.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController // 이 클래스가 REST API 컨트롤러임을 선언
@RequestMapping("/api/messages") // 이 컨트롤러의 모든 API는 /api/messages로 시작
public class MessageController {

    private final MessageService messageService;

    // 1. 수동 생성자 (Lombok 없는 버전)
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * (중요) 현재 로그인한 사용자 ID를 가져오는 메서드
     * * 지금은 테스트를 위해 1L (1번 유저)을 반환하도록 하드코딩했습니다.
     * * [나중에 할 일]
     * Spring Security를 사용하고 있다면, 
     * Authentication 객체에서 (UserDetails) auth.getPrincipal() 등을 통해
     * 실제 로그인한 사용자의 ID를 가져와야 합니다.
     */
    private Long getCurrentUserId() {
        // [임시] 테스트를 위해 1번 사용자가 로그인했다고 가정
        return 1L; 
        
        // [Spring Security 예시]
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // if (authentication == null || !authentication.isAuthenticated()) {
        //     throw new IllegalStateException("인증되지 않은 사용자입니다.");
        // }
        // // UserDetailsImpl 같은 커스텀 UserDetails에서 ID를 가져오는 로직
        // return ((UserDetailsImpl) authentication.getPrincipal()).getId(); 
    }

    /**
     * 1. 쪽지 보내기
     * [POST] /api/messages
     */
    @PostMapping
    public ResponseEntity<MessageResponseDto> sendMessage(@RequestBody MessageSendRequestDto requestDto) {
        Long currentUserId = getCurrentUserId(); // 현재 로그인한 사용자 ID
        
        // 서비스의 sendMessage 메서드 호출
        MessageResponseDto responseDto = messageService.sendMessage(currentUserId, requestDto);
        
        // HTTP 201 Created (생성 성공) 상태 코드와 함께 응답 DTO 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    /**
     * 2. 특정 사용자와의 대화 내역 불러오기 (클릭 시)
     * [GET] /api/messages/conversation/{otherUserId}
     * * @param otherUserId 대화 상대방의 ID
     */
    @GetMapping("/conversation/{otherUserId}")
    public ResponseEntity<List<MessageResponseDto>> getConversation(@PathVariable Long otherUserId) {
        Long currentUserId = getCurrentUserId(); // 내 ID
        
        // 서비스 호출 (이때 '읽음' 처리가 됨)
        List<MessageResponseDto> conversation = messageService.getConversation(currentUserId, otherUserId);
        
        // HTTP 200 OK와 함께 대화 목록 반환
        return ResponseEntity.ok(conversation);
    }

    /**
     * 3. 내 전체 대화 목록 불러오기 (쪽지함 첫 화면)
     * [GET] /api/messages/conversations
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<MessageResponseDto>> getMyConversations() {
        Long currentUserId = getCurrentUserId(); // 내 ID
        
        // 서비스 호출 (상대방별 최신 메시지 1개씩)
        List<MessageResponseDto> conversations = messageService.getMyConversations(currentUserId);
        
        return ResponseEntity.ok(conversations);
    }

    /**
     * 4. 안 읽은 메시지 총 개수 (아이콘 배지용)
     * [GET] /api/messages/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Integer>> getUnreadCount() {
        Long currentUserId = getCurrentUserId(); // 내 ID
        
        int count = messageService.getUnreadMessageCount(currentUserId);
        
        // {"count": 5} 형태의 JSON으로 반환
        return ResponseEntity.ok(Map.of("count", count));
    }
}