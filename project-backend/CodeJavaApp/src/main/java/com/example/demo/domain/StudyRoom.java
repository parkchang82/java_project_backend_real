package com.example.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;   // ⭐ 반드시 필요
import java.util.ArrayList;
import java.util.List;

@Entity
public class StudyRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;        // 방 이름

    @Column(length = 255)
    private String description; // 방 설명

    @Column(length = 50)
    private String host;        // 방장 아이디 또는 이름

    @Column
    private Long postId;

    // ⭐ 스케줄 (JSON 직렬화 차단 + cascade 삭제)
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Schedule> schedules = new ArrayList<>();

    // ⭐ 스터디 멤버 (JSON 직렬화 차단)
    @JsonIgnore
    @OneToMany(mappedBy = "room", cascade = CascadeType.REMOVE, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<StudyRoomMember> members = new ArrayList<>();

    // 기본 생성자
    public StudyRoom() {}

    // Getter / Setter
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }

    public List<Schedule> getSchedules() { return schedules; }
    public List<StudyRoomMember> getMembers() { return members; }
}
