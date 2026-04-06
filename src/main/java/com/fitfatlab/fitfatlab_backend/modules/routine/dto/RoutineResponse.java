package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RoutineResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;
    private List<RoutineExerciseDetail> exercises;

    @Getter
    @Builder
    public static class RoutineExerciseDetail {
        private UUID exerciseId;
        private String exerciseName;
        private String muscleGroup;
        private int sets;
        private int reps;
        private int restSeconds;
    }
}