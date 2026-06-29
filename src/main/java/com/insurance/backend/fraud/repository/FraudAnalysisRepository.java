package com.insurance.backend.fraud.repository;

import com.insurance.backend.fraud.entity.FraudAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FraudAnalysisRepository extends JpaRepository<FraudAnalysis, Long> {

    Optional<FraudAnalysis> findByClaimId(Long claimId);
}