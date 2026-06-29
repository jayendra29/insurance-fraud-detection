package com.insurance.backend.dashboard.dto;

import java.math.BigDecimal;

public record UserDashboardResponse(
        long totalClaims,
        long submittedClaims,
        long underReviewClaims,
        long approvedClaims,
        long rejectedClaims,
        long fraudSuspectedClaims,
        BigDecimal totalClaimAmount
) {
}