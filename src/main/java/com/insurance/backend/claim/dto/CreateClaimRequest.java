package com.insurance.backend.claim.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateClaimRequest(

        @NotBlank(message = "Title is required")
        @Size(max = 150, message = "Title must not exceed 150 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 2000, message = "Description must not exceed 2000 characters")
        String description,

        @NotNull(message = "Claim amount is required")
        @DecimalMin(value = "0.01", message = "Claim amount must be greater than zero")
        BigDecimal claimAmount,

        @NotNull(message = "Incident date is required")
        @PastOrPresent(message = "Incident date cannot be in the future")
        LocalDate incidentDate
) {
}