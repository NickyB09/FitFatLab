package com.fitfatlab.fitfatlab_backend.modules.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String email;
    private String fullName;
    private Set<String> roles;
    private long expiresIn; // milisegundos hasta expiración
}
