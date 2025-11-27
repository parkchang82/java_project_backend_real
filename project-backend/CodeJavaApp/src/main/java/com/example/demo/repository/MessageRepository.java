package com.example.demo.repository;

import com.example.demo.domain.Message;
import com.example.demo.domain.User; // User 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// <Message, Long>은 Message 엔티티를 다루고, PK의 타입이 Long이라는 의미
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * [필수 메서드 1]
     * 특정 두 사용자 간의 모든 대화 내역을 시간순으로 조회
     */
    @Query("SELECT m FROM Message m " +
           "WHERE (m.sender = :user1 AND m.receiver = :user2) " +
           "OR (m.sender = :user2 AND m.receiver = :user1) " +
           "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("user1") User user1, @Param("user2") User user2);

    /**
     * [필수 메서드 2]
     * 특정 사용자가 받은 안 읽은 메시지 개수 조회 (아이콘 배지용)
     */
    int countByReceiverAndIsReadIsFalse(User receiver);

    /**
     * [필수 메서드 3]
     * 특정 사용자가 받은 안 읽은 메시지 목록 조회 (전체 조회)
     */
    List<Message> findByReceiverAndIsReadIsFalse(User receiver);
    
    /**
     * [필수 메서드 4]
     * 특정 사용자와 관련된 모든 메시지(보낸/받은)를 최신순으로 조회
     */
    @Query("SELECT m FROM Message m " +
           "WHERE m.sender = :user OR m.receiver = :user " +
           "ORDER BY m.sentAt DESC")
    List<Message> findMyMessages(@Param("user") User user);
    
    /**
     * [필수 메서드 5]
     * 특정 사용자가 '다른 특정 사용자'에게 받은 안 읽은 메시지 목록 조회 (읽음 처리용)
     */
    List<Message> findByReceiverAndSenderAndIsReadIsFalse(User receiver, User sender);
}