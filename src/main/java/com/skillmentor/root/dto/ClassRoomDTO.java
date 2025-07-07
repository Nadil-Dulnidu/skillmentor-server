package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for ClassRoom", title = "ClassRoomDTO", type = "object")
public class ClassRoomDTO {
    @JsonProperty("class_room_id")
    @Schema(description = "Unique identifier for the classroom", example = "1")
    private Integer classRoomId;

    @NotBlank(message = "Title must not be blank")
    @JsonProperty("title")
    @Schema(description = "Title of the classroom", example = "Introduction to Java Programming")
    private String title;

    @JsonProperty("enrolled_student_count")
    @Schema(description = "Number of students enrolled in the classroom", example = "25")
    private Integer enrolledStudentCount;

    @NotNull(message = "class_image must not be null")
    @JsonProperty("class_image")
    @Schema(description = "Image URL for the classroom", example = "https://example.com/classroom.jpg")
    private String classImage;

    @JsonProperty("mentor")
    @Schema(description = "Mentor associated with the classroom")
    private MentorDTO mentorDTO;
}
