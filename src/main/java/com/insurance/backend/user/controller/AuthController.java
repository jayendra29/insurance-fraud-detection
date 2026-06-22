package com.insurance.backend.user.controller;

import com.insurance.backend.user.dto.AuthResponse;
import com.insurance.backend.user.dto.RegisterRequest;
import com.insurance.backend.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request){
        return authService.register(request);
    }
}
