package com.insurance.backend.claim.service;

import com.insurance.backend.claim.dto.ClaimResponse;
import com.insurance.backend.claim.dto.CreateClaimRequest;
import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.enums.ClaimStatus;
import com.insurance.backend.claim.repository.ClaimRepository;
import com.insurance.backend.claim.specification.ClaimSpecification;
import com.insurance.backend.exception.ResourceNotFoundException;
import com.insurance.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClaimService {

    private final ClaimRepository claimRepository;

    @Transactional
    public ClaimResponse createClaim(CreateClaimRequest request) {
        User currentUser = getAuthenticatedUser();

        Claim claim = Claim.builder()
                .title(request.title())
                .description(request.description())
                .claimAmount(request.claimAmount())
                .incidentDate(request.incidentDate())
                .status(ClaimStatus.SUBMITTED)
                .user(currentUser)
                .build();

        Claim savedClaim = claimRepository.save(claim);
        return ClaimResponse.fromEntity(savedClaim);
    }

    @Transactional(readOnly = true)
    public Page<ClaimResponse> getClaimsForCurrentUser(Pageable pageable) {
        return searchMyClaims(null, null, null, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClaimResponse> searchMyClaims(ClaimStatus status, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        User currentUser = getAuthenticatedUser();

        Specification<Claim> specification = Specification.where(ClaimSpecification.belongsToUser(currentUser.getId()))
                .and(ClaimSpecification.hasStatus(status))
                .and(ClaimSpecification.incidentDateFrom(fromDate))
                .and(ClaimSpecification.incidentDateTo(toDate));

        return claimRepository.findAll(specification, pageable)
                .map(ClaimResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public ClaimResponse getClaimById(Long claimId) {
        Claim claim = findClaimOwnedByCurrentUser(claimId);
        return ClaimResponse.fromEntity(claim);
    }

    @Transactional
    public void deleteClaim(Long claimId) {
        Claim claim = findClaimOwnedByCurrentUser(claimId);
        claimRepository.delete(claim);
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