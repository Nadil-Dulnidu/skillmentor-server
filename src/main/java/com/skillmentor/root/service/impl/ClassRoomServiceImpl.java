package com.skillmentor.root.service.impl;

import com.skillmentor.root.dto.ClassRoomDTO;
import com.skillmentor.root.dto.MentorDTO;
import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.exception.ClassRoomException;
import com.skillmentor.root.mapper.ClassRoomEntityDTOMapper;
import com.skillmentor.root.mapper.MentorEntityDTOMapper;
import com.skillmentor.root.repository.ClassRoomRepository;
import com.skillmentor.root.service.ClassRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ClassRoomServiceImpl implements ClassRoomService {
    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public ClassRoomServiceImpl(ClassRoomRepository classRoomRepository) {
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassRoomDTO createClassRoom(final ClassRoomDTO classRoomDTO) {
        log.info("Creating new classroom with details: {}", classRoomDTO);
        if (Objects.isNull(classRoomDTO)) {
            log.error("ClassRoom data cannot be null");
            throw new IllegalArgumentException("ClassRoom data cannot be null");
        }
        log.debug("Mapping ClassRoomDTO to ClassRoomEntity: {}", classRoomDTO);
        final ClassRoomEntity classRoomEntity = ClassRoomEntityDTOMapper.map(classRoomDTO);
        final ClassRoomEntity savedEntity = classRoomRepository.save(classRoomEntity);
        log.info("ClassRoom created successfully with ID: {}", savedEntity.getClassRoomId());
        return ClassRoomEntityDTOMapper.map(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassRoomDTO> getAllClassRooms() {
        log.info("Fetching all classrooms from the repository");
        final List<ClassRoomEntity> classRoomEntities = classRoomRepository.findAll();
        return classRoomEntities.stream()
                .map(entity -> {
                    log.debug("Mapping ClassRoomEntity to ClassRoomDTO: {}", entity);
                    final ClassRoomDTO classRoomDTO = ClassRoomEntityDTOMapper.map(entity);
                    if (entity.getMentor() != null) {
                        log.debug("Mapping MentorEntity to MentorDTO for ClassRoom: {}", entity.getClassRoomId());
                        final MentorDTO mentorDTO = MentorEntityDTOMapper.map(entity.getMentor());
                        classRoomDTO.setMentorDTO(mentorDTO);
                    }
                    return classRoomDTO;
                }
        ).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClassRoomDTO findClassRoomById(final Integer id) {
        log.info("Fetching classroom with ID: {}", id);
        if(Objects.isNull(id)) {
            log.error("ClassRoom ID cannot be null");
            throw new IllegalArgumentException("ClassRoom ID cannot be null");
        }
        final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findById(id);
        if (classRoomEntity.isEmpty()) {
            log.error("ClassRoom not found with ID: {}", id);
            throw new ClassRoomException("ClassRoom not found");
        }
        final ClassRoomEntity entity = classRoomEntity.get();
        log.debug("Mapping ClassRoomEntity to ClassRoomDTO: {}", entity);
        final ClassRoomDTO classRoomDTO = ClassRoomEntityDTOMapper.map(entity);
        if (entity.getMentor() != null) {
            log.debug("Mapping MentorEntity to MentorDTO for ClassRoom: {}", entity.getClassRoomId());
            final MentorDTO mentorDTO = MentorEntityDTOMapper.map(entity.getMentor());
            classRoomDTO.setMentorDTO(mentorDTO);
        }
        log.info("ClassRoom found and mapped: {}", classRoomDTO);
        return classRoomDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassRoomDTO deleteClassRoomById(final Integer id) {
        log.info("Deleting classroom with ID: {}", id);
        if (Objects.isNull(id)) {
            log.error("ClassRoom ID cannot be null");
            throw new IllegalArgumentException("ClassRoom ID cannot be null");
        }
        log.debug("Checking if ClassRoom with ID: {} exists", id);
        final Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findById(id);
        if (classRoomEntity.isEmpty()) {
            log.error("ClassRoom not found with ID: {}", id);
            throw new ClassRoomException("ClassRoom not found");
        }
        classRoomRepository.deleteById(id);
        log.info("ClassRoom with ID: {} deleted successfully", id);
        return ClassRoomEntityDTOMapper.map(classRoomEntity.get());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClassRoomDTO updateClassRoom(final ClassRoomDTO classRoomDTO) {
        log.info("Updating classroom with ID: {}", classRoomDTO.getClassRoomId());
        if (Objects.isNull(classRoomDTO)) {
            log.error("ClassRoom data or ClassRoom ID cannot be null");
            throw new IllegalArgumentException("ClassRoom data or ClassRoom ID cannot be null");
        }
        Optional<ClassRoomEntity> classRoomEntity = classRoomRepository.findById(classRoomDTO.getClassRoomId());
        if (classRoomEntity.isEmpty()) {
            log.error("ClassRoom not found with ID: {}", classRoomDTO.getClassRoomId());
            throw new ClassRoomException("ClassRoom not found");
        }
        final ClassRoomEntity updatedEntity = classRoomEntity.get();
        updatedEntity.setTitle(classRoomDTO.getTitle());
        updatedEntity.setClassImage(classRoomDTO.getClassImage());
        final ClassRoomEntity savedEntity = classRoomRepository.save(updatedEntity);
        log.info("ClassRoom updated successfully with ID: {}", savedEntity.getClassRoomId());
        return ClassRoomEntityDTOMapper.map(savedEntity);
    }
}