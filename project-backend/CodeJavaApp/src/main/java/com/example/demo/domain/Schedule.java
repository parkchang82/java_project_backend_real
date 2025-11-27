package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Schedule extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore  // ⭐ 직렬화 문제 해결(필수)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private StudyRoom room; // StudyRoom과 ManyToOne 관계

    private String username; // 작성자

    private LocalDate date; // 일정 날짜

    @Column(length = 255)
    private String note; // 일정 내용

    public Schedule() {}

    // getter & setter
    public Long getId() { return id; }

    public StudyRoom getRoom() { return room; }
    public void setRoom(StudyRoom room) { this.room = room; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
