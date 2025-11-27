package com.example.demo.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class StudyRoomMember extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 방에 참여했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    @JsonIgnore
    private StudyRoom room;

    // 참여한 사람 아이디 or 이름
    @Column(nullable = false, length = 50)
    private String username;

    public StudyRoomMember() {}
    
    // Getter / Setter
    public Long getId() { return id; }

    public StudyRoom getRoom() { return room; }
    public void setRoom(StudyRoom room) { this.room = room; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    
}
