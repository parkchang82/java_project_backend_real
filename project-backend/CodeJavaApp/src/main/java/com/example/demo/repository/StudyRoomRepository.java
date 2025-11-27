package com.example.demo.repository;

import com.example.demo.domain.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
	StudyRoom findByPostId(Long postId);
}
