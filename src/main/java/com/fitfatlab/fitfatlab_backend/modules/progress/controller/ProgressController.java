package com.fitfatlab.fitfatlab_backend.modules.progress.controller;

import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordRequest;
import com.fitfatlab.fitfatlab_backend.modules.progress.dto.ProgressRecordResponse;
import com.fitfatlab.fitfatlab_backend.modules.progress.service.ProgressService;
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
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "Progress Tracking", description = "Endpoints for logging and tracking body progress (weight, body fat)")
@SecurityRequirement(name = "Bearer Authentication")
public class ProgressController {

    private final ProgressService progressService;

    @Operation(summary = "Upsert today's progress record", description = "Add or update the progress record (weight, body fat) for the current date")
    @PutMapping("/today")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgressRecordResponse> upsertToday(
            @CurrentUser UUID userId,
            @Valid @RequestBody ProgressRecordRequest request) {
        return ResponseEntity.ok(progressService.upsertTodayRecord(userId, request));
    }

    @Operation(summary = "Get progress history", description = "Retrieve a list of progress records between two dates")
    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProgressRecordResponse>> getHistory(
            @CurrentUser UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(progressService.getHistory(userId, start, end));
    }

    @Operation(summary = "Get a progress record by ID", description = "Retrieve a single progress record belonging to the current user")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgressRecordResponse> getRecord(
            @CurrentUser UUID userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(progressService.getRecord(userId, id));
    }

    @Operation(summary = "Delete a progress record", description = "Delete a progress record belonging to the current user")
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteRecord(
            @CurrentUser UUID userId,
            @PathVariable UUID id) {
        progressService.deleteRecord(userId, id);
        return ResponseEntity.noContent().build();
    }
}
