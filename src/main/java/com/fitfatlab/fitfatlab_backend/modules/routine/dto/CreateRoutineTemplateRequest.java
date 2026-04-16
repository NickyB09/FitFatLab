package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineTemplateType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateRoutineTemplateRequest {
    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull
    private RoutineTemplateType templateType;

    @NotEmpty
    @Valid
    private List<TemplateExerciseRequest> exercises;

    @Getter
    @Setter
    public static class TemplateExerciseRequest {
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
