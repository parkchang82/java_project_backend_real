package com.example.demo.domain;

import jakarta.persistence.*;

@Entity
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Lob
    private String content;

    @Column(length = 50)
    private String author;
    
    @Column
    private Long roomId;

    // === 기본 생성자 ===
    public Post() {}

    // === Getter / Setter (Lombok 없이) ===
    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}