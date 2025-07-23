package com.skillmentor.root.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmentor.root.dto.StudentDTO;
import com.skillmentor.root.entity.StudentEntity;
import com.skillmentor.root.exception.StudentException;
import com.skillmentor.root.mapper.StudentEntityDTOMapper;
import com.skillmentor.root.repository.StudentRepository;
import com.skillmentor.root.service.StudentService;
import com.skillmentor.root.util.InterServiceCommunicationHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final InterServiceCommunicationHandler interServiceCommunicationHandler;

    @Autowired
    public StudentServiceImpl(StudentRepository studentRepository,
                              InterServiceCommunicationHandler interServiceCommunicationHandler) {
        this.studentRepository = studentRepository;
        this.interServiceCommunicationHandler = interServiceCommunicationHandler;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentDTO createStudent(final StudentDTO studentDTO) {
        log.info("Creating new student...");
        if (studentDTO == null) {
            log.error("Failed to create student: input DTO is null.");
            throw new IllegalArgumentException("Student data must not be null.");
        }
        try {
            Optional<StudentEntity> existingStudent = studentRepository.findByClerkStudentId(studentDTO.getClerkStudentId());
            if (existingStudent.isPresent()) {
                log.info("Student already exists with clerk ID: {}", studentDTO.getClerkStudentId());
                return StudentEntityDTOMapper.map(existingStudent.get());
            }

            final StudentEntity studentEntity = StudentEntityDTOMapper.map(studentDTO);
            final StudentEntity savedEntity = studentRepository.save(studentEntity);
            log.info("Student created with ID: {}", savedEntity.getStudentId());
            return StudentEntityDTOMapper.map(savedEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating student: {}", e.getMessage());
            return studentRepository.findByClerkStudentId(studentDTO.getClerkStudentId())
                    .map(StudentEntityDTOMapper::map)
                    .orElseThrow(() -> new StudentException("Failed to create student due to data integrity violation"));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StudentDTO> getAllStudents(final List<String> addresses, final List<Integer> ages, final List<String> firstNames) {
        log.info("Fetching all students with filters: addresses={}, ages={}, firstNames={}", addresses, ages, firstNames);
        final List<StudentEntity> studentEntities = studentRepository.findAll();
        List<StudentDTO> result = studentEntities
                .stream()
                .filter(student -> addresses == null || addresses.contains(student.getAddress()))
                .filter(student -> ages == null || ages.contains(student.getAge()))
                .filter(student -> firstNames == null || firstNames.contains(student.getFirstName()))
                .map(StudentEntityDTOMapper::map)
                .toList();
        log.info("Found {} students after filtering ", result.size());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentDTO findStudentById(final Integer id) {
        log.info("Fetching student by ID: {}", id);
        return studentRepository.findById(id)
                .map(student -> {
                    log.debug("Student found: {}", student);
                    return StudentEntityDTOMapper.map(student);
                })
                .orElseThrow(() -> {
                    log.error("Student not found with ID: {} ", id);
                    return new StudentException("Student not found with ID: " + id);
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentDTO updateStudentById(final StudentDTO studentDTO) {
        log.info("Updating student...");
        if (studentDTO == null || studentDTO.getStudentId() == null) {
            log.error("Failed to update student: DTO or studentId is null.");
            throw new IllegalArgumentException("Student ID must not be null for update.");
        }
        log.debug("Updating student with ID: {}", studentDTO.getStudentId());
        final StudentEntity studentEntity = studentRepository.findById(studentDTO.getStudentId())
                .orElseThrow(() -> {
                    log.error("Cannot update. Student not found with ID: {}", studentDTO.getStudentId());
                    return new StudentException("Cannot update. Student not found with ID: " + studentDTO.getStudentId());
                });
        studentEntity.setFirstName(studentDTO.getFirstName());
        studentEntity.setLastName(studentDTO.getLastName());
        studentEntity.setEmail(studentDTO.getEmail());
        studentEntity.setPhoneNumber(studentDTO.getPhoneNumber());
        studentEntity.setAddress(studentDTO.getAddress());
        studentEntity.setAge(studentDTO.getAge());
        StudentEntity updated = studentRepository.save(studentEntity);
        log.info("Student updated with ID: {}", updated.getStudentId());
        return StudentEntityDTOMapper.map(updated);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentDTO deleteStudentById(final Integer id) {
        log.info("Deleting student with ID: {}", id);
        final StudentEntity studentEntity = studentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cannot delete. Student not found with ID: {}", id);
                    return new StudentException("Cannot delete. Student not found with ID: " + id);
                });
        studentRepository.delete(studentEntity);
        log.info("Student with ID {} deleted successfully", id);
        return StudentEntityDTOMapper.map(studentEntity);
    }

    @Override
    public StudentDTO findStudentByClerkId(String clerkId) {
        log.info("Fetching student by clerk ID: {}", clerkId);
        return studentRepository.findByClerkStudentId(clerkId)
                .map(StudentEntityDTOMapper::map)
                .orElseThrow(() -> {
                    log.error("Student not found with clerk ID: {}", clerkId);
                    return new StudentException("Student not found with clerk ID: " + clerkId);
                });
    }

    @Override
    public StudentDTO deleteStudentByClerkId(String clerkId) throws StudentException {
        log.info("Deleting student with clerk ID: {}", clerkId);
        final StudentEntity studentEntity = studentRepository.findByClerkStudentId(clerkId)
                .orElseThrow(() -> {
                    log.error("Cannot delete. Student not found with clerk ID: {}", clerkId);
                    return new StudentException("Cannot delete. Student not found with clerk ID: " + clerkId);
                });
        studentRepository.delete(studentEntity);
        log.info("Student with clerk ID {} deleted successfully", clerkId);
        return StudentEntityDTOMapper.map(studentEntity);
    }

    @Override
    public boolean assignStudentRole(final String userId) throws Exception {
        log.info("Assigning student role to user with ID: {}", userId);
        final String userResponse = interServiceCommunicationHandler.getUserId(userId);
        log.debug("Fetched user info from clerk");
        final Map<String, Object> userMap = objectMapper.readValue(userResponse, Map.class);
        Map<String, Object> publicMetadata = (Map<String, Object>) userMap.get("public_metadata");
        if (publicMetadata == null) {
            publicMetadata = new HashMap<>();
        }
        final String currentRole = (String) publicMetadata.get("role");
        if (currentRole != null){
            log.info("Role not assign: User has already a role {}", userId);
            return false;
        }
        publicMetadata.put("role", "STUDENT");
        final Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("public_metadata", publicMetadata);
        log.debug("Updating student role");
        final String patchResponse = interServiceCommunicationHandler.updateUserMetaData(updatePayload,userId);
        log.info("Student role assigned successfully for user with ID: {}", userId);
        return true;
    }
}
