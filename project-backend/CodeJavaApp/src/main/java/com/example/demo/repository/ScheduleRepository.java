package com.example.demo.repository;

import com.example.demo.domain.Schedule;
import com.example.demo.domain.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    // 기존 findByStudyId 삭제
    // StudyRoom 객체로 일정 조회
    List<Schedule> findByRoom(StudyRoom room);
}
