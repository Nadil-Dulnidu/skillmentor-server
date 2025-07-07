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
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for Session Lite")
public class SessionLiteDTO {
    @JsonProperty("session_id")
    @Schema(description = "Unique identifier for the session", example = "1")
    private Integer sessionId;

    @NotNull(message = "Student ID must not be null")
    @JsonProperty("student_id")
    @Schema(description = "Unique identifier for the student", example = "123")
    private Integer studentId;

    @NotNull(message = "Classroom ID must not be null")
    @JsonProperty("class_room_id")
    @Schema(description = "Unique identifier for the classroom", example = "456")
    private Integer classRoomId;

    @NotNull(message = "Mentor ID must not be null")
    @JsonProperty("mentor_id")
    @Schema(description = "Unique identifier for the mentor", example = "789")
    private Integer mentorId;

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
    @Schema(description = "Topic of the session", example = "Introduction to Java")
    private String topic;
}