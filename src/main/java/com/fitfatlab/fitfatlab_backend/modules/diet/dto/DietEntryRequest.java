package com.fitfatlab.fitfatlab_backend.modules.diet.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DietEntryRequest {

    @NotBlank
    @Size(max = 200)
    private String foodName;

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