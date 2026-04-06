package com.fitfatlab.fitfatlab_backend.modules.user.controller;


import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserRegistrationRequest;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserResponse;
import com.fitfatlab.fitfatlab_backend.modules.user.dto.UserUpdateRequest;
import com.fitfatlab.fitfatlab_backend.modules.user.service.UserService;
import com.fitfatlab.fitfatlab_backend.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user registration, listing and retrieval")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Public endpoint to register a new user in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User successfully registered"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
        @Valid @RequestBody UserRegistrationRequest request
    ) {
        UserResponse response = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe(@CurrentUser UUID currentUserId) {
        return ResponseEntity.ok(userService.findById(currentUserId));
    }

    @Operation(summary = "Update current user profile", description = "Update the current user's full name and/or password")
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> updateMe(
            @CurrentUser UUID currentUserId,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateProfile(currentUserId, request));
    }

    @Operation(summary = "List all users", description = "Retrieve a list of all registered users. Only accessible by ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @Operation(summary = "Get user by ID", description = "Retrieve user details by their UUID. Accessible by ADMIN or the owner")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == @userResolver.resolve(principal)")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }
}
