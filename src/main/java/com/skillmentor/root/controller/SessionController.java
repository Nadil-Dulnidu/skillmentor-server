package com.skillmentor.root.controller;

import com.skillmentor.root.common.Constants;
import com.skillmentor.root.dto.SessionDTO;
import com.skillmentor.root.dto.SessionLiteDTO;
import com.skillmentor.root.dto.StudentDTO;
import com.skillmentor.root.exception.SessionException;
import com.skillmentor.root.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/academic")
@Tag(name = "Session Management", description = "Endpoints for creating and retrieving academic sessions")
@Validated
public class SessionController {
    private final SessionService sessionService;

    @Autowired
    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(
            summary = "Create a new session",
            description = "Creates a new academic session with details about class, mentor, and time"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid session data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @PostMapping(value = "/session", consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<SessionLiteDTO> createSession(
            @Parameter(description = "Session data to create", required = true)
            @Valid @RequestBody SessionLiteDTO sessionDTO) {
        final SessionLiteDTO savedDTO = sessionService.createSession(sessionDTO);
        return new ResponseEntity<>(savedDTO, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all sessions",
            description = "Retrieves all academic sessions with extended student, mentor, and class data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No sessions found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @GetMapping(value = "/session", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<List<SessionDTO>> getAllSessions() {
        final List<SessionDTO> sessionDTOS = sessionService.getAllSessions();
        return new ResponseEntity<>(sessionDTOS, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all sessions for a student",
            description = "Retrieves all academic sessions for a specific student"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student sessions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No sessions found for the student"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid student data"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @GetMapping(value = "/session/student/{clerkId}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<List<SessionDTO>> getAllStudentSessions(
            @Parameter(description = "Clerk ID of the student", required = true)
            @PathVariable @NotNull final String clerkId) {
        final List<SessionDTO> sessionDTOS = sessionService.getAllStudentSessions(clerkId);
        return new ResponseEntity<>(sessionDTOS, HttpStatus.OK);
    }

    @Operation(
            summary = "Update session status",
            description = "Updates the status of an existing session"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "400", description = "Bad request - Invalid session data"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @PutMapping(value = "/session/{sessionId}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<SessionDTO> updateSessionStatus(
            @Parameter(description = "ID of the session to update", required = true)
            @PathVariable @NotNull Integer sessionId,
            @Parameter(description = "New status for the session", required = true)
            @RequestParam @NotNull Constants.SessionStatus sessionStatus) {
        final SessionDTO updatedSession = sessionService.updateSessionStatus(sessionId, sessionStatus);
        return new ResponseEntity<>(updatedSession, HttpStatus.OK);
    }
}
