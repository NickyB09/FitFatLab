package com.fitfatlab.fitfatlab_backend.modules.diet.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class DietDailySummary {
    private LocalDate date;
    private int totalCalories;
    private float totalProteinG;
    private float totalCarbsG;
    private float totalFatG;
    private List<DietEntryResponse> entries;
}