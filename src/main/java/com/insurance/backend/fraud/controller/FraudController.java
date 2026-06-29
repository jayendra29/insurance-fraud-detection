package com.insurance.backend.fraud.controller;

import com.insurance.backend.fraud.dto.FraudResponse;
import com.insurance.backend.fraud.service.FraudAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudAnalysisService fraudAnalysisService;

    @PostMapping("/analyze/{claimId}")
    public ResponseEntity<FraudResponse> analyzeClaim(@PathVariable Long claimId) {
        FraudResponse response = fraudAnalysisService.analyzeClaim(claimId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{claimId}")
    public ResponseEntity<FraudResponse> getFraudAnalysis(@PathVariable Long claimId) {
        FraudResponse response = fraudAnalysisService.getFraudAnalysis(claimId);
        return ResponseEntity.ok(response);
    }
}