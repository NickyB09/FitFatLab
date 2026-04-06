package com.fitfatlab.fitfatlab_backend.modules.exercise.dto;

import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise.Difficulty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRequest {

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotBlank
    @Size(max = 80)
    private String muscleGroup;

    @Size(max = 80)
    private String equipment;

    @NotNull
    private Difficulty difficulty;

    @Size(max = 1000)
    private String description;
}