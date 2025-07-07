package com.skillmentor.root.service.impl;

import com.skillmentor.root.common.Constants;
import com.skillmentor.root.dto.*;
import com.skillmentor.root.entity.ClassRoomEntity;
import com.skillmentor.root.entity.LiteSessionEntity;
import com.skillmentor.root.entity.SessionEntity;
import com.skillmentor.root.exception.ClassRoomException;
import com.skillmentor.root.mapper.AuditDTOEntityMapper;
import com.skillmentor.root.mapper.LiteSessionEntityDTOMapper;
import com.skillmentor.root.mapper.SessionDTOEntityMapper;
import com.skillmentor.root.repository.ClassRoomRepository;
import com.skillmentor.root.repository.LiteSessionRepository;
import com.skillmentor.root.repository.SessionRepository;
import com.skillmentor.root.service.SessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final LiteSessionRepository liteSessionRepository;
    private final ClassRoomRepository classRoomRepository;

    @Autowired
    public SessionServiceImpl(SessionRepository sessionRepository,
                              LiteSessionRepository liteSessionRepository,
                              ClassRoomRepository classRoomRepository) {
        this.sessionRepository = sessionRepository;
        this.liteSessionRepository = liteSessionRepository;
        this.classRoomRepository = classRoomRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionLiteDTO createSession(final SessionLiteDTO sessionDTO) {
        log.info("Creating new session with details: {}", sessionDTO);
        if (sessionDTO == null) {
            log.error("Failed to create session: input DTO is null.");
            throw new IllegalArgumentException("Session data must not be null.");
        }
        log.debug("Mapping SessionLiteDTO to LiteSessionEntity: {}", sessionDTO);
        LiteSessionEntity sessionEntity = LiteSessionEntityDTOMapper.map(sessionDTO);
        log.debug("Saving LiteSessionEntity: {}", sessionEntity);
        LiteSessionEntity savedEntity = liteSessionRepository.save(sessionEntity);
        log.debug("Updating enrolled student count for classroom ID: {}", sessionDTO.getClassRoomId());
        updateEnrollStudentCount(sessionDTO);
        log.info("Session created with ID: {} and classroom ID: {}", savedEntity.getSessionId(), savedEntity.getClassRoomId());
        return LiteSessionEntityDTOMapper.map(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDTO> getAllSessions() {
        log.info("Retrieving all sessions with extended details...");
        List<SessionEntity> sessions = sessionRepository.findAll();
        return sessions
                .stream()
                .map(SessionDTOEntityMapper::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditDTO> getAllAudits() {
        log.info("Retrieving all session audits...");
        List<SessionEntity> sessions = sessionRepository.findAll();
        return sessions
                .stream()
                .map(AuditDTOEntityMapper::map)
                .toList();
    }

    @Override
    public List<PaymentDTO> findMentorPayments(final String startDate, final String endDate) {
        log.info("Finding mentor payments from {} to {}", startDate, endDate);
        if (startDate == null || endDate == null) {
            log.error("Start date and end date must not be null.");
            throw new IllegalArgumentException("Start date and end date must not be null.");
        }
        final List<Object> rawResults = sessionRepository.findMentorPayments(startDate, endDate);
        if (rawResults == null || rawResults.isEmpty()) {
            log.warn("No mentor payments found for the given date range: {} to {}", startDate, endDate);
            return Collections.emptyList();
        }
        log.debug("Raw results retrieved: {}", rawResults.size());
        final List<PaymentDTO> list = rawResults.stream().map(obj -> {
            Object[] row = (Object[]) obj;
            Integer mentorId = (Integer) row[0];
            String mentorName = (String) row[1];
            Double totalFee = (Double) row[2];
            return new PaymentDTO(mentorId, mentorName, totalFee);
        }).toList();
        log.info("Mentor payments found: {}", list.size());
        return list;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SessionDTO> getAllStudentSessions(final String studentClerkId) {
        log.info("Retrieving all sessions for student with clerk ID: {}", studentClerkId);
        if (Objects.isNull(studentClerkId)) {
            log.error("Student clerk ID must not be null or empty.");
            throw new IllegalArgumentException("Student clerk ID must not be null or empty.");
        }
        log.debug("Fetching sessions for student clerk ID: {}", studentClerkId);
        List<SessionEntity> sessions = sessionRepository.findAll();
        return sessions
                .stream()
                .filter(session -> session.getStudentEntity().getClerkStudentId().equals(studentClerkId))
                .map(SessionDTOEntityMapper::map)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SessionDTO updateSessionStatus(final Integer sessionId, final Constants.SessionStatus sessionStatus) {
        log.info("Updating session status for session ID: {} to {}", sessionId, sessionStatus);
        if (sessionId == null || sessionStatus == null) {
            log.error("Session ID and status must not be null.");
            throw new IllegalArgumentException("Session ID and status must not be null.");
        }
        final Optional<SessionEntity> optionalSession = sessionRepository.findById(sessionId);
        if (optionalSession.isEmpty()) {
            log.error("Session with ID {} not found.", sessionId);
            throw new IllegalArgumentException("Session with ID " + sessionId + " not found.");
        }
        final SessionEntity sessionEntity = optionalSession.get();
        sessionEntity.setSessionStatus(sessionStatus);
        log.debug("Session status updated to: {}", sessionStatus);
        final SessionEntity updatedEntity = sessionRepository.save(sessionEntity);
        log.info("Session with ID {} updated successfully.", sessionId);
        return SessionDTOEntityMapper.map(updatedEntity);
    }

    //Helper method to update classroom student enrolment count
    public void updateEnrollStudentCount(final SessionLiteDTO sessionLiteDTO) {
        log.info("Updating enrolled student count for classroom ID: {}", sessionLiteDTO.getClassRoomId());
        if (sessionLiteDTO.getClassRoomId() == null) {
            log.error("Classroom ID must not be null.");
            throw new IllegalArgumentException("Classroom ID must not be null.");
        }
        ClassRoomEntity classRoomEntity = classRoomRepository.findById(sessionLiteDTO.getClassRoomId())
                .orElseThrow(() -> {
                    log.error("Classroom not found with ID: {}", sessionLiteDTO.getClassRoomId());
                            return new ClassRoomException("Classroom not found with ID: " +
                                    sessionLiteDTO.getClassRoomId());
                        });
        classRoomEntity.setEnrolledStudentCount(classRoomEntity.getEnrolledStudentCount() + 1);
        classRoomRepository.save(classRoomEntity);
        log.info("Enrolled student count updated for classroom ID: {}", sessionLiteDTO.getClassRoomId());
    }
}
