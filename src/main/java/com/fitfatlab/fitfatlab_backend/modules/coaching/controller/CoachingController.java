package com.fitfatlab.fitfatlab_backend.modules.coaching.controller;

import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CoachStudentLinkResponse;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.CreateCoachStudentLinkRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.UpdateCoachStudentLinkStatusRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.dto.UpdateMealEditPermissionRequest;
import com.fitfatlab.fitfatlab_backend.modules.coaching.service.CoachingService;
import com.fitfatlab.fitfatlab_backend.security.CurrentUser;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/coaching")
@RequiredArgsConstructor
@Tag(name = "Coaching", description = "Endpoints for trainer-student relationships and permissions")
@SecurityRequirement(name = "Bearer Authentication")
public class CoachingController {

    private final CoachingService coachingService;

    @Operation(summary = "Invite a student", description = "Create a pending trainer-student relationship")
    @PostMapping("/relationships")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<CoachStudentLinkResponse> createInvitation(
            @CurrentUser UUID coachId,
            @Valid @RequestBody CreateCoachStudentLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coachingService.createInvitation(coachId, request));
    }

    @Operation(summary = "List my coach relationships", description = "List relationships where the authenticated user is the coach")
    @GetMapping("/relationships/coach")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<List<CoachStudentLinkResponse>> getCoachRelationships(@CurrentUser UUID coachId) {
        return ResponseEntity.ok(coachingService.findCoachLinks(coachId));
    }

    @Operation(summary = "List my student relationships", description = "List relationships where the authenticated user is the student")
    @GetMapping("/relationships/student")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CoachStudentLinkResponse>> getStudentRelationships(@CurrentUser UUID studentId) {
        return ResponseEntity.ok(coachingService.findStudentLinks(studentId));
    }

    @Operation(summary = "Update relationship status", description = "Students can accept/reject invitations and either side can end active links")
    @PatchMapping("/relationships/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CoachStudentLinkResponse> updateStatus(
            @PathVariable UUID id,
            @CurrentUser UUID actingUserId,
            @Valid @RequestBody UpdateCoachStudentLinkStatusRequest request) {
        return ResponseEntity.ok(coachingService.updateStatus(id, actingUserId, request.getStatus()));
    }

    @Operation(summary = "Update meal edit permissions", description = "Allow or deny student meal edits on an active relationship")
    @PatchMapping("/relationships/{id}/meal-permissions")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<CoachStudentLinkResponse> updateMealEditPermission(
            @PathVariable UUID id,
            @CurrentUser UUID coachId,
            @Valid @RequestBody UpdateMealEditPermissionRequest request) {
        return ResponseEntity.ok(coachingService.updateMealEditPermission(id, coachId, request));
    }
}
