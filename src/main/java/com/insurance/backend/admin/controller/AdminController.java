package com.insurance.backend.admin.controller;

import com.insurance.backend.admin.dto.AdminClaimResponse;
import com.insurance.backend.admin.dto.UpdateClaimStatusRequest;
import com.insurance.backend.admin.service.AdminService;
import com.insurance.backend.claim.dto.ClaimResponse;
import com.insurance.backend.fraud.dto.FraudResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/claims")
    public ResponseEntity<List<ClaimResponse>> getAllClaims() {
        List<ClaimResponse> claims = adminService.getAllClaims();
        return ResponseEntity.ok(claims);
    }

    @GetMapping("/claims/{id}")
    public ResponseEntity<AdminClaimResponse> getClaimDetails(@PathVariable Long id) {
        AdminClaimResponse response = adminService.getClaimDetails(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/claims/{id}/status")
    public ResponseEntity<ClaimResponse> updateClaimStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClaimStatusRequest request) {

        ClaimResponse response = adminService.updateClaimStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fraud/high-risk")
    public ResponseEntity<List<FraudResponse>> getHighRiskFraudAnalyses() {
        List<FraudResponse> response = adminService.getHighRiskFraudAnalyses();
        return ResponseEntity.ok(response);
    }
}