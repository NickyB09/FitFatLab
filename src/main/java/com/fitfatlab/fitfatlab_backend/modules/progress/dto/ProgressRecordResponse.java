package com.fitfatlab.fitfatlab_backend.modules.progress.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ProgressRecordResponse {
    private UUID id;
    private Float weightKg;
    private Float bodyFatPct;
    private LocalDate recordDate;
}