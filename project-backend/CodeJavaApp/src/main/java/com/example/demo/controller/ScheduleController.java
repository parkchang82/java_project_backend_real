package com.example.demo.controller;

import com.example.demo.domain.Schedule;
import com.example.demo.domain.StudyRoom;
import com.example.demo.domain.StudyRoomMember;
import com.example.demo.repository.ScheduleRepository;
import com.example.demo.repository.StudyRoomMemberRepository;
import com.example.demo.repository.StudyRoomRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleRepository scheduleRepository;
    private final StudyRoomMemberRepository memberRepository;
    private final StudyRoomRepository roomRepository;

    public ScheduleController(ScheduleRepository scheduleRepository,
                              StudyRoomMemberRepository memberRepository,
                              StudyRoomRepository roomRepository) {
        this.scheduleRepository = scheduleRepository;
        this.memberRepository = memberRepository;
        this.roomRepository = roomRepository;
    }

    // ✅ 특정 스터디 일정 전체 조회: GET /schedules?roomId=
    @GetMapping
    public List<Schedule> getByRoom(@RequestParam("roomId") Long roomId) {

        StudyRoom room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("방이 존재하지 않습니다: " + roomId));

        return scheduleRepository.findByRoom(room);
    }

    // ✅ 일정 추가: POST /schedules
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ScheduleReq req) {

        // 1️⃣ 해당 방에 참여했는지 체크
        boolean joined = memberRepository.findAll().stream()
                .anyMatch(m ->
                        m.getRoom() != null &&
                        m.getRoom().getId().equals(req.roomId()) &&
                        m.getUsername().equals(req.username())
                );

        if (!joined) {
            return ResponseEntity.status(403).body("스터디에 참여해야 일정 추가가 가능합니다.");
        }

        // 2️⃣ 방 가져오기
        StudyRoom room = roomRepository.findById(req.roomId())
                .orElseThrow(() -> new RuntimeException("방이 존재하지 않습니다: " + req.roomId()));

        // 3️⃣ 일정 생성
        Schedule schedule = new Schedule();
        schedule.setRoom(room);
        schedule.setUsername(req.username());
        schedule.setDate(LocalDate.parse(req.date())); // yyyy-MM-dd
        schedule.setNote(req.note());

        Schedule saved = scheduleRepository.save(schedule);
        return ResponseEntity.ok(saved);
    }

    // ✅ 일정 삭제 (작성자만): DELETE /schedules/{id}?username=
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable("id") Long id,
            @RequestParam("username") String username) {

        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정이 존재하지 않습니다: " + id));

        if (!schedule.getUsername().equals(username)) {
            return ResponseEntity.status(403).body("작성자만 삭제할 수 있습니다.");
        }

        scheduleRepository.delete(schedule);
        return ResponseEntity.ok("삭제 완료: " + id);
    }

    // === 요청 DTO ===
    public static record ScheduleReq(Long roomId, String username, String date, String note) {}
}
