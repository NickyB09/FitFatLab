package com.fitfatlab.fitfatlab_backend.modules.routine.controller;

import com.fitfatlab.fitfatlab_backend.modules.routine.dto.*;
import com.fitfatlab.fitfatlab_backend.modules.routine.service.RoutinePlanningService;
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
@RequestMapping("/api/v1/planning/routines")
@RequiredArgsConstructor
@Tag(name = "Routine Planning", description = "Endpoints for reusable templates and coach-assigned routines")
@SecurityRequirement(name = "Bearer Authentication")
public class RoutinePlanningController {

    private final RoutinePlanningService routinePlanningService;

    @PostMapping("/templates")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "Create routine template")
    public ResponseEntity<RoutineTemplateResponse> createTemplate(
            @CurrentUser UUID coachId,
            @Valid @RequestBody CreateRoutineTemplateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routinePlanningService.createTemplate(coachId, request));
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "List coach routine templates")
    public ResponseEntity<List<RoutineTemplateResponse>> getTemplates(@CurrentUser UUID coachId) {
        return ResponseEntity.ok(routinePlanningService.findCoachTemplates(coachId));
    }

    @PostMapping("/assignments")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "Assign routine to student")
    public ResponseEntity<AssignedRoutineResponse> assignRoutine(
            @CurrentUser UUID coachId,
            @Valid @RequestBody AssignRoutineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routinePlanningService.assignRoutine(coachId, request));
    }

    @GetMapping("/assignments/student")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List student assignments")
    public ResponseEntity<List<AssignedRoutineResponse>> getStudentAssignments(@CurrentUser UUID studentId) {
        return ResponseEntity.ok(routinePlanningService.findStudentAssignments(studentId));
    }

    @GetMapping("/assignments/coach")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "List coach assignments")
    public ResponseEntity<List<AssignedRoutineResponse>> getCoachAssignments(@CurrentUser UUID coachId) {
        return ResponseEntity.ok(routinePlanningService.findCoachAssignments(coachId));
    }

    @PatchMapping("/assignments/{id}/complete")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Mark assignment completed")
    public ResponseEntity<AssignedRoutineResponse> completeAssignment(
            @PathVariable UUID id,
            @CurrentUser UUID studentId) {
        return ResponseEntity.ok(routinePlanningService.completeAssignment(id, studentId));
    }

    @PostMapping("/assignments/{id}/reuse")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "Reuse assignment as template or new assignment")
    public ResponseEntity<Object> reuseAssignment(
            @PathVariable UUID id,
            @CurrentUser UUID coachId,
            @Valid @RequestBody ReuseAssignedRoutineRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routinePlanningService.reuseAssignment(coachId, id, request));
    }
}
