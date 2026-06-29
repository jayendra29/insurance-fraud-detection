package com.insurance.backend.claim.repository;

import com.insurance.backend.claim.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long>, JpaSpecificationExecutor<Claim> {

    List<Claim> findByUserIdOrderByCreatedAtDesc(Long userId);
}