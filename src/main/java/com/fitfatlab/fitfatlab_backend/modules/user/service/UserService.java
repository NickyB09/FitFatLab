package com.fitfatlab.fitfatlab_backend.modules.user.service;

import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserRegistrationRequest;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserResponse;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserUpdateRequest;
import java.util.UUID;

public interface UserService {

    UserResponse register(UserRegistrationRequest request);

    UserResponse findById(UUID id);

    UserResponse findByEmail(String email);

    java.util.List<UserResponse> findAll();

    UserResponse updateProfile(UUID id, UserUpdateRequest request);
}
