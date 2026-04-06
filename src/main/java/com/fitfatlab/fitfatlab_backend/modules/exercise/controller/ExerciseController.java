package com.fitfatlab.fitfatlab_backend.modules.exercise.controller;

import com.fitfatlab.fitfatlab_backend.modules.exercise.dto.ExerciseRequest;
import com.fitfatlab.fitfatlab_backend.modules.exercise.dto.ExerciseResponse;
import com.fitfatlab.fitfatlab_backend.modules.exercise.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercise Management", description = "Endpoints for exercises catalog and creation")
@SecurityRequirement(name = "Bearer Authentication")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @Operation(summary = "List all exercises", description = "Retrieve a paginated list of exercises. Public for authenticated users")
    @GetMapping
    public ResponseEntity<Page<ExerciseResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(exerciseService.findAll(pageable));
    }

    @Operation(summary = "Filter exercises by muscle group", description = "Retrieve a paginated list of exercises filtered by muscle group")
    @GetMapping("/by-muscle")
    public ResponseEntity<Page<ExerciseResponse>> getByMuscle(
        @RequestParam String muscleGroup,
        Pageable pageable
    ) {
        return ResponseEntity.ok(exerciseService.findByMuscleGroup(muscleGroup, pageable));
    }

    @Operation(summary = "Get exercise by ID", description = "Retrieve exercise details by its UUID")
    @GetMapping("/{id}")
    public ResponseEntity<ExerciseResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(exerciseService.findById(id));
    }

    @Operation(summary = "Create an exercise", description = "Create a new exercise. Accessible by TRAINER or ADMIN")
    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<ExerciseResponse> create(@Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(exerciseService.create(request, false));
    }

    @Operation(summary = "Create a global exercise", description = "Create a new global exercise. Only accessible by ADMIN")
    @PostMapping("/global")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseResponse> createGlobal(@Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(exerciseService.create(request, true));
    }

    @Operation(summary = "Update an exercise", description = "Update an existing exercise. Accessible by TRAINER or ADMIN")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<ExerciseResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.update(id, request));
    }

    @Operation(summary = "Delete an exercise", description = "Delete an exercise. Only accessible by ADMIN")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        exerciseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
