package com.example.demo.repository;

import com.example.demo.domain.StudyRoom;
import com.example.demo.domain.StudyRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface StudyRoomMemberRepository extends JpaRepository<StudyRoomMember, Long> {

    // 1. 중복 가입 방지: "이 이름(username)을 가진 사람이 이 방(room)에 있나요?"
    boolean existsByUsernameAndRoom(String username, StudyRoom room);

    @Query("SELECT m FROM StudyRoomMember m JOIN FETCH m.room WHERE m.username = :username")
    List<StudyRoomMember> findAllByUsername(@Param("username") String username);
}