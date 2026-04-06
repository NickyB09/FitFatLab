package com.fitfatlab.fitfatlab_backend.modules.exercise.dto;

import com.fitfatlab.fitfatlab_backend.modules.exercise.model.Exercise.Difficulty;
import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class ExerciseResponse {
    private UUID id;
    private String name;
    private String muscleGroup;
    private String equipment;
    private Difficulty difficulty;
    private String description;
    private boolean global;
}