package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for Audit information", title = "AuditDTO",type = "object")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuditDTO {

    private Integer sessionId;
    @JsonProperty("student_id")
    @Schema(description = "Unique identifier for the student", example = "1")
    private Integer studentId;

    @NotBlank(message = "Student first name must not be blank")
    @JsonProperty("student_first_name")
    @Schema(description = "First name of the student", example = "John")
    private String studentFirstName;

    @NotBlank(message = "Student last name must not be blank")
    @JsonProperty("student_last_name")
    @Schema(description = "Last name of the student", example = "Doe")
    private String studentLastName;

    @NotBlank(message = "Student email must not be blank")
    @JsonProperty("student_email")
    @Schema(description = "Email address of the student", example = "johndoe@gmail.com")
    private String studentEmail;

    @NotBlank(message = "Student phone number must not be blank")
    @JsonProperty("student_phone_number")
    @Schema(description = "Phone number of the student", example = "+1234567890")
    private String studentPhoneNumber;

    @NotBlank(message = "Class title must not be blank")
    @JsonProperty("class_title")
    @Schema(description = "Title of the class", example = "Advanced Java Programming")
    private String classTitle;

    @NotNull(message = "Mentor ID must not be null")
    @JsonProperty("mentor_id")
    @Schema(description = "Unique identifier for the mentor", example = "2")
    private Integer mentorId;

    @NotBlank(message = "Mentor first name must not be blank")
    @JsonProperty("mentor_first_name")
    @Schema(description = "First name of the mentor", example = "Jane")
    private String mentorFirstName;

    @NotBlank(message = "Mentor last name must not be blank")
    @JsonProperty("mentor_last_name")
    @Schema(description = "Last name of the mentor", example = "Smith")
    private String mentorLastName;

    @NotBlank(message = "Mentor phone number must not be blank")
    @JsonProperty("mentor_phone_number")
    @Schema(description = "Phone number of the mentor", example = "+0987654321")
    private String mentorPhoneNumber;

    @NotNull(message = "Fee must not be null")
    @JsonProperty("fee")
    @Schema(description = "Fee charged for the session", example = "49.99")
    private Double fee;

    @NotNull(message = "Start time must not be null")
    @JsonProperty("start_time")
    @Schema(description = "Start time of the session", example = "2023-10-01T10:00:00Z")
    private Instant startTime;

    @NotNull(message = "End time must not be null")
    @JsonProperty("end_time")
    @Schema(description = "End time of the session", example = "2023-10-01T11:00:00Z")
    private Instant endTime;

    @NotBlank(message = "Topic must not be blank")
    @JsonProperty("topic")
    @Schema(description = "Topic of the session", example = "Introduction to Spring Boot")
    private String topic;
}
