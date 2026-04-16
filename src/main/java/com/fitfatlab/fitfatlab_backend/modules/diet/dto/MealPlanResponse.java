package com.fitfatlab.fitfatlab_backend.modules.diet.dto;

import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlanPeriodType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class MealPlanResponse {
    private UUID id;
    private UUID coachId;
    private UUID studentId;
    private String name;
    private String description;
    private MealPlanPeriodType periodType;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean allowStudentEdits;
    private LocalDateTime createdAt;
    private List<MealPlanMealResponse> meals;

    @Getter
    @Builder
    public static class MealPlanMealResponse {
        private UUID id;
        private String mealName;
        private LocalDate plannedDate;
        private int calories;
        private float proteinG;
        private float carbsG;
        private float fatG;
    }
}
