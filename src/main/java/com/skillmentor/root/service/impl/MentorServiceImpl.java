package com.skillmentor.root.service.impl;

import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.entity.MentorEntity;
import com.skillmentor.root.exception.MentorException;
import com.skillmentor.root.mapper.MentorEntityDTOMapper;
import com.skillmentor.root.repository.ClassRoomRepository;
import com.skillmentor.root.repository.MentorRepository;
import com.skillmentor.root.service.MentorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MentorServiceImpl implements MentorService {
    private final MentorRepository mentorRepository;
    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public MentorServiceImpl(MentorRepository mentorRepository, ClassRoomRepository classRoomRepository) {
        this.mentorRepository = mentorRepository;
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MentorDTO createMentor(final MentorDTO mentorDTO) {
        log.info("Creating mentor with details: {}", mentorDTO);
        if(Objects.isNull(mentorDTO)){
            log.error("Mentor data cannot be null");
            throw new IllegalArgumentException("Mentor data cannot be null");
        }
        log.debug("Mapping MentorDTO to MentorEntity: {}", mentorDTO);
        final MentorEntity mentorEntity = MentorEntityDTOMapper.map(mentorDTO);
        if (mentorDTO.getClassRoomId() != null) {
            final ClassRoomEntity classRoomEntity = classRoomRepository.findById(mentorDTO.getClassRoomId())
                    .orElseThrow(() -> {
                        log.error("Classroom not found with ID: {}", mentorDTO.getClassRoomId());
                        return new MentorException("Classroom not found with ID: " + mentorDTO.getClassRoomId());
                    });
            classRoomEntity.setMentor(mentorEntity);;
            final MentorEntity savedMentor = mentorRepository.save(mentorEntity);
            log.info("Mentor created: {}", savedMentor);
            classRoomRepository.save(classRoomEntity);
            log.info("Classroom created and associated with mentor: {}", classRoomEntity.getClassRoomId());
            log.debug("Mapping saved MentorEntity to MentorDTO: {}", savedMentor);
            return MentorEntityDTOMapper.map(savedMentor);
        }
        log.info("No Classroom ID provided, saving mentor without classroom association");
        final MentorEntity savedEntity = mentorRepository.save(mentorEntity);
        log.info("Mentor created without classroom association: {}", savedEntity);
        return MentorEntityDTOMapper.map(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MentorDTO> getAllMentors(final List<String> firstNames, final List<String> subjects) {
        log.info("Fetching all mentors with filters - First Names: {}, Subjects: {}", firstNames, subjects);
        return mentorRepository.findAll().stream()
                .filter(mentor -> firstNames == null ||
                                firstNames.isEmpty() ||
                                firstNames.contains(mentor.getFirstName()))
                .filter(mentor -> subjects == null || subjects.isEmpty() ||
                        subjects.contains(mentor.getSubject()))
                .map(mentorEntity -> {
                    final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository
                            .findMentorByMentor(mentorEntity);
                    final MentorDTO mentorDTO = MentorEntityDTOMapper.map(mentorEntity);
                    classRoomEntity.ifPresent(classRoomEntity1 ->
                            mentorDTO.setClassRoomId(classRoomEntity1.getClassRoomId()));
                    return mentorDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MentorDTO findMentorById(final Integer id) {
        log.info("Finding mentor by ID: {}", id);
        if(Objects.isNull(id)){
            log.error("Mentor ID cannot be null");
            throw new IllegalArgumentException("Mentor ID cannot be null");
        }
        final Optional<MentorEntity> mentorEntity =  mentorRepository.findById(id);
        if(mentorEntity.isPresent()){
            final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository
                    .findMentorByMentor(mentorEntity.get());
            log.debug("MentorDTO mapping with MentorEntity");
            final MentorDTO mentorDTO = MentorEntityDTOMapper.map(mentorEntity.get());
            classRoomEntity.ifPresent(roomEntity -> {
                mentorDTO.setClassRoomId(roomEntity.getClassRoomId());
                log.info("Classroom id assigned to the mentorDTO");
            });
            return mentorDTO;
        }else{
            log.error("No mentor found with ID: {}", id);
            throw new MentorException("No mentor found with ID: " + id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MentorDTO updateMentorById(final MentorDTO mentorDTO) {
        log.info("Updating mentor with details: {}", mentorDTO);
        if (Objects.isNull(mentorDTO) || Objects.isNull(mentorDTO.getMentorId())) {
            log.error("Mentor data or Mentor ID cannot be null");
            throw new IllegalArgumentException("Mentor data or Mentor ID cannot be null");
        }
        final MentorEntity mentorEntity = mentorRepository.findById(mentorDTO.getMentorId())
                .orElseThrow(() -> {
                    log.error("Cannot update. Mentor not found with ID: {}", mentorDTO.getMentorId());
                    return new MentorException("Cannot update. Mentor not found with ID: " + mentorDTO.getMentorId());
                });
        mentorEntity.setFirstName(mentorDTO.getFirstName());
        mentorEntity.setLastName(mentorDTO.getLastName());
        mentorEntity.setEmail(mentorDTO.getEmail());
        mentorEntity.setPhoneNumber(mentorDTO.getPhoneNumber());
        mentorEntity.setTitle(mentorDTO.getTitle());
        mentorEntity.setProfession(mentorDTO.getProfession());
        mentorEntity.setSubject(mentorDTO.getSubject());
        mentorEntity.setAddress(mentorDTO.getAddress());
        mentorEntity.setSessionFee(mentorDTO.getSessionFee());
        mentorEntity.setQualification(mentorDTO.getQualification());
        log.info("Mentor updated: {}", mentorEntity);
        final MentorEntity updatedEntity = mentorRepository.save(mentorEntity);
        log.info("Updated mentor saved successfully: {}", updatedEntity);
        return MentorEntityDTOMapper.map(updatedEntity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MentorDTO deleteMentorById(final Integer id) {
        log.info("Deleting mentor with ID: {}", id);
        if(Objects.isNull(id)){
            log.error("Mentor ID cannot be null");
            throw new IllegalArgumentException("Mentor ID cannot be null");
        }
        final MentorEntity mentorEntity = mentorRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete. Mentor not found with ID: {}", id);
                    return new MentorException("Cannot delete. Mentor not found with ID: " + id);
                });
        final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findMentorByMentor(mentorEntity);
        classRoomEntity.ifPresent(roomEntity -> {
            log.debug("Remove the reference to the mentor ");
            roomEntity.setMentor(null);
            log.info("Save classroom after removing mentor");
            classRoomRepository.save(roomEntity);
        });
        log.info("Found mentor for deletion: {}", mentorEntity);
        mentorRepository.deleteById(id);
        log.info("Mentor deleted successfully: {}", mentorEntity);
        return MentorEntityDTOMapper.map(mentorEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public MentorDTO findMentorByClerkId(final String clerkId) {
        log.info("Finding mentor by Clerk ID: {}", clerkId);
        if (Objects.isNull(clerkId)) {
            log.error("Clerk ID cannot be null or empty");
            throw new IllegalArgumentException("Clerk ID cannot be null or empty");
        }
        return mentorRepository.findByClerkMentorId(clerkId)
                .map(MentorEntityDTOMapper::map)
                .orElseThrow(() -> {
                    log.error("Mentor not found with Clerk ID: {}", clerkId);
                    return new MentorException("Mentor not found with Clerk ID: " + clerkId);
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MentorDTO deleteMentorByClerkId(final String clerkId) {
        log.info("Deleting mentor with Clerk ID: {}", clerkId);
        if (Objects.isNull(clerkId)) {
            log.error("Clerk ID cannot be null or empty");
            throw new IllegalArgumentException("Clerk ID cannot be null or empty");
        }
        final MentorEntity mentorEntity = mentorRepository.findByClerkMentorId(clerkId)
                .orElseThrow(() -> {
                    log.error("Cannot delete. Mentor not found with Clerk ID: {}", clerkId);
                    return new MentorException("Cannot delete. Mentor not found with Clerk ID: " + clerkId);
                });
        log.info("Found mentor for deletion: {}", mentorEntity);
        mentorRepository.delete(mentorEntity);
        log.info("Mentor deleted successfully: {}", mentorEntity);
        return MentorEntityDTOMapper.map(mentorEntity);
    }
}
