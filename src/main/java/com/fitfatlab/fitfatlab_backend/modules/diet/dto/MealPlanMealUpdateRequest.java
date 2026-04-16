package com.fitfatlab.fitfatlab_backend.modules.diet.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MealPlanMealUpdateRequest {
    @NotBlank
    @Size(max = 120)
    private String mealName;
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
