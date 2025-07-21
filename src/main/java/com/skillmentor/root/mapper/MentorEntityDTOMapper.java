package com.skillmentor.root.mapper;

import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.entity.MentorEntity;

import java.util.Objects;

public class MentorEntityDTOMapper {
    public static MentorDTO map(MentorEntity mentorEntity) {
        if(Objects.isNull(mentorEntity)){
            throw new IllegalArgumentException("mentorEntity cannot be null");
        }
        MentorDTO mentorDTO = new MentorDTO();
        mentorDTO.setMentorId(mentorEntity.getMentorId());
        mentorDTO.setClerkMentorId(mentorEntity.getClerkMentorId());
        mentorDTO.setFirstName(mentorEntity.getFirstName());
        mentorDTO.setLastName(mentorEntity.getLastName());
        mentorDTO.setEmail(mentorEntity.getEmail());
        mentorDTO.setProfession(mentorEntity.getProfession());
        mentorDTO.setPhoneNumber(mentorEntity.getPhoneNumber());
        mentorDTO.setAddress(mentorEntity.getAddress());
        mentorDTO.setSessionFee(mentorEntity.getSessionFee());
        mentorDTO.setTitle(mentorEntity.getTitle());
        mentorDTO.setSubject(mentorEntity.getSubject());
        mentorDTO.setMentorImage(mentorEntity.getMentorImage());
        mentorDTO.setQualification(mentorEntity.getQualification());
        return mentorDTO;
    }

    public static MentorEntity map(MentorDTO mentorDTO) {
        if(Objects.isNull(mentorDTO)){
            throw new IllegalArgumentException("MentorDTO cannot be null");
        }
        MentorEntity mentorEntity = new MentorEntity();
        mentorEntity.setMentorId(mentorDTO.getMentorId());
        mentorEntity.setClerkMentorId(mentorDTO.getClerkMentorId());
        mentorEntity.setFirstName(mentorDTO.getFirstName());
        mentorEntity.setLastName(mentorDTO.getLastName());
        mentorEntity.setEmail(mentorDTO.getEmail());
        mentorEntity.setProfession(mentorDTO.getProfession());
        mentorEntity.setPhoneNumber(mentorDTO.getPhoneNumber());
        mentorEntity.setAddress(mentorDTO.getAddress());
        mentorEntity.setSessionFee(mentorDTO.getSessionFee());
        mentorEntity.setTitle(mentorDTO.getTitle());
        mentorEntity.setSubject(mentorDTO.getSubject());
        mentorEntity.setMentorImage(mentorDTO.getMentorImage());
        mentorEntity.setQualification(mentorDTO.getQualification());
        return mentorEntity;
    }
}