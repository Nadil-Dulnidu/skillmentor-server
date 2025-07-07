package com.skillmentor.root.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
        description = "Data Transfer Object for Payment",
        title = "PaymentDTO",
        requiredProperties = {"mentorId", "mentorName", "totalFee"})
public class PaymentDTO {
    @JsonProperty("mentor_id")
    @Schema(description = "Unique identifier for the mentor", example = "1")
    private Integer mentorId;

    @NotBlank(message = "Mentor name must not be blank")
    @JsonProperty("mentor_name")
    @Schema(description = "Name of the mentor", example = "John Doe")
    private String mentorName;

    @NotNull(message = "Total fee must not be null")
    @Min(value = 0, message = "Total fee must be zero or positive")
    @JsonProperty("total_fee")
    @Schema(description = "Total fee for the payment", example = "150.00")
    private Double totalFee;
}