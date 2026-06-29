package com.insurance.backend.admin.dto;

import com.insurance.backend.claim.dto.ClaimResponse;
import com.insurance.backend.document.dto.DocumentResponse;
import com.insurance.backend.fraud.dto.FraudResponse;

import java.util.List;

public record AdminClaimResponse(
        ClaimResponse claim,
        List<DocumentResponse> documents,
        FraudResponse fraudAnalysis
) {
}