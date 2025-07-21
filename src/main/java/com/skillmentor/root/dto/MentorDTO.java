package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(
        description = "Data Transfer Object for Mentor",
        title = "MentorDTO",
        requiredProperties = {
                "mentorId", "clerkMentorId", "firstName", "lastName", "address",
                "email", "title", "sessionFee", "profession", "subject",
                "phoneNumber", "qualification", "mentorImage", "classRoomId"
        })
public class MentorDTO {
    @JsonProperty("mentor_id")
    @Schema(description = "Unique identifier for the mentor", example = "1")
    private Integer mentorId;

    @JsonProperty("clerk_mentor_id")
    @NotBlank(message = "Clerk mentor ID must not be blank")
    @Schema(description = "Unique identifier for the mentor in the clerk system", example = "mentor123")
    private String clerkMentorId;

    @NotBlank(message = "First name must not be blank")
    @JsonProperty("first_name")
    @Schema(description = "First name of the mentor", example = "John")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @JsonProperty("last_name")
    @Schema(description = "Last name of the mentor", example = "Doe")
    private String lastName;

    @NotBlank(message = "Address must not be blank")
    @JsonProperty("address")
    @Schema(description = "Address of the mentor", example = "123 Main St, Springfield")
    private String address;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email must not be blank")
    @JsonProperty("email")
    @Schema(description = "Email address of the mentor", example = "johndoe@gmail.com")
    private String email;

    @NotBlank(message = "Title must not be blank")
    @JsonProperty("title")
    @Schema(description = "Title of the mentor", example = "Senior Mentor")
    private String title;

    @NotNull(message = "Session fee must not be null")
    @Min(value = 0, message = "Session fee must be zero or positive")
    @JsonProperty("session_fee")
    @Schema(description = "Fee charged by the mentor for a session", example = "50.0")
    private Double sessionFee;

    @NotBlank(message = "Profession must not be blank")
    @JsonProperty("profession")
    @Schema(description = "Profession of the mentor", example = "Software Engineer")
    private String profession;

    @NotBlank(message = "Subject must not be blank")
    @JsonProperty("subject")
    @Schema(description = "Subject expertise of the mentor", example = "Computer Science")
    private String subject;

    @NotBlank(message = "Phone number must not be blank")
    @JsonProperty("phone_number")
    @Schema(description = "Phone number of the mentor", example = "+1234567890")
    private String phoneNumber;

    @NotBlank(message = "Qualification must not be blank")
    @JsonProperty("qualification")
    @Schema(description = "Qualification of the mentor", example = "PhD in Computer Science")
    private String qualification;

    @NotBlank(message = "mentor_image must not be null")
    @JsonProperty("mentor_image")
    @Schema(description = "Image URL for the mentor", example = "https://example.com/mentor.jpg")
    private String mentorImage;

    @JsonProperty("class_room_id")
    @Schema(description = "ID of the classroom associated with the mentor", example = "101")
    private Integer classRoomId;
}