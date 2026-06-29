package com.insurance.backend.admin.service;

import com.insurance.backend.admin.dto.AdminClaimResponse;
import com.insurance.backend.admin.dto.UpdateClaimStatusRequest;
import com.insurance.backend.claim.dto.ClaimResponse;
import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.repository.ClaimRepository;
import com.insurance.backend.document.dto.DocumentResponse;
import com.insurance.backend.document.repository.DocumentRepository;
import com.insurance.backend.exception.ResourceNotFoundException;
import com.insurance.backend.fraud.dto.FraudResponse;
import com.insurance.backend.fraud.enums.RiskLevel;
import com.insurance.backend.fraud.repository.FraudAnalysisRepository;
import com.insurance.backend.user.entity.Role;
import com.insurance.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ClaimRepository claimRepository;
    private final DocumentRepository documentRepository;
    private final FraudAnalysisRepository fraudAnalysisRepository;

    @Transactional(readOnly = true)
    public List<ClaimResponse> getAllClaims() {
        verifyAdminAccess();
        return claimRepository.findAll()
                .stream()
                .map(ClaimResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminClaimResponse getClaimDetails(Long claimId) {
        verifyAdminAccess();

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        List<DocumentResponse> documents = documentRepository.findByClaimIdOrderByUploadedAtDesc(claimId)
                .stream()
                .map(DocumentResponse::fromEntity)
                .toList();

        FraudResponse fraudResponse = fraudAnalysisRepository.findByClaimId(claimId)
                .map(FraudResponse::fromEntity)
                .orElse(null);

        return new AdminClaimResponse(
                ClaimResponse.fromEntity(claim),
                documents,
                fraudResponse
        );
    }

    @Transactional
    public ClaimResponse updateClaimStatus(Long claimId, UpdateClaimStatusRequest request) {
        verifyAdminAccess();

        Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + claimId));

        claim.setStatus(request.status());
        Claim updatedClaim = claimRepository.save(claim);

        return ClaimResponse.fromEntity(updatedClaim);
    }

    @Transactional(readOnly = true)
    public List<FraudResponse> getHighRiskFraudAnalyses() {
        verifyAdminAccess();

        return fraudAnalysisRepository.findAll()
                .stream()
                .filter(fraudAnalysis -> fraudAnalysis.getRiskLevel() == RiskLevel.HIGH)
                .map(FraudResponse::fromEntity)
                .toList();
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