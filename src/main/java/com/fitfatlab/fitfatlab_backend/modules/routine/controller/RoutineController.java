package com.fitfatlab.fitfatlab_backend.modules.routine.controller;

import com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineRequest;
import com.fitfatlab.fitfatlab_backend.modules.routine.dto.RoutineResponse;
import com.fitfatlab.fitfatlab_backend.modules.routine.service.RoutineService;
import com.fitfatlab.fitfatlab_backend.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routines")
@RequiredArgsConstructor
@Tag(name = "Routine Management", description = "Endpoints for creating and managing workout routines")
@SecurityRequirement(name = "Bearer Authentication")
public class RoutineController {

    private final RoutineService routineService;

    @Operation(summary = "Create a workout routine", description = "Create a new routine with exercises for the current user")
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','TRAINER','ADMIN')")
    public ResponseEntity<RoutineResponse> create(
            @CurrentUser UUID userId,
            @Valid @RequestBody RoutineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(routineService.create(userId, request));
    }

    @Operation(summary = "Get my active routines", description = "Retrieve a paginated list of active routines for the current user")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<RoutineResponse>> getMyRoutines(
            @CurrentUser UUID userId,
            Pageable pageable) {
        return ResponseEntity.ok(routineService.findActiveByUser(userId, pageable));
    }

    @Operation(summary = "Get routine by ID", description = "Retrieve routine details by its UUID. Only if it belongs to the current user or user is ADMIN")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoutineResponse> getById(
            @PathVariable UUID id,
            @CurrentUser UUID userId) {
        return ResponseEntity.ok(routineService.findById(id, userId));
    }

    @Operation(summary = "Update a routine", description = "Replace the routine details and exercises for the current user")
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RoutineResponse> update(
            @PathVariable UUID id,
            @CurrentUser UUID userId,
            @Valid @RequestBody RoutineRequest request) {
        return ResponseEntity.ok(routineService.update(id, userId, request));
    }

    @Operation(summary = "Deactivate routine", description = "Soft delete a routine. Only if it belongs to the current user or user is ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            @CurrentUser UUID userId) {
        routineService.deactivate(id, userId);
        return ResponseEntity.noContent().build();
    }
}
