package com.example.demo.service;

import com.example.demo.domain.Message;
import com.example.demo.domain.User;
import com.example.demo.dto.MessageResponseDto;
import com.example.demo.dto.MessageSendRequestDto;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.UserRepository;

// lombok.RequiredArgsConstructor를 import에서 삭제합니다!
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor를 삭제했습니다!
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    /**
     * [새로 추가]
     * @RequiredArgsConstructor 대신 수동으로 생성자를 추가합니다.
     * (Lombok 오류 해결)
     */
    public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    /**
     * 1. 쪽지 보내기 (이메일로 찾도록 수정됨)
     */
    @Transactional
    public MessageResponseDto sendMessage(Long senderId, MessageSendRequestDto requestDto) {
        
        // 1. 보내는 사람(sender) 조회 (이건 동일)
        User sender = userRepository.findById(senderId)
            .orElseThrow(() -> new IllegalArgumentException("보내는 사람을 찾을 수 없습니다. ID: " + senderId));

     // [수정된 부분!]
        // 2. 받는 사람(receiver)을 Email로 조회합니다. (님의 UserRepository 메서드 사용)
        User receiver = userRepository.findByEmail(requestDto.getReceiverEmail())
            .orElseThrow(() -> new IllegalArgumentException("받는 사람을 찾을 수 없습니다. Email: " + requestDto.getReceiverEmail()));
        
        // [수정 끝]

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(requestDto.getContent());

    message.setSentAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);

        // DTO의 static 메서드로 변환
        return MessageResponseDto.fromEntity(savedMessage);
    }

    /**
     * 2. 특정 사용자와의 대화 내역 불러오기
     */
    @Transactional
    public List<MessageResponseDto> getConversation(Long myId, Long otherUserId) {
        
        User me = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다. ID: " + myId));

        User other = userRepository.findById(otherUserId)
                .orElseThrow(() -> new IllegalArgumentException("상대방 정보를 찾을 수 없습니다. ID: " + otherUserId));

        List<Message> unreadMessages = messageRepository.findByReceiverAndSenderAndIsReadIsFalse(me, other);
        
        for (Message msg : unreadMessages) {
            msg.setIsRead(true); // '읽음' 처리
        }
        
        List<Message> conversation = messageRepository.findConversation(me, other);

        // [수정] .builder() 대신 DTO의 static 메서드 사용
        return conversation.stream()
                .map(MessageResponseDto::fromEntity) // 람다식 축약
                .collect(Collectors.toList());
    }

    /**
     * 3. 내 전체 대화 목록 불러오기 (최신 메시지 기준)
     */
    public List<MessageResponseDto> getMyConversations(Long myId) {
        User me = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다. ID: " + myId));
        
        List<Message> myMessages = messageRepository.findMyMessages(me);

        Map<Long, MessageResponseDto> latestMessages = new ConcurrentHashMap<>();

        for (Message msg : myMessages) {
            // [수정] .equals() 대신 ID 비교
            Long otherUserId;
            if (msg.getSender().getId().equals(myId)) {
                otherUserId = msg.getReceiver().getId();
            } else {
                otherUserId = msg.getSender().getId();
            }
            
            // [수정] .builder() 대신 DTO의 static 메서드 사용
            latestMessages.putIfAbsent(otherUserId, MessageResponseDto.fromEntity(msg));
        }

        return latestMessages.values().stream()
                .collect(Collectors.toList());
    }

    /**
     * 4. 안 읽은 메시지 총 개수 확인 (아이콘 배지용)
     */
    public int getUnreadMessageCount(Long myId) {
        User me = userRepository.findById(myId)
                .orElseThrow(() -> new IllegalArgumentException("내 정보를 찾을 수 없습니다. ID: " + myId));
        
        return messageRepository.countByReceiverAndIsReadIsFalse(me);
    }


    // --- 헬퍼 메서드 (Helper Method) ---
    /**
     * [수정] 
     * convertMessageToDto 메서드를 삭제했습니다.
     * (MessageResponseDto.fromEntity()로 대체되었기 때문)
     */
    // private MessageResponseDto convertMessageToDto(Message message) { ... } // 이 메서드 전체 삭제
}
