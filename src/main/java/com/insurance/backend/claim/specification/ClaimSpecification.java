package com.insurance.backend.claim.specification;

import com.insurance.backend.claim.entity.Claim;
import com.insurance.backend.claim.enums.ClaimStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class ClaimSpecification {

    private ClaimSpecification() {
    }

    public static Specification<Claim> belongsToUser(Long userId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Claim> hasStatus(ClaimStatus status) {
        return (root, query, criteriaBuilder) ->
                status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Claim> incidentDateFrom(LocalDate fromDate) {
        return (root, query, criteriaBuilder) ->
                fromDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("incidentDate"), fromDate);
    }

    public static Specification<Claim> incidentDateTo(LocalDate toDate) {
        return (root, query, criteriaBuilder) ->
                toDate == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("incidentDate"), toDate);
    }
}