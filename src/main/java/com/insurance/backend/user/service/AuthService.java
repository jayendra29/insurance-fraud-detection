package com.insurance.backend.user.service;

import com.insurance.backend.exception.ResourceAlreadyExistsException;
import com.insurance.backend.user.dto.AuthResponse;
import com.insurance.backend.user.dto.RegisterRequest;
import com.insurance.backend.user.entity.Role;
import com.insurance.backend.user.entity.User;
import com.insurance.backend.user.repository.userRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final userRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(userRepository userRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new ResourceAlreadyExistsException("Email already registered.");
        }

        if(userRepository.existsByPhoneNumber(request.getPhoneNumber())){
            throw new ResourceAlreadyExistsException("Phone Number already registerd.");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();
        userRepository.save(user);

        return AuthResponse.builder().message("User registered successfully.").build();
    }


}
