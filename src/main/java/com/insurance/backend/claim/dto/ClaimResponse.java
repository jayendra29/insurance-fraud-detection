package com.insurance.backend.claim.dto;

import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.enums.ClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClaimResponse(
        Long id,
        String title,
        String description,
        BigDecimal claimAmount,
        LocalDate incidentDate,
        ClaimStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ClaimResponse fromEntity(Claim claim) {
        return new ClaimResponse(
                claim.getId(),
                claim.getTitle(),
                claim.getDescription(),
                claim.getClaimAmount(),
                claim.getIncidentDate(),
                claim.getStatus(),
                claim.getCreatedAt(),
                claim.getUpdatedAt()
        );
    }
}