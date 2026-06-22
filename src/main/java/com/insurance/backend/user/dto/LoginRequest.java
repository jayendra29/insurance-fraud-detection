package com.insurance.backend.user.dto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    String username;
    String password;
}
