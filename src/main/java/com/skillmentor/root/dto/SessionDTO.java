package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillmentor.root.common.Constants;
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
@Schema(description = "Data Transfer Object for Session")
public class SessionDTO {
    @JsonProperty("session_id")
    @Schema(description = "Unique identifier for the session", example = "1")
    private Integer sessionId;

    @NotNull(message = "Student must not be null")
    @JsonProperty("student")
    @Schema(description = "Details of the student participating in the session")
    private StudentDTO studentDTO;

    @NotNull(message = "Classroom must not be null")
    @JsonProperty("class_room")
    @Schema(description = "Details of the classroom where the session is held")
    private ClassRoomDTO classRoomDTO;

    @NotNull(message = "Mentor must not be null")
    @JsonProperty("mentor")
    @Schema(description = "Details of the mentor conducting the session")
    private MentorDTO mentorDTO;

    @NotBlank(message = "Topic must not be blank")
    @JsonProperty("topic")
    @Schema(description = "Topic of the session", example = "Introduction to Java")
    private String topic;

    @NotNull(message = "Start time must not be null")
    @JsonProperty("start_time")
    @Schema(description = "Start time of the session", example = "2023-10-01T10:00:00Z")
    private Instant startTime;

    @NotNull(message = "End time must not be null")
    @JsonProperty("end_time")
    @Schema(description = "End time of the session", example = "2023-10-01T11:00:00Z")
    private Instant endTime;

    @NotNull(message = "Session status must not be null")
    @JsonProperty("session_status")
    @Schema(description = "Current status of the session", example = "PENDING")
    private Constants.SessionStatus sessionStatus;
}
