package com.skillmentor.root.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillmentor.root.dto.StudentDTO;
import com.skillmentor.root.exception.ClerkException;
import com.skillmentor.root.service.StudentService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/clerk")
public class ClerkWebhookController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final StudentService studentService;

    ClerkWebhookController(StudentService studentService) {
        this.studentService = studentService;
    }
    @PostMapping("/user-created")
    public ResponseEntity<String> createUser(@RequestBody final String rawBody) throws ClerkException{
        try {
            final Map<String, Object> body = objectMapper.readValue(rawBody, Map.class);
            final Map<String, Object> data = (Map<String, Object>) body.get("data");
            final JsonNode root = objectMapper.readTree(rawBody);
            if (data == null || data.get("id") == null) {
                return ResponseEntity.badRequest().body("Missing user ID");
            }
            final String userId = data.get("id").toString();
            final JsonNode emailNode = root.path("data")
                    .path("email_addresses")
                    .get(0).path("email_address");
            final StudentDTO studentDTO = new StudentDTO(
                    userId,
                    data.get("first_name").toString(),
                    data.get("last_name").toString(),
                    emailNode.asText(),
                    "-",
                    "-",
                    20
                    );
            final StudentDTO savedStudent = studentService.createStudent(studentDTO);
            final boolean isRoleAssign  = studentService.assignStudentRole(userId);
            if(isRoleAssign){
                return ResponseEntity.ok().body("User saved & role assign Successfully");
            }
            return ResponseEntity.ok().body("User saved Successfully");
        } catch (Exception e) {
            throw new ClerkException(e.getMessage());
        }
    }
}
