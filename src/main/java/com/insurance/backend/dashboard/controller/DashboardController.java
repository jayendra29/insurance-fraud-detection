package com.insurance.backend.dashboard.controller;

import com.insurance.backend.dashboard.dto.AdminDashboardResponse;
import com.insurance.backend.dashboard.dto.UserDashboardResponse;
import com.insurance.backend.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user")
    public ResponseEntity<UserDashboardResponse> getUserDashboard() {
        UserDashboardResponse response = dashboardService.getUserDashboard();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<AdminDashboardResponse> getAdminDashboard() {
        AdminDashboardResponse response = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(response);
    }
}