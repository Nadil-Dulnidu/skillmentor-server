package com.skillmentor.root.repository;

import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.entity.MentorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassRoomRepository extends JpaRepository<ClassRoomEntity, Integer> {
    Optional<ClassRoomEntity> findMentorByMentor(MentorEntity mentorEntity);
}