package com.example.demo.controller;

import com.example.demo.CustomUserDetails;
import com.example.demo.domain.StudyRoom;
import com.example.demo.repository.StudyRoomRepository;
import com.example.demo.domain.Post;
import com.example.demo.repository.PostRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.example.demo.repository.StudyRoomMemberRepository;
import com.example.demo.domain.StudyRoomMember;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMemberRepository roomMemberRepository;

    public PostController(PostRepository postRepository,
                          StudyRoomRepository studyRoomRepository,
                          StudyRoomMemberRepository roomMemberRepository) {
        this.postRepository = postRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    // DTO
    public static record PostCreateReq(String title, String content) {}
    public static record PostUpdateReq(String title, String content) {}

    // =============================
    // 🔵 글 작성 + 스터디룸 자동 생성
    // =============================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody PostCreateReq req,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 1) 게시글 생성
        Post p = new Post();
        p.setTitle(req.title());
        p.setContent(req.content());
        p.setAuthor(userDetails.getUsername());
        postRepository.save(p);

        // 2) 스터디룸 생성
        StudyRoom room = new StudyRoom();
        room.setName(req.title());
        room.setDescription(req.content());
        room.setHost(userDetails.getUsername());

        // ⭐ 반드시 추가해야 정상 동작
        room.setPostId(p.getId());

        studyRoomRepository.save(room);

        // 3) 게시글에 roomId 연결
        p.setRoomId(room.getId());
        postRepository.save(p);

        return ResponseEntity.ok(p);
    }

    // =============================
    // 🔵 글 전체 조회
    // =============================
    @GetMapping
    public List<Post> list() {
        return postRepository.findAll();
    }

    // =============================
    // 🔵 글 단건 조회
    // =============================
    @GetMapping("/{id}")
    public Post getOne(@PathVariable("id") Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("post not found: " + id));
    }

    // =============================
    // 🟡 글 수정 (작성자만)
    // =============================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                    @RequestBody PostUpdateReq req,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Post p = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("post not found: " + id));

        if (!p.getAuthor().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 수정 가능합니다.");
        }

        // 게시글 수정
        p.setTitle(req.title());
        p.setContent(req.content());
        postRepository.save(p);

        // 스터디룸도 함께 수정
        StudyRoom room = studyRoomRepository.findById(p.getRoomId())
                .orElseThrow(() -> new RuntimeException("room not found: " + p.getRoomId()));

        room.setName(req.title());
        room.setDescription(req.content());
        studyRoomRepository.save(room);

        return ResponseEntity.ok(p);
    }

    // =============================
    // 🔴 글 삭제 (작성자만)
    // =============================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id,
                                    @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Post p = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("post not found: " + id));

        if (!p.getAuthor().equals(userDetails.getUsername())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 삭제 가능합니다.");
        }

        // ⭐ 연결된 StudyRoom 삭제
        Long roomId = p.getRoomId();

        if (roomId != null) {

            // 1) 방 참여 멤버 전부 삭제
            List<StudyRoomMember> members = roomMemberRepository.findAll()
                    .stream()
                    .filter(m -> m.getRoom().getId().equals(roomId))
                    .toList();

            roomMemberRepository.deleteAll(members);

            // 2) 방 삭제
            studyRoomRepository.deleteById(roomId);
        }

        // 3) 게시글 삭제
        postRepository.delete(p);

        return ResponseEntity.ok("삭제 완료: " + id);
    }
}
