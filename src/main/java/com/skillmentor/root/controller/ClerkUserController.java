package com.skillmentor.root.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmentor.root.dto.StudentDTO;
import com.skillmentor.root.exception.ClerkException;
import com.skillmentor.root.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/clerk")
@Tag(name = "Clerk Webhook", description = "Handles user creation and updates from Clerk webhooks")
public class ClerkUserController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StudentService studentService;

    ClerkUserController(StudentService studentService) {
        this.studentService = studentService;
    }
    @Operation(
            summary = "Handle Clerk user webhook",
            description = "Handles `user.created` and `user.updated` events from Clerk and maps to internal StudentDTO logic.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User processed successfully (created or updated)"),
                    @ApiResponse(responseCode = "400", description = "Bad request or missing fields"),
                    @ApiResponse(responseCode = "500", description = "Internal server error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden")
            }
    )
    @PostMapping("/user")
    public ResponseEntity<?> handleClerkUser(
            @Parameter()
            @RequestBody final String rawBody) throws ClerkException{
        try {
            final Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
            final Map<String, Object> data = (Map<String, Object>) body.get("data");
            final JsonNode root = objectMapper.readTree(rawBody);
            if (data == null || data.get("id") == null) {
                throw new IllegalArgumentException("Invalid data: Missing userId");
            }
            final String userId = data.get("id").toString();
            final JsonNode emailNode = root.path("data")
                    .path("email_addresses")
                    .get(0).path("email_address");

            if(body.get("type").equals("user.updated")){
                final StudentDTO studentDTO = new StudentDTO(
                        userId,
                        data.get("first_name").toString(),
                        data.get("last_name").toString(),
                        emailNode.asText(),
                        "-",
                        "-",
                        20
                );
                final StudentDTO updatedStudent = studentService.updateStudentById(studentDTO);
                return new ResponseEntity<>(updatedStudent, HttpStatus.OK);
            }
            final StudentDTO studentDTO = new StudentDTO(
                    userId,
                    Objects.nonNull(data.get("first_name").toString()) ? data.get("first_name").toString() : "-",
                    Objects.nonNull(data.get("last_name").toString()) ? data.get("last_name").toString() : "-",
                    emailNode.asText(),
                    "-",
                    "-",
                    20
            );
            final StudentDTO savedStudent = studentService.createStudent(studentDTO);
            studentService.assignStudentRole(userId);
            return new ResponseEntity<>(savedStudent, HttpStatus.OK);
        } catch (Exception e) {
            throw new ClerkException(e.getMessage());
        }
    }
}
