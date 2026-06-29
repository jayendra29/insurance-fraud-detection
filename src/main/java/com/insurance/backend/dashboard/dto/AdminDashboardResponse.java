package com.insurance.backend.dashboard.dto;

public record AdminDashboardResponse(
        long totalUsers,
        long totalClaims,
        long submittedClaims,
        long underReviewClaims,
        long approvedClaims,
        long rejectedClaims,
        long fraudSuspectedClaims,
        long highRiskClaims,
        long totalDocumentsUploaded
) {
}