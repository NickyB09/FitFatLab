package com.fitfatlab.fitfatlab_backend.modules.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class UserResponse {

    private UUID id;
    private String email;
    private String fullName;
    private boolean enabled;
    private Set<String> roles; // e.g. ["ROLE_USER"]
    private LocalDateTime createdAt;
}