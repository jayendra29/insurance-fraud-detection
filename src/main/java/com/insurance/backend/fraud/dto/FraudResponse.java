package com.insurance.backend.fraud.dto;

import com.insurance.backend.fraud.entity.FraudAnalysis;
import com.insurance.backend.fraud.enums.RiskLevel;

import java.time.LocalDateTime;

public record FraudResponse(
        Long id,
        Long claimId,
        Integer fraudScore,
        RiskLevel riskLevel,
        String analysisReason,
        LocalDateTime analyzedAt
) {

    public static FraudResponse fromEntity(FraudAnalysis fraudAnalysis) {
        return new FraudResponse(
                fraudAnalysis.getId(),
                fraudAnalysis.getClaim().getId(),
                fraudAnalysis.getFraudScore(),
                fraudAnalysis.getRiskLevel(),
                fraudAnalysis.getAnalysisReason(),
                fraudAnalysis.getAnalyzedAt()
        );
    }
}