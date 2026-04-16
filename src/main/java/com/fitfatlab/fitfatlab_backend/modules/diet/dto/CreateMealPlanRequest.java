package com.fitfatlab.fitfatlab_backend.modules.diet.dto;

import com.fitfatlab.fitfatlab_backend.modules.diet.model.MealPlanPeriodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateMealPlanRequest {

    @NotNull
    private UUID studentId;

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private MealPlanPeriodType periodType;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private boolean allowStudentEdits;

    @NotEmpty
    @Valid
    private List<MealRequest> meals;

    @Getter
    @Setter
    public static class MealRequest {
        @NotBlank
        @Size(max = 120)
        private String mealName;
        @NotNull
        private LocalDate plannedDate;
        @Min(0)
        @Max(10000)
        private int calories;
        @Min(0)
        private float proteinG;
        @Min(0)
        private float carbsG;
        @Min(0)
        private float fatG;
    }
}
