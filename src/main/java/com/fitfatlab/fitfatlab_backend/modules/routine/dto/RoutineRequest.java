package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class RoutineRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @Size(max = 500)
    private String description;

    @NotEmpty(message = "A routine must have at least one exercise")
    @Valid
    private List<RoutineExerciseRequest> exercises;

    @Getter
    @Setter
    public static class RoutineExerciseRequest {
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
