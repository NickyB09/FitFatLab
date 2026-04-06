package com.fitfatlab.fitfatlab_backend.modules.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Size(max = 120, message = "Full name must not exceed 120 characters")
    private String fullName;
}
