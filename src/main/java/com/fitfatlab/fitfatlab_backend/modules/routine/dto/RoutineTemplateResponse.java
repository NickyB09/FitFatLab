package com.fitfatlab.fitfatlab_backend.modules.routine.dto;

import com.fitfatlab.fitfatlab_backend.modules.routine.model.RoutineTemplateType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class RoutineTemplateResponse {
    private UUID id;
    private UUID coachId;
    private String name;
    private String description;
    private RoutineTemplateType templateType;
    private LocalDateTime createdAt;
    private List<RoutineTemplateExerciseResponse> exercises;

    @Getter
    @Builder
    public static class RoutineTemplateExerciseResponse {
        private UUID exerciseId;
        private String exerciseName;
        private int sets;
        private int reps;
        private int restSeconds;
    }
}
