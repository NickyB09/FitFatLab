package com.fitfatlab.fitfatlab_backend.modules.diet.controller;

import com.fitfatlab.fitfatlab_backend.modules.diet.dto.*;
import com.fitfatlab.fitfatlab_backend.modules.diet.service.DietService;
import com.fitfatlab.fitfatlab_backend.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/diet")
@RequiredArgsConstructor
@Tag(name = "Diet Management", description = "Endpoints for logging and tracking nutrition/diet")
@SecurityRequirement(name = "Bearer Authentication")
public class DietController {

    private final DietService dietService;

    @Operation(summary = "Log a diet entry", description = "Add a new food/meal entry for the currently authenticated user")
    @PostMapping("/entries")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DietEntryResponse> logEntry(
            @CurrentUser UUID userId,
            @Valid @RequestBody DietEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dietService.logEntry(userId, request));
    }

    @Operation(summary = "Get daily diet summary", description = "Retrieve nutritional summary (calories, macros) for a specific date")
    @GetMapping("/summary")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DietDailySummary> getDailySummary(
            @CurrentUser UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(dietService.getDailySummary(userId, targetDate));
    }

    @Operation(summary = "List diet entries in a range", description = "Retrieve all diet entries between two dates for the current user")
    @GetMapping("/entries")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<DietEntryResponse>> getEntries(
            @CurrentUser UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(dietService.findEntries(userId, start, end));
    }

    @Operation(summary = "Get a diet entry by ID", description = "Retrieve a single diet entry belonging to the current user")
    @GetMapping("/entries/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DietEntryResponse> getEntry(
            @CurrentUser UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(dietService.findEntry(userId, id));
    }

    @Operation(summary = "Update a diet entry", description = "Update an existing diet entry belonging to the current user")
    @PutMapping("/entries/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DietEntryResponse> updateEntry(
            @CurrentUser UUID userId,
            @PathVariable UUID id,
            @Valid @RequestBody DietEntryRequest request) {
        return ResponseEntity.ok(dietService.updateEntry(userId, id, request));
    }

    @Operation(summary = "Delete a diet entry", description = "Delete an existing diet entry belonging to the current user")
    @DeleteMapping("/entries/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEntry(
            @CurrentUser UUID userId,
            @PathVariable UUID id) {
        dietService.deleteEntry(userId, id);
        return ResponseEntity.noContent().build();
    }
}
