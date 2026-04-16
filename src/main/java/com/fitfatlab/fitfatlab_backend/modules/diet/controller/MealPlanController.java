package com.fitfatlab.fitfatlab_backend.modules.diet.controller;

import com.fitfatlab.fitfatlab_backend.modules.diet.dto.CreateMealPlanRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanMealUpdateRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanResponse;
import com.fitfatlab.fitfatlab_backend.modules.diet.service.MealPlanService;
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
@RequestMapping("/api/v1/planning/meals")
@RequiredArgsConstructor
@Tag(name = "Meal Planning", description = "Endpoints for coach-assigned meal plans")
@SecurityRequirement(name = "Bearer Authentication")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "Create meal plan")
    public ResponseEntity<MealPlanResponse> createMealPlan(
            @CurrentUser UUID coachId,
            @Valid @RequestBody CreateMealPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mealPlanService.createMealPlan(coachId, request));
    }

    @GetMapping("/student")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List student meal plans")
    public ResponseEntity<List<MealPlanResponse>> getStudentMealPlans(@CurrentUser UUID studentId) {
        return ResponseEntity.ok(mealPlanService.findStudentMealPlans(studentId));
    }

    @GetMapping("/coach")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    @Operation(summary = "List coach meal plans")
    public ResponseEntity<List<MealPlanResponse>> getCoachMealPlans(@CurrentUser UUID coachId) {
        return ResponseEntity.ok(mealPlanService.findCoachMealPlans(coachId));
    }

    @PatchMapping("/meals/{mealId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Student updates planned meal")
    public ResponseEntity<MealPlanResponse.MealPlanMealResponse> updateMealAsStudent(
            @PathVariable UUID mealId,
            @CurrentUser UUID studentId,
            @Valid @RequestBody MealPlanMealUpdateRequest request) {
        return ResponseEntity.ok(mealPlanService.updateMealAsStudent(mealId, studentId, request));
    }
}
