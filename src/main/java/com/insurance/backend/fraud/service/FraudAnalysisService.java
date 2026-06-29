package com.insurance.backend.fraud.service;

import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.repository.ClaimRepository;
import com.insurance.backend.document.repository.DocumentRepository;
import com.insurance.backend.exception.ResourceNotFoundException;
import com.insurance.backend.fraud.dto.FraudResponse;
import com.insurance.backend.fraud.entity.FraudAnalysis;
import com.insurance.backend.fraud.enums.RiskLevel;
import com.insurance.backend.fraud.repository.FraudAnalysisRepository;
import com.insurance.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudAnalysisService {

    private static final BigDecimal HIGH_AMOUNT_THRESHOLD = BigDecimal.valueOf(100000);
    private static final BigDecimal VERY_HIGH_AMOUNT_THRESHOLD = BigDecimal.valueOf(500000);
    private static final int DOCUMENT_COUNT_THRESHOLD = 5;

    private static final int LOW_RISK_MAX = 29;
    private static final int MEDIUM_RISK_MAX = 59;

    private final ClaimRepository claimRepository;
    private final DocumentRepository documentRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;

    @Transactional
    public FraudResponse analyzeClaim(Long claimId) {
        Claim claim = findClaimOwnedByCurrentUser(claimId);

        List<String> reasons = new ArrayList<>();
        int score = 0;

        if (claim.getClaimAmount().compareTo(HIGH_AMOUNT_THRESHOLD) > 0) {
            score += 40;
            reasons.add("Claim amount exceeds 100,000 (+40)");
        }

        if (claim.getIncidentDate() != null && claim.getIncidentDate().isEqual(LocalDate.now())) {
            score += 20;
            reasons.add("Incident date is today (+20)");
        }

        int documentCount = documentRepository.findByClaimIdOrderByUploadedAtDesc(claimId).size();
        if (documentCount > DOCUMENT_COUNT_THRESHOLD) {
            score += 20;
            reasons.add("More than 5 documents uploaded (+20)");
        }

        if (claim.getClaimAmount().compareTo(VERY_HIGH_AMOUNT_THRESHOLD) > 0) {
            score += 20;
            reasons.add("Claim amount exceeds 500,000 (+20)");
        }

        RiskLevel riskLevel = determineRiskLevel(score);
        String analysisReason = reasons.isEmpty()
                ? "No fraud indicators triggered"
                : String.join("; ", reasons);

        FraudAnalysis fraudAnalysis = fraudAnalysisRepository.findByClaimId(claimId)
                .orElseGet(() -> FraudAnalysis.builder().claim(claim).build());

        fraudAnalysis.setFraudScore(score);
        fraudAnalysis.setRiskLevel(riskLevel);
        fraudAnalysis.setAnalysisReason(analysisReason);
        fraudAnalysis.setAnalyzedAt(LocalDateTime.now());

        FraudAnalysis savedAnalysis = fraudAnalysisRepository.save(fraudAnalysis);
        return FraudResponse.fromEntity(savedAnalysis);
    }

    @Transactional(readOnly = true)
    public FraudResponse getFraudAnalysis(Long claimId) {
        findClaimOwnedByCurrentUser(claimId);

        FraudAnalysis fraudAnalysis = fraudAnalysisRepository.findByClaimId(claimId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Fraud analysis not found for claim id: " + claimId));

        return FraudResponse.fromEntity(fraudAnalysis);
    }

    private RiskLevel determineRiskLevel(int score) {
        if (score <= LOW_RISK_MAX) {
            return RiskLevel.LOW;
        } else if (score <= MEDIUM_RISK_MAX) {
            return RiskLevel.MEDIUM;
        } else {
            return RiskLevel.HIGH;
        }
    }

    private Claim findClaimOwnedByCurrentUser(Long claimId) {
        User currentUser = getAuthenticatedUser();

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        if (!claim.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this claim");
        }

        return claim;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}