package com.insurance.backend.admin.dto;

import com.insurance.backend.claim.enums.ClaimStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateClaimStatusRequest(

        @NotNull(message = "Status is required")
        ClaimStatus status
) {
}