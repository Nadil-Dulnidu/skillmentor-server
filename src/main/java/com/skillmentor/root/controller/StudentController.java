package com.skillmentor.root.controller;

import com.skillmentor.root.common.Constants;
import com.skillmentor.root.dto.StudentDTO;
import com.skillmentor.root.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(value = "/academic")
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @Operation(summary = "Create a new student", description = "Accepts a student JSON and creates a new student record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid student data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @PostMapping(value = "/student", consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<StudentDTO> createStudent(
            @Parameter(description = "Student details to create", required = true)
            @RequestBody @Valid StudentDTO studentDTO) {
        final StudentDTO savedDTO = studentService.createStudent(studentDTO);
        log.info("Create Student......");
        return new ResponseEntity<>(savedDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get all students", description = "Retrieves all students with optional filters for address, age, and first name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No students found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_ROLE_PERMISSION)
    @GetMapping(value = "/student", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<List<StudentDTO>> getAllStudents(
            @Parameter(description = "Filter by address")
            @RequestParam(required = false) final List<String> addresses,
            @Parameter(description = "Filter by age")
            @RequestParam(required = false) final List<Integer> ages,
            @Parameter(description = "Filter by first name")
            @RequestParam(required = false) final List<String> firstNames
    ) {
        final List<StudentDTO> studentDTOS = studentService.getAllStudents(addresses, ages, firstNames);
        log.info("Get All Students......");
        return new ResponseEntity<>(studentDTOS, HttpStatus.OK);
    }

    @Operation(summary = "Update a student", description = "Updates an existing student based on the provided data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid student data"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @PutMapping(value = "/student", consumes = Constants.APPLICATION_JSON, produces = Constants.APPLICATION_JSON)
    public ResponseEntity<StudentDTO> updateStudent(
            @Parameter(description = "Student data to update", required = true)
            @RequestBody @Valid final StudentDTO studentDTO
    ) {
        final StudentDTO student = studentService.updateStudentById(studentDTO);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @Operation(summary = "Get student by Clerk ID", description = "Fetches a student by their unique Clerk ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid student ID"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @GetMapping(value = "/student/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<StudentDTO> findStudentById(
            @Parameter(description = "ID of the student to fetch", required = true)
            @PathVariable @NotNull String id
    ) {
        final StudentDTO student = studentService.findStudentByClerkId(id);
        log.info("Find Student id: {} from server......", id);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }

    @Operation(summary = "Delete a student by Clerk ID", description = "Deletes a student by their unique Clerk ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid student ID"),
            @ApiResponse(responseCode = "404", description = "Student not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize(Constants.ADMIN_OR_STUDENT_ROLE_PERMISSION)
    @DeleteMapping(value = "/student/{id}", produces = Constants.APPLICATION_JSON)
    public ResponseEntity<StudentDTO> deleteStudentByClerkId(
            @Parameter(description = "Clerk ID of the student to delete", required = true)
            @PathVariable @NotNull String id
    ) {
        final StudentDTO student = studentService.deleteStudentByClerkId(id);
        return new ResponseEntity<>(student, HttpStatus.OK);
    }
}
