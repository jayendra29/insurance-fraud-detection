package com.insurance.backend.user.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    private String fullName;

    private String email;

    private String phoneNumber;

    private String password;
}
