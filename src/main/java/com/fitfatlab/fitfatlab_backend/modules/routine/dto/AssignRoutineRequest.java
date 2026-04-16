package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.AssignedRoutinePeriodType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AssignRoutineRequest {
    @NotNull
    private UUID studentId;

    private UUID templateId;

    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private AssignedRoutinePeriodType periodType;

    @NotNull
    private LocalDate scheduledDate;

    @Valid
    private List<AssignedExerciseRequest> exercises;

    @Getter
    @Setter
    public static class AssignedExerciseRequest {
        @NotNull
        private UUID exerciseId;
        @Min(1)
        private int sets;
        @Min(1)
        private int reps;
        @Min(0)
        private int restSeconds;
    }
}
