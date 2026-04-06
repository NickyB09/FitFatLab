package com.fitfatlab.fitfatlab_backend.modules.diet.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class DietEntryResponse {
    private UUID id;
    private String foodName;
    private int calories;
    private float proteinG;
    private float carbsG;
    private float fatG;
    private LocalDate entryDate;
}