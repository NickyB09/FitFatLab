package com.fitfatlab.fitfatlab_backend.modules.diet.service;

import com.fitfatlab.fitfatlab_backend.modules.diet.dto.CreateMealPlanRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanMealUpdateRequest;
import com.fitfatlab.fitfatlab_backend.modules.diet.dto.MealPlanResponse;

import java.util.List;
import java.util.UUID;

public interface MealPlanService {
    MealPlanResponse createMealPlan(UUID coachId, CreateMealPlanRequest request);
    List<MealPlanResponse> findStudentMealPlans(UUID studentId);
    List<MealPlanResponse> findCoachMealPlans(UUID coachId);
    MealPlanResponse.MealPlanMealResponse updateMealAsStudent(UUID mealId, UUID studentId, MealPlanMealUpdateRequest request);
}
