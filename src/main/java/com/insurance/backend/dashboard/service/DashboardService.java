package com.insurance.backend.dashboard.service;

import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.enums.ClaimStatus;
import com.insurance.backend.claim.repository.ClaimRepository;
import com.insurance.backend.dashboard.dto.AdminDashboardResponse;
import com.insurance.backend.dashboard.dto.UserDashboardResponse;
import com.insurance.backend.document.repository.DocumentRepository;
import com.insurance.backend.fraud.enums.RiskLevel;
import com.insurance.backend.fraud.repository.FraudAnalysisRepository;
import com.insurance.backend.user.entity.Role;
import com.insurance.backend.user.entity.User;
import com.insurance.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ClaimRepository claimRepository;
    private final DocumentRepository documentRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;

    @Transactional(readOnly = true)
    public UserDashboardResponse getUserDashboard() {
        User currentUser = getAuthenticatedUser();
        List<Claim> userClaims = claimRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());

        long totalClaims = userClaims.size();
        long submittedClaims = countByStatus(userClaims, ClaimStatus.SUBMITTED);
        long underReviewClaims = countByStatus(userClaims, ClaimStatus.UNDER_REVIEW);
        long approvedClaims = countByStatus(userClaims, ClaimStatus.APPROVED);
        long rejectedClaims = countByStatus(userClaims, ClaimStatus.REJECTED);
        long fraudSuspectedClaims = countByStatus(userClaims, ClaimStatus.FRAUD_SUSPECTED);

        BigDecimal totalClaimAmount = userClaims.stream()
                .map(Claim::getClaimAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new UserDashboardResponse(
                totalClaims,
                submittedClaims,
                underReviewClaims,
                approvedClaims,
                rejectedClaims,
                fraudSuspectedClaims,
                totalClaimAmount
        );
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse getAdminDashboard() {
        verifyAdminAccess();

        List<Claim> allClaims = claimRepository.findAll();

        long totalUsers = userRepository.count();
        long totalClaims = allClaims.size();
        long submittedClaims = countByStatus(allClaims, ClaimStatus.SUBMITTED);
        long underReviewClaims = countByStatus(allClaims, ClaimStatus.UNDER_REVIEW);
        long approvedClaims = countByStatus(allClaims, ClaimStatus.APPROVED);
        long rejectedClaims = countByStatus(allClaims, ClaimStatus.REJECTED);
        long fraudSuspectedClaims = countByStatus(allClaims, ClaimStatus.FRAUD_SUSPECTED);

        long highRiskClaims = fraudAnalysisRepository.findAll()
                .stream()
                .filter(fraudAnalysis -> fraudAnalysis.getRiskLevel() == RiskLevel.HIGH)
                .count();

        long totalDocumentsUploaded = documentRepository.count();

        return new AdminDashboardResponse(
                totalUsers,
                totalClaims,
                submittedClaims,
                underReviewClaims,
                approvedClaims,
                rejectedClaims,
                fraudSuspectedClaims,
                highRiskClaims,
                totalDocumentsUploaded
        );
    }

    private long countByStatus(List<Claim> claims, ClaimStatus status) {
        return claims.stream()
                .filter(claim -> claim.getStatus() == status)
                .count();
    }

    private void verifyAdminAccess() {
        User currentUser = getAuthenticatedUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only administrators can access this resource");
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}