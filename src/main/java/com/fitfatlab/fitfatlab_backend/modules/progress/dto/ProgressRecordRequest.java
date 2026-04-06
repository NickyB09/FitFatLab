package com.fitfatlab.fitfatlab_backend.modules.progress.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProgressRecordRequest {

    @DecimalMin("20.0")
    @DecimalMax("300.0")
    private Float weightKg;

    @DecimalMin("2.0")
    @DecimalMax("70.0")
    private Float bodyFatPct;
}