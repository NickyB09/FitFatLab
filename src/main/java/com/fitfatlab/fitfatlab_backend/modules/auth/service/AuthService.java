package com.fitfatlab.fitfatlab_backend.modules.auth.service;

import com.fitfatlab.fitfatlab_backend.modules.auth.dto.AuthResponse;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.LoginRequest;
import com.fitfatlab.fitfatlab_backend.modules.auth.dto.RefreshTokenRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout(RefreshTokenRequest request);
}
