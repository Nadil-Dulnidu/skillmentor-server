package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Data Transfer Object for Student")
public class StudentDTO {
    @JsonProperty("student_id")
    @Schema(description = "Unique identifier for the student", example = "1")
    private Integer studentId;

    @NotBlank(message = "Clerk student ID must not be blank")
    @NonNull
    @JsonProperty("clerk_student_id")
    @Schema(description = "Unique identifier for the student in the system", example = "STU12345")
    private String clerkStudentId;

    @NotBlank(message = "First name must not be blank")
    @NonNull
    @JsonProperty("first_name")
    @Schema(description = "First name of the student", example = "John")
    private String firstName;

    @NotBlank(message = "Last name must not be blank")
    @NonNull
    @JsonProperty("last_name")
    @Schema(description = "Last name of the student", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email must not be blank")
    @NonNull
    @JsonProperty("email")
    @Schema(description = "Email address of the student", example = "johndoe@gmail.com")
    private String email;

    @JsonProperty("phone_number")
    @NonNull
    @Schema(description = "Phone number of the student", example = "+1234567890")
    private String phoneNumber;

    @JsonProperty("address")
    @NonNull
    @Schema(description = "Residential address of the student", example = "123 Main St, Springfield")
    private String address;

    @Min(value = 18, message = "Age must be at least 18")
    @NonNull
    @JsonProperty("age")
    @Schema(description = "Age of the student", example = "20")
    private Integer age;
}