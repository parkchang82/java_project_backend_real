package com.example.demo.controller;

import com.example.demo.domain.StudyRoom;
import com.example.demo.domain.StudyRoomMember;
import com.example.demo.repository.StudyRoomMemberRepository;
import com.example.demo.repository.StudyRoomRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
@RestController
@RequestMapping("/rooms")
public class StudyRoomController {

    private final StudyRoomRepository roomRepository;
    private final StudyRoomMemberRepository memberRepository;

    public StudyRoomController(StudyRoomRepository roomRepository,
                               StudyRoomMemberRepository memberRepository) {
        this.roomRepository = roomRepository;
        this.memberRepository = memberRepository;
    }

    public static record RoomCreateReq(String name, String description, String host) {}
    public static record JoinReq(String username) {}

    // 🟢 방 만들기
    @PostMapping
    public StudyRoom createRoom(@RequestBody RoomCreateReq req) {
        StudyRoom room = new StudyRoom();
        room.setName(req.name());
        room.setDescription(req.description());
        room.setHost(req.host());
        return roomRepository.save(room);
    }

    // 🔵 방 목록 보기
    @GetMapping
    public List<StudyRoom> listRooms() {
        return roomRepository.findAll();
    }

    // 🔵 방 하나 상세
    @GetMapping("/{id}")
    public StudyRoom getRoom(@PathVariable("id") Long id) { // ("id") 추가
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("room not found: " + id));
    }

    // 🟡 방 참여하기
    @PostMapping("/{id}/join")
    public String joinRoom(@PathVariable("id") Long id, @RequestBody JoinReq req) { // ("id") 추가
        StudyRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("room not found: " + id));

        if (memberRepository.existsByUsernameAndRoom(req.username(), room)) {
            return "이미 이 방에 참여한 사용자입니다.";
        }

        StudyRoomMember member = new StudyRoomMember();
        member.setRoom(room);
        member.setUsername(req.username());
        memberRepository.save(member);

        return "참여 완료";
    }

 // ⭐ 내 스터디 일정 보기 (500 에러 해결 버전)
    // List<StudyRoom> 대신 List<Map<String, Object>>를 반환하여 JSON 오류를 원천 차단합니다.
    @GetMapping("/my-schedule")
    public List<Map<String, Object>> getMyStudies(@RequestParam("username") String username) {
        System.out.println("📢 [API 요청] 스터디 목록 조회: " + username);

        List<StudyRoomMember> members = memberRepository.findAllByUsername(username);
        
        // 원본 Entity(StudyRoom)를 바로 주지 말고, Map으로 변환해서 줍니다.
        return members.stream()
                .map(member -> {
                    StudyRoom room = member.getRoom();
                    Map<String, Object> dto = new HashMap<>();
                    
                    // 필요한 정보만 쏙쏙 뽑아 담기
                    dto.put("id", room.getId());
                    dto.put("name", room.getName());
                    dto.put("description", room.getDescription());
                    dto.put("host", room.getHost());
                    
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 🔵 방 참여 인원 목록 보기
    @GetMapping("/{id}/members")
    public List<StudyRoomMember> listMembers(@PathVariable("id") Long id) { // ("id") 추가
        return memberRepository.findAll().stream()
                .filter(m -> m.getRoom().getId().equals(id))
                .toList();
    }

    // 🔴 방 나가기
    @DeleteMapping("/{id}/leave")
    public String leaveRoom(@PathVariable("id") Long id, @RequestParam("username") String username) { // 둘 다 이름 추가
        StudyRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("room not found: " + id));

        List<StudyRoomMember> targetMembers = memberRepository.findAll().stream()
                .filter(m -> m.getRoom().getId().equals(id) && m.getUsername().equals(username))
                .toList();

        if (targetMembers.isEmpty()) {
            return "해당 방에 참여 중이 아닙니다.";
        }

        memberRepository.deleteAll(targetMembers);
        return "방 나가기 완료";
    }

    // 🔴 방 삭제하기
    @DeleteMapping("/{id}")
    public String deleteRoom(@PathVariable("id") Long id, @RequestParam("host") String host) { // 둘 다 이름 추가
        StudyRoom room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("room not found: " + id));

        if (!room.getHost().equals(host)) {
            throw new RuntimeException("방장만 방을 삭제할 수 있습니다.");
        }

        List<StudyRoomMember> members = memberRepository.findAll().stream()
                .filter(m -> m.getRoom().getId().equals(id))
                .toList();

        memberRepository.deleteAll(members);
        roomRepository.delete(room);

        return "방 삭제 완료";
    }
}