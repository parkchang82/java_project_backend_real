package com.example.demo.service;

import com.example.demo.domain.StudyRoom;
import com.example.demo.domain.StudyRoomMember;
import com.example.demo.repository.StudyRoomMemberRepository;
import com.example.demo.repository.StudyRoomRepository;
import org.springframework.beans.factory.annotation.Autowired; // 추가됨
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudyService {

    private final StudyRoomRepository studyRoomRepository;
    private final StudyRoomMemberRepository studyRoomMemberRepository;

    // [수정] 생성자를 직접 만들어서 주입 (Lombok 없이도 작동)
    @Autowired
    public StudyService(StudyRoomRepository studyRoomRepository, 
                        StudyRoomMemberRepository studyRoomMemberRepository) {
        this.studyRoomRepository = studyRoomRepository;
        this.studyRoomMemberRepository = studyRoomMemberRepository;
    }

    // 1. 스터디 참여하기 기능
    public void joinStudy(Long studyId, String username) {
        StudyRoom room = studyRoomRepository.findById(studyId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디입니다."));

        if (studyRoomMemberRepository.existsByUsernameAndRoom(username, room)) {
            throw new IllegalStateException("이미 참여 중인 스터디입니다.");
        }

        StudyRoomMember member = new StudyRoomMember();
        member.setRoom(room);
        member.setUsername(username);

        studyRoomMemberRepository.save(member);
    }

    // 2. 내가 참여한 스터디 목록 조회 기능
    @Transactional(readOnly = true)
    public List<StudyRoom> getMyStudyList(String username) {
        return studyRoomMemberRepository.findAllByUsername(username).stream()
                .map(StudyRoomMember::getRoom)
                .collect(Collectors.toList());
    }
}