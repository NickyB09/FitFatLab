package com.fitfatlab.fitfatlab_backend.modules.schedule.controller;

import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.CreateTrainingSlotRequest;
import com.fitfatlab.fitfatlab_backend.modules.schedule.dto.TrainingSlotResponse;
import com.fitfatlab.fitfatlab_backend.modules.schedule.service.TrainingSlotService;
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
@RequestMapping("/api/v1/planning/schedule")
@RequiredArgsConstructor
@Tag(name = "Training Schedule", description = "Informative workout slots for coach-student planning")
@SecurityRequirement(name = "Bearer Authentication")
public class TrainingSlotController {

    private final TrainingSlotService trainingSlotService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "Create informative training slot")
    public ResponseEntity<TrainingSlotResponse> createSlot(
            @CurrentUser UUID coachId,
            @Valid @RequestBody CreateTrainingSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trainingSlotService.createSlot(coachId, request));
    }

    @GetMapping("/coach")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "List coach schedule slots")
    public ResponseEntity<List<TrainingSlotResponse>> getCoachSlots(@CurrentUser UUID coachId) {
        return ResponseEntity.ok(trainingSlotService.findCoachSlots(coachId));
    }

    @GetMapping("/student")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List student schedule slots")
    public ResponseEntity<List<TrainingSlotResponse>> getStudentSlots(@CurrentUser UUID studentId) {
        return ResponseEntity.ok(trainingSlotService.findStudentSlots(studentId));
    }
}
